package com.renogy.device.rdpdemo.consts

import com.renogy.device.rdplibrary.ProtocolsConsts
import com.renogy.device.rdplibrary.device.anotation.DeviceType
import com.renogy.device.rdplibrary.device.bat.BatConsts
import com.renogy.device.rdplibrary.device.ctrl.CtrlConsts
import com.renogy.device.rdplibrary.device.dcc.DCCConsts
import com.renogy.device.rdplibrary.device.entity.BaseSelectEntity
import com.renogy.device.rdplibrary.device.inv.InvConsts

/**
 * @author Create by 17474 on 2024/1/4.
 * Email： lishuwentimor1994@163.com
 * Describe：设备常量
 */
object DeviceConsts {

    const val RNG_PRO = "RNGPRO"
    val mTestCtrlCmdList = mutableListOf(
        ProtocolsConsts.getReadCmd(DeviceType.CTRL, CtrlConsts.DEVICE_ADDRESS),
        ProtocolsConsts.getReadCmd(DeviceType.CTRL, CtrlConsts.SKU),
        ProtocolsConsts.getReadCmd(DeviceType.CTRL, CtrlConsts.FIRMWARE_VERSION),
        ProtocolsConsts.getReadCmd(DeviceType.CTRL, CtrlConsts.ESTIMATED_SOC),
        ProtocolsConsts.getReadCmd(DeviceType.CTRL, CtrlConsts.BATTERY_CHARGING_VOLTS),
        ProtocolsConsts.getReadCmd(DeviceType.CTRL, CtrlConsts.BATTERY_CHARGING_AMPS),
        ProtocolsConsts.getReadCmd(DeviceType.CTRL, CtrlConsts.CTRL_TEMP),
        ProtocolsConsts.getReadCmd(DeviceType.CTRL, CtrlConsts.BATTERY_TEMP),
        ProtocolsConsts.getReadCmd(DeviceType.CTRL, CtrlConsts.SOLAR_VOLTS),
        ProtocolsConsts.getReadCmd(DeviceType.CTRL, CtrlConsts.SOLAR_AMPS),
        ProtocolsConsts.getReadCmd(DeviceType.CTRL, CtrlConsts.TOTAL_POWER_GENERATION),
        ProtocolsConsts.getReadCmd(DeviceType.CTRL, CtrlConsts.CHARGING_STATUS),
        ProtocolsConsts.getReadCmd(DeviceType.CTRL, CtrlConsts.BATTERY_TYPE),
        ProtocolsConsts.getReadCmd(DeviceType.CTRL, CtrlConsts.SYSTEM_VOLTS)
    )

    val mTestBatCmdList = mutableListOf(
        ProtocolsConsts.getReadCmd(DeviceType.BAT, BatConsts.DEVICE_ADDRESS),
        ProtocolsConsts.getReadCmd(DeviceType.BAT, BatConsts.SKU),
        ProtocolsConsts.getReadCmd(DeviceType.BAT, BatConsts.FIRMWARE_VERSION),
        ProtocolsConsts.getReadCmd(DeviceType.BAT, BatConsts.BATTERY_VOLTS),
        ProtocolsConsts.getReadCmd(DeviceType.BAT, BatConsts.BATTERY_AMPS),
        ProtocolsConsts.getReadCmd(DeviceType.BAT, BatConsts.REMAIN_CAPACITY),
        ProtocolsConsts.getReadCmd(DeviceType.BAT, BatConsts.TOTAL_CAPACITY),
        ProtocolsConsts.getReadCmd(DeviceType.BAT, BatConsts.BATTERY_CELLS_NUMBER),
        ProtocolsConsts.getReadCmd(DeviceType.BAT, BatConsts.BATTERY_TEMPERATURE_CELLS_NUMBER),
        ProtocolsConsts.getReadCmd(DeviceType.BAT, BatConsts.BATTERY_CELLS_VOLTS),
        ProtocolsConsts.getReadCmd(DeviceType.BAT, BatConsts.BATTERY_CELLS_TEMPERATURE),
        ProtocolsConsts.getReadCmd(DeviceType.BAT, BatConsts.HEATER_MODE_STATUS)
    )

    val mTestDCCCmdList = mutableListOf(
        ProtocolsConsts.getReadCmd(DeviceType.DCC, DCCConsts.DEVICE_ADDRESS),
        ProtocolsConsts.getReadCmd(DeviceType.DCC, DCCConsts.SKU),
        ProtocolsConsts.getReadCmd(DeviceType.DCC, DCCConsts.BATTERY_TYPE),
        ProtocolsConsts.getReadCmd(DeviceType.DCC, DCCConsts.SYSTEM_VOLTS),
        ProtocolsConsts.getReadCmd(DeviceType.DCC, DCCConsts.CHARGE_STATUS),
        ProtocolsConsts.getReadCmd(DeviceType.DCC, DCCConsts.STARTER_BATTERY_VOLTS),
        ProtocolsConsts.getReadCmd(DeviceType.DCC, DCCConsts.STARTER_BATTERY_AMPS),
        ProtocolsConsts.getReadCmd(DeviceType.DCC, DCCConsts.SOLAR_CHARGE_VOLTS),
        ProtocolsConsts.getReadCmd(DeviceType.DCC, DCCConsts.SOLAR_CHARGE_AMPS),
        ProtocolsConsts.getReadCmd(DeviceType.DCC, DCCConsts.TOTAL_KWH_GENERATED),
        ProtocolsConsts.getReadCmd(DeviceType.DCC, DCCConsts.AUXILIARY_BATTERY_TEMPERATURE),
        ProtocolsConsts.getReadCmd(DeviceType.DCC, DCCConsts.DC_DC_MPPT_TEMPERATURE),
        ProtocolsConsts.getReadCmd(DeviceType.DCC, DCCConsts.MAX_CHARGE_AMPS),
        ProtocolsConsts.getReadCmd(DeviceType.DCC, DCCConsts.BOOST_VOLTS)
    )

    val mTestInvCmdList = mutableListOf(
        ProtocolsConsts.getReadCmd(DeviceType.INV, InvConsts.DEVICE_ADDRESS),
        ProtocolsConsts.getReadCmd(DeviceType.INV, InvConsts.SKU),
        ProtocolsConsts.getReadCmd(DeviceType.INV, InvConsts.SN),
        ProtocolsConsts.getReadCmd(DeviceType.INV, InvConsts.FIRMWARE_VERSION),
        ProtocolsConsts.getReadCmd(DeviceType.INV, InvConsts.OUTPUT_VOLTS),
        ProtocolsConsts.getReadCmd(DeviceType.INV, InvConsts.OUTPUT_AMPS),
        ProtocolsConsts.getReadCmd(DeviceType.INV, InvConsts.OUTPUT_FREQUENCY),
        ProtocolsConsts.getReadCmd(DeviceType.INV, InvConsts.BATTERY_VOLTS),
        ProtocolsConsts.getReadCmd(DeviceType.INV, InvConsts.DEVICE_TEMPERATURE),
        ProtocolsConsts.getReadCmd(DeviceType.INV, InvConsts.POWER_SAVING_MODE),
        ProtocolsConsts.getReadCmd(DeviceType.INV, InvConsts.BEEP_SWITCH),
        ProtocolsConsts.getReadCmd(DeviceType.INV, InvConsts.NG_BONDING_ENABLE)
    )

    val deviceList = mutableListOf(
        BaseSelectEntity.createNormal("Controller", false, DeviceType.CTRL),
        BaseSelectEntity.createNormal("Inverter", false, DeviceType.INV),
        BaseSelectEntity.createNormal("Battery", false, DeviceType.BAT),
        BaseSelectEntity.createNormal("DCC", false, DeviceType.DCC)
    )


    val BLE_MAP = HashMap<String, String>()

    fun addBle(mac: String, bleName: String) {
        BLE_MAP[mac] = bleName
    }

    fun getBleName(mac: String): String {
        return BLE_MAP.getOrDefault(mac, "")
    }

    //设备类型
    fun getDeviceTypeArray(): Array<String> {
        val typeList = mutableListOf<String>()
        for (item in deviceList.indices) {
            typeList.add(deviceList[item].showTitle())
        }
        return typeList.toTypedArray()
    }


    fun boRngPro(bleName: String): Boolean {
        return bleName.startsWith(RNG_PRO)
    }

}