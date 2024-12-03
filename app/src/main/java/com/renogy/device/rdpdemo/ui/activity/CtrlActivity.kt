package com.renogy.device.rdpdemo.ui.activity

import android.annotation.SuppressLint
import android.text.method.ScrollingMovementMethod
import android.util.Log
import com.renogy.device.rdplibrary.ProtocolsConsts
import com.renogy.device.rdplibrary.device.anotation.DeviceType
import com.renogy.device.rdplibrary.device.ctrl.CtrlConsts
import com.renogy.device.rdplibrary.device.parse.UnitConsts
import com.renogy.device.rdplibrary.utils.ModBusUtils
import com.renogy.device.rdpdemo.R
import com.renogy.device.rdpdemo.SendCmdEntity
import com.renogy.device.rdpdemo.SendCmdService
import com.renogy.device.rdpdemo.consts.DeviceConsts
import com.renogy.device.rdpdemo.databinding.ActivityCtrlBinding
import com.renogy.device.rdpdemo.util.BtUtil
import com.renogy.device.rdpdemo.util.StrUtils


/**
 * @author Create by 17474 on 2024/11/13.
 * Email： lishuwentimor1994@163.com
 * Describe：控制器命令测试
 */
class CtrlActivity : BaseBleActivity<ActivityCtrlBinding>() {

    override val viewBinding: ActivityCtrlBinding
        get() = ActivityCtrlBinding.inflate(layoutInflater, bindView.root, true)

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

    @SuppressLint("SetTextI18n")
    override fun initData() {
        vb.multipleCommands.setOnClickListener {
            if (boCanStartMultipleCommands) {
                this.boSingleCmd = false
                vb.content.text = ""
                sendCmdService.startCmd()
            } else {
                showError(getString(R.string.wait_command_finish))
            }
        }
        vb.deviceAddress.setOnClickListener {
            this.boSingleCmd = true
            bleMac?.let {
                /**
                 * DeviceType.CTRL  the device type
                 * CtrlConsts.TOTAL_POWER_GENERATION the parameter name of the device
                 */
                val cmdEntity =
                    ProtocolsConsts.getReadCmd(DeviceType.CTRL, CtrlConsts.DEVICE_ADDRESS)
                BtUtil.instance.send(it, cmdEntity.cmd, cmdEntity.cmdTag)
            }
        }

        vb.totalPowerGeneration.setOnClickListener {
            this.boSingleCmd = true
            bleMac?.let {
                val cmdEntity =
                    ProtocolsConsts.getReadCmd(DeviceType.CTRL, CtrlConsts.TOTAL_POWER_GENERATION)
                BtUtil.instance.send(it, cmdEntity.cmd, cmdEntity.cmdTag)
            }
        }

        vb.solarVolts.setOnClickListener {
            this.boSingleCmd = true
            bleMac?.let {
                val cmdEntity =
                    ProtocolsConsts.getReadCmd(DeviceType.CTRL, CtrlConsts.SOLAR_VOLTS)
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
                CtrlConsts.TOTAL_POWER_GENERATION -> {
                    //To display the parsed data, the organization needs to add it by itself
                    vb.content.text = baseParseEntity.result() + "kwh"
                }

                CtrlConsts.DEVICE_ADDRESS -> {
                    vb.content.text = baseParseEntity.result()
                }

                CtrlConsts.SOLAR_VOLTS -> {
                    vb.content.text = baseParseEntity.result() + UnitConsts.VOLTS
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
        DeviceConsts.mTestCtrlCmdList.forEach {
            cmdList.add(SendCmdEntity(this.bleMac!!, it.cmd, it.cmdTag))
        }
        return cmdList
    }
}