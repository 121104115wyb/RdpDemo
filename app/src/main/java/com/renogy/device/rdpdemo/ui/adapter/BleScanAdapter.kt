package com.renogy.device.rdpdemo.ui.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.renogy.device.rdpdemo.R
import com.renogy.device.rdpdemo.entity.LeBleData
import com.renogy.device.rdpdemo.consts.DeviceConsts
import kotlin.math.abs

/**
 * @author Create by 17474 on 2024/1/3.
 * Email： lishuwentimor1994@163.com
 * Describe：蓝牙适配器
 */
class BleScanAdapter : BaseQuickAdapter<LeBleData, BaseViewHolder>(R.layout.scan_ble_item) {

    override fun convert(holder: BaseViewHolder, item: LeBleData) {
        holder.setText(R.id.name, "name：${item.name}").setText(R.id.mac, "mac：${item.mac}")
            .setText(R.id.rssi, "rssi：${abs(item.rssi)}")
            .setText(R.id.connect, if (item.boConnected) "DisConnect" else "Connect")
    }

    fun connected(mac: String) {
        for ((index, item) in data.withIndex()) {
            if (item.mac == mac) {
                DeviceConsts.addBle(mac, item.name)
                item.boConnected = true
                notifyItemChanged(index)
                break
            }
        }
    }


    fun disconnect(mac: String) {
        for ((index, item) in data.withIndex()) {
            if (item.mac == mac) {
                item.boConnected = false
                notifyItemChanged(index)
                break
            }
        }
    }


    //是否已经有连接的蓝牙
    fun hasConnected(): Boolean {
        data.onEach {
            if (it.boConnected) return true
        }
        return false
    }

    fun clear() {
        if (data.isEmpty()) return
        var cntItem: LeBleData? = null
        for (item in data) {
            if (item.boConnected) {
                cntItem = item
                break
            }
        }
        setList(if (cntItem != null) mutableListOf(cntItem) else mutableListOf())
    }

    fun addBle(item: LeBleData) {
        if (data.size > 0 && data.contains(item)) return
        addData(item)
    }
}