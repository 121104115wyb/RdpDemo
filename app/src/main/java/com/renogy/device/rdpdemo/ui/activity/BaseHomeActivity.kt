package com.renogy.device.rdpdemo.ui.activity

import android.bluetooth.BluetoothAdapter
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import com.ble.ble.BleService
import com.renogy.device.rdpdemo.databinding.ActivityHomeBinding
import com.renogy.device.rdpdemo.receiver.BlueToothReceiver
import com.renogy.device.rdpdemo.util.BtUtil

/**
 * @author Create by 17474 on 2024/1/3.
 * Email： lishuwentimor1994@163.com
 * Describe：首页
 */
open class BaseHomeActivity : BaseBleActivity<ActivityHomeBinding>() {

    override val viewBinding: ActivityHomeBinding
        get() = ActivityHomeBinding.inflate(layoutInflater, bindView.root, true)
    protected val mHandler = Handler(Looper.getMainLooper())

    override fun initView() {
        bindService(Intent(this, BleService::class.java), mConnection, Context.BIND_AUTO_CREATE)
        BlueToothReceiver.getInstance().registerBleReceiver(this, receiverListener)
    }

    override fun registerEvBus(): Boolean {
        return true
    }

    override fun initData() {

    }


    private val mConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            BtUtil.instance.setBleService(service)
        }

        override fun onServiceDisconnected(name: ComponentName) {
        }
    }

    //监听蓝牙的状态来处理所有蓝牙问题
    private val receiverListener =
        BlueToothReceiver.ReceiverListener { _: Context?, intent: Intent ->
            try {
                val action = intent.action
                if (BluetoothAdapter.ACTION_STATE_CHANGED == action) {
                    val status =
                        intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1)
                    //蓝牙开关状态
                    BtUtil.instance.updateBleStatus(status)
                } else if (BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED == action) {

                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(mConnection)
        BlueToothReceiver.getInstance().unregisterBleReceiver(this)
    }

    override fun onDisconnect(mac: String) {
        
    }

    override fun onConnect(mac: String) {
        
    }

    override fun onConnectTimeOut(mac: String) {
        
    }

    override fun onConnectError(mac: String) {
        
    }

    override fun onMtuChanged(mac: String, mtu: Int, mtuStatus: Int) {
        
    }

    override fun onCharChanged(mac: String, hexResp: String, cmdTag: String) {
        
    }

    override fun onServiceDiscovered(mac: String) {
        
    }

    override fun onBleOff() {
        
    }

    override fun onBleOn() {
        
    }
}