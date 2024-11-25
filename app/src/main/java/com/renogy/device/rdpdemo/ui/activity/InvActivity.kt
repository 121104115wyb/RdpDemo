package com.renogy.device.rdpdemo.ui.activity
import android.annotation.SuppressLint
import android.text.method.ScrollingMovementMethod
import com.renogy.device.rdplibrary.ProtocolsConsts
import com.renogy.device.rdplibrary.device.anotation.DeviceType
import com.renogy.device.rdplibrary.device.inv.InvConsts
import com.renogy.device.rdplibrary.device.parse.UnitConsts
import com.renogy.device.rdplibrary.utils.ModBusUtils
import com.renogy.device.rdpdemo.R
import com.renogy.device.rdpdemo.SendCmdEntity
import com.renogy.device.rdpdemo.SendCmdService
import com.renogy.device.rdpdemo.consts.DeviceConsts
import com.renogy.device.rdpdemo.databinding.ActivityInvBinding
import com.renogy.device.rdpdemo.util.BtUtil

/**
 * 逆变器
 */
class InvActivity : BaseBleActivity<ActivityInvBinding>() {
    private var boCanStartMultipleCommands = true
    private var boSingleCmd = false
    override val viewBinding: ActivityInvBinding
        get() = ActivityInvBinding.inflate(layoutInflater, bindView.root, true)

    override fun initView() {
        viewBinding.content.movementMethod = ScrollingMovementMethod()
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
        vb.deviceSku.setOnClickListener {
            this.boSingleCmd = true
            bleMac?.let {
                val cmdEntity =
                    ProtocolsConsts.getReadCmd(DeviceType.INV, InvConsts.SKU)
                BtUtil.instance.send(it, cmdEntity.cmd, cmdEntity.cmdTag)
            }
        }

        vb.outputVolts.setOnClickListener {
            this.boSingleCmd = true
            bleMac?.let {
                val cmdEntity =
                    ProtocolsConsts.getReadCmd(DeviceType.INV, InvConsts.OUTPUT_VOLTS)
                BtUtil.instance.send(it, cmdEntity.cmd, cmdEntity.cmdTag)
            }
        }

        vb.outputFrequency.setOnClickListener {
            this.boSingleCmd = true
            bleMac?.let {
                val cmdEntity =
                    ProtocolsConsts.getReadCmd(DeviceType.INV, InvConsts.OUTPUT_FREQUENCY)
                BtUtil.instance.send(it, cmdEntity.cmd, cmdEntity.cmdTag)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCharChanged(mac: String, hexResp: String, cmdTag: String) {
        if (boSingleCmd){
            val rspCheck = ModBusUtils.simpleCheck(hexResp)
            if (!rspCheck) return
            val baseParseEntity = ProtocolsConsts.parseResp(cmdTag, hexResp)
            //Distinguish between different response values by unique identification
            when (cmdTag) {
                InvConsts.SKU-> {
                    //To display the parsed data, the organization needs to add it by itself
                    vb.content.text = baseParseEntity.result()
                }

                InvConsts.OUTPUT_VOLTS -> {
                    vb.content.text = baseParseEntity.result() +UnitConsts.VOLTS
                }

                InvConsts.OUTPUT_FREQUENCY -> {
                    vb.content.text = baseParseEntity.result() + UnitConsts.HZ
                }
            }
        }else {
            super.onCharChanged(mac, hexResp, cmdTag)
            //Parse the response value received from Bluetooth
            val baseParseEntity = ProtocolsConsts.parseResp(cmdTag, hexResp)
            appendText("\ncmdtag:" + cmdTag + "\nresult: ${baseParseEntity.result()}")
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
        DeviceConsts.mTestInvCmdList.forEach {
            cmdList.add(SendCmdEntity(this.bleMac!!, it.cmd, it.cmdTag))
        }
        return cmdList
    }
}