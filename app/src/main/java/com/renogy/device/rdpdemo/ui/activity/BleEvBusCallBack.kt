package com.renogy.device.rdpdemo.ui.activity

/**
 * @author Create by 17474 on 2024/1/9.
 * Email： lishuwentimor1994@163.com
 * Describe：解析处理接收到的蓝牙通讯状态
 */
interface BleEvBusCallBack {

    fun onDisconnect(mac: String)
    fun onConnect(mac: String)

    fun onConnectTimeOut(mac: String)

    fun onConnectError(mac: String)

    fun onMtuChanged(mac: String, mtu: Int, mtuStatus: Int)

    fun onCharChanged(mac: String, hexResp: String, cmdTag: String)

    fun onServiceDiscovered(mac: String)

    //蓝牙关闭
    fun onBleOff()

    //蓝牙打开
    fun onBleOn()
}