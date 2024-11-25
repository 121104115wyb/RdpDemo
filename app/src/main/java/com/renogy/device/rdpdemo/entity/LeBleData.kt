package com.renogy.device.rdpdemo.entity

/**
 * @author Create by 17474 on 2024/1/3.
 * Email： lishuwentimor1994@163.com
 * Describe：扫描到的蓝牙
 */
class LeBleData(
    var name: String,
    var mac: String,
    var rssi: Int = 0,
    var boConnected: Boolean = false
) : BaseEntity() {


    override fun equals(other: Any?): Boolean {
        return if (other is LeBleData) {
            other.mac == mac
        } else false
    }


}