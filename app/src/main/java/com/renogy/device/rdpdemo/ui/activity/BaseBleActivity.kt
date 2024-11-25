package com.renogy.device.rdpdemo.ui.activity

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.viewbinding.ViewBinding
import com.renogy.device.rdplibrary.utils.ModBusUtils
import com.renogy.device.rdpdemo.SendCmdService
import com.renogy.device.rdpdemo.entity.EvBusEntity
import com.renogy.device.rdpdemo.entity.EventEntity
import com.renogy.device.rdpdemo.util.BtUtil

/**
 * @author Create by 17474 on 2024/1/9.
 * Email： lishuwentimor1994@163.com
 * Describe：蓝牙事务
 */
abstract class BaseBleActivity<VB : ViewBinding> : BaseActivity<VB>(), BleEvBusCallBack {

    protected var bleMac: String? = null

    protected val sendCmdService by lazy {
        SendCmdService()
    }

    //蓝牙是否关闭，默认没有关闭,可以使用到设备页详情页重连
    protected var bleClosed = false

    //蓝牙断开
    protected var bleDisconnected = false

    protected fun boCanRefresh(): Boolean {
        return !bleClosed && !bleDisconnected
    }

    override fun registerEvBus(): Boolean {
        return true
    }


    override fun onReceiveEvent(eventEntity: EventEntity) {
        super.onReceiveEvent(eventEntity)
        val code = eventEntity.code
        if (code != BtUtil.CODE) {
            onOtherEvent(eventEntity)
            return
        }
        try {
            val data = eventEntity.data as EvBusEntity
            val action = data.action
            val mac = data.bleMac
            when (action) {
                BtUtil.ACTION_GATT_CONNECTED -> onConnect(mac)
                BtUtil.ACTION_CONNECT_TIMEOUT -> onConnectTimeOut(mac)
                BtUtil.ACTION_CONNECT_ERROR -> onConnectError(mac)
                BtUtil.ACTION_GATT_DISCONNECTED -> onDisconnect(mac)
                BtUtil.ACTION_GATT_SERVICES_DISCOVERED -> onServiceDiscovered(mac)
                BtUtil.ACTION_DATA_AVAILABLE -> {
                    val hexResp = data.hexResp
                    val cmdTag = data.cmdTag
                    onCharChanged(mac, hexResp, cmdTag)
                }

                BtUtil.ACTION_MTU_CHANGED -> {
                    val mtu = data.mtu
                    val mtuStatus = data.mtuStatus
                    onMtuChanged(mac, mtu, mtuStatus)
                }

                BtUtil.BLE_STATUS -> {
                    val status = data.bleState
                    if (status == BluetoothAdapter.STATE_ON) {
                        onBleOn()
                    } else if (status == BluetoothAdapter.STATE_OFF) {
                        onBleOff()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    //其它的非蓝牙的事务
    open fun onOtherEvent(eventEntity: EventEntity) {

    }

    override fun onDisconnect(mac: String) {
        if (TextUtils.isEmpty(mac) || this.bleMac != mac) return
        this.bleMac = ""
        this.bleDisconnected = true
        if (!sendCmdService.boStop) {
            sendCmdService.stopCmd()
        }
    }

    override fun onConnect(mac: String) {
    }

    override fun onConnectTimeOut(mac: String) {
    }

    override fun onConnectError(mac: String) {
        if (TextUtils.isEmpty(mac) || this.bleMac != mac) return
        this.bleMac = ""
        this.bleDisconnected = true
        if (!sendCmdService.boStop) {
            sendCmdService.stopCmd()
        }
    }

    override fun onMtuChanged(mac: String, mtu: Int, mtuStatus: Int) {
    }

    override fun onPause() {
        super.onPause()
        if (!sendCmdService.boStop) {
            sendCmdService.stopCmd()
        }
    }

    override fun onRestart() {
        super.onRestart()
        if (sendCmdService.boRefresh() && boCanRefresh()) {
            sendCmdService.startCmd()
        }
    }

    override fun onCharChanged(mac: String, hexResp: String, cmdTag: String) {
        val rspCheck = ModBusUtils.simpleCheck(hexResp)
        if (!rspCheck) return
        if (!sendCmdService.boStop) {
            sendCmdService.startNextCmd(mac, hexResp.substring(0, 2), cmdTag)
        }
    }

    override fun onServiceDiscovered(mac: String) {
        this.bleMac = mac
    }

    override fun onBleOff() {
        this.bleMac = ""
        this.bleClosed = true
        if (!sendCmdService.boStop) {
            sendCmdService.stopCmd()
        }
    }

    override fun onBleOn() {
        this.bleClosed = false
    }

    override fun initParams(bundle: Bundle?) {
        super.initParams(bundle)
        bundle?.let {
            this.bleMac = it.getString(BLE_MAC)
        }
    }

    companion object {
        const val BLE_MAC = "ble_mac"

        fun startTargetActy(bleMac: String, context: Context, clz: Class<out Activity?>) {
            val intent = Intent()
            val extras = Bundle()
            extras.putString(BLE_MAC, bleMac)
            intent.putExtras(extras)
            intent.setComponent(ComponentName(context.packageName, clz.name))
            context.startActivity(intent)
        }
    }

}