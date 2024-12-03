package com.renogy.device.rdpdemo.ui.activity

import android.annotation.SuppressLint
import android.text.method.ScrollingMovementMethod
import android.util.Log
import com.renogy.device.rdpdemo.R
import com.renogy.device.rdpdemo.SendCmdEntity
import com.renogy.device.rdpdemo.SendCmdService
import com.renogy.device.rdpdemo.consts.DeviceConsts
import com.renogy.device.rdpdemo.databinding.ActivityDccBinding
import com.renogy.device.rdpdemo.util.BtUtil
import com.renogy.device.rdpdemo.util.StrUtils
import com.renogy.device.rdplibrary.ProtocolsConsts
import com.renogy.device.rdplibrary.device.anotation.DeviceType
import com.renogy.device.rdplibrary.device.dcc.DCCConsts
import com.renogy.device.rdplibrary.device.parse.UnitConsts
import com.renogy.device.rdplibrary.utils.ModBusUtils

/**
 * @author Create by 17474 on 2024/12/3.
 * Email： lishuwentimor1994@163.com
 * Describe：DCC设备
 */
class DCCActivity : BaseBleActivity<ActivityDccBinding>() {

    override val viewBinding: ActivityDccBinding
        get() = ActivityDccBinding.inflate(layoutInflater, bindView.root, true)

    private var boCanStartMultipleCommands = true
    private var boSingleCmd = false

    override fun initView() {
        vb.content.movementMethod = ScrollingMovementMethod()
        sendCmdService.cmdList = getSendCmdEntities()
        sendCmdService.callBack = object : SendCmdService.SimpleCallBack() {
            override fun onStart(sendCmdEntity: SendCmdEntity?) {
                super.onStart(sendCmdEntity)
                boCanStartMultipleCommands = false
                sendCmdEntity?.let { cmdEntity ->
                    bleMac?.let {
                        BtUtil.instance.send(it, cmdEntity.cmd, cmdEntity.cmdTag)
                    }
                }
            }

            override fun onTimeOut(sendCmdEntity: SendCmdEntity?) {
                super.onTimeOut(sendCmdEntity)
            }

            override fun onInterval() {
                super.onInterval()
                boCanStartMultipleCommands = true
            }

            override fun onError(s: String) {
                super.onError(s)
            }
        }
        //是否轮询发送
        sendCmdService.boLoop = false
        sendCmdService.startCmd()
    }

    override fun initData() {
        vb.multipleCommands.setOnClickListener {
            if (boCanStartMultipleCommands) {
                this.boSingleCmd = false
                sendCmdService.startCmd()
            } else {
                showError(getString(R.string.wait_command_finish))
            }
        }
        vb.deviceAddress.setOnClickListener {
            this.boSingleCmd = true
            bleMac?.let {
                val cmdEntity =
                    ProtocolsConsts.getReadCmd(DeviceType.DCC, DCCConsts.DEVICE_ADDRESS)
                BtUtil.instance.send(it, cmdEntity.cmd, cmdEntity.cmdTag)
            }
        }

        vb.auxiliaryBatVolts.setOnClickListener {
            this.boSingleCmd = true
            bleMac?.let {
                val cmdEntity =
                    ProtocolsConsts.getReadCmd(DeviceType.DCC, DCCConsts.AUXILIARY_BATTERY_VOLTS)
                BtUtil.instance.send(it, cmdEntity.cmd, cmdEntity.cmdTag)
            }
        }

        vb.auxiliaryBatChargeAmps.setOnClickListener {
            this.boSingleCmd = true
            bleMac?.let {
                val cmdEntity =
                    ProtocolsConsts.getReadCmd(
                        DeviceType.DCC,
                        DCCConsts.AUXILIARY_BATTERY_CHARGE_AMPS
                    )
                BtUtil.instance.send(it, cmdEntity.cmd, cmdEntity.cmdTag)
            }
        }

        vb.auxiliaryBatChargeWatts.setOnClickListener {
            this.boSingleCmd = true
            bleMac?.let {
                val cmdEntity =
                    ProtocolsConsts.getReadCmd(
                        DeviceType.DCC,
                        DCCConsts.AUXILIARY_BATTERY_CHARGE_WATTS
                    )
                BtUtil.instance.send(it, cmdEntity.cmd, cmdEntity.cmdTag)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCharChanged(mac: String, hexResp: String, cmdTag: String) {
        if (boSingleCmd) {
            val rspCheck = ModBusUtils.simpleCheck(hexResp)
            if (!rspCheck) return
            val baseParseEntity = ProtocolsConsts.parseResp(cmdTag, hexResp)
            //Distinguish between different response values by unique identification
            when (cmdTag) {
                DCCConsts.AUXILIARY_BATTERY_VOLTS -> {
                    //To display the parsed data, the organization needs to add it by itself
                    vb.content.text = baseParseEntity.result() + UnitConsts.VOLTS
                }

                DCCConsts.DEVICE_ADDRESS -> {
                    vb.content.text = baseParseEntity.result()
                }

                DCCConsts.AUXILIARY_BATTERY_CHARGE_AMPS -> {
                    vb.content.text = baseParseEntity.result() + UnitConsts.AMPS
                }

                DCCConsts.AUXILIARY_BATTERY_CHARGE_WATTS -> {
                    vb.content.text = baseParseEntity.result() + UnitConsts.KWH
                }
            }
        } else {
            super.onCharChanged(mac, hexResp, cmdTag)
            //Parse the response value received from Bluetooth
            val baseParseEntity = ProtocolsConsts.parseResp(cmdTag, hexResp)
            Log.d("testCmdTag", "cmdTag:$cmdTag")
            appendText(
                "\nparamsName:" + StrUtils.getParamsName(cmdTag)
                        + "\nresult: ${baseParseEntity.result()}"
            )
        }
    }

    @SuppressLint("SetTextI18n")
    private fun appendText(s: String) {
        val oldText = vb.content.text.toString()
        vb.content.text = oldText + s
    }

    //发送命令的列表
    private fun getSendCmdEntities(): MutableList<SendCmdEntity> {
        if (this.bleMac == null) return mutableListOf()
        val cmdList = mutableListOf<SendCmdEntity>()
        DeviceConsts.mTestDCCCmdList.forEach {
            cmdList.add(SendCmdEntity(this.bleMac!!, it.cmd, it.cmdTag))
        }
        return cmdList
    }
}