package com.renogy.device.rdpdemo.ui

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Build
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ble.ble.scan.LeScanResult
import com.ble.ble.scan.LeScanner
import com.ble.ble.scan.OnLeScanListener
import com.ble.ble.scan.ScanPermissionRequest
import com.ble.ble.scan.ScanRequestCallback
import com.renogy.device.rdplibrary.device.BleConsts
import com.renogy.device.rdplibrary.device.anotation.DeviceType
import com.renogy.device.rdpdemo.R
import com.renogy.device.rdpdemo.entity.LeBleData
import com.renogy.device.rdpdemo.ui.activity.BaseHomeActivity
import com.renogy.device.rdpdemo.ui.adapter.BleScanAdapter
import com.renogy.device.rdpdemo.util.BtUtil
import com.renogy.device.rdpdemo.util.XpoUtils
import com.renogy.device.rdpdemo.consts.DeviceConsts
import com.renogy.device.rdpdemo.consts.DeviceConsts.getDeviceTypeArray
import com.renogy.device.rdpdemo.ui.activity.CtrlActivity
import com.renogy.device.rdpdemo.ui.activity.InvActivity
import com.ttcble.leui.LeProxy

/**
 * @author Create by 17474 on 2024/11/13.
 * Email： lishuwentimor1994@163.com
 * Describe：首页
 */
class HomeActivity : BaseHomeActivity(), OnLeScanListener {

    private lateinit var bleScanAdapter: BleScanAdapter

    override fun registerEvBus(): Boolean {
        return true
    }

    override fun initView() {
        super.initView()
        bleScanAdapter = BleScanAdapter()
        bleScanAdapter.addChildClickViewIds(R.id.connect)
        vb.bleRlv.layoutManager =
            LinearLayoutManager(this@HomeActivity, RecyclerView.VERTICAL, false)
        vb.bleRlv.adapter = bleScanAdapter
        vb.smf.setOnRefreshListener {
            scanBle()
        }
        scanBle()
    }

    override fun initData() {
        super.initData()
        bleScanAdapter.setOnItemClickListener { a, v, p ->
            val item = bleScanAdapter.getItem(p)
            if (!item.boConnected) {
                showError(getString(R.string.connect_ble))
                return@setOnItemClickListener
            }
            showDeviceList(item.mac)
        }

        bleScanAdapter.setOnItemChildClickListener { adapter, view, position ->
            if (view.id == R.id.connect) {
                val item = bleScanAdapter.getItem(position)
                if (item.boConnected) {
                    LeProxy.instance.disconnect(item.mac)
                } else {
                    showLoading("connecting")
                    LeProxy.instance.connect(item.mac)
                }
            }
        }
    }

    private fun scanBle() {
        LeScanner.requestScan(this, REQ_SCAN_DEVICE, object : ScanRequestCallback() {

            override fun onReady() {
                super.onReady()
                bleScanAdapter.clear()
                LeScanner.startScan(this@HomeActivity)
            }

            override fun onLocationServiceDisabled() {
                super.onLocationServiceDisabled()
                showLocationServiceDialog()
            }

            override fun onPermissionDenied() {
                super.onPermissionDenied()
                showPermissionDialog { _, _ ->
                    LeScanner.startAppDetailsActivity(this@HomeActivity)
                }
            }

            override fun shouldShowPermissionRationale(request: ScanPermissionRequest) {
                super.shouldShowPermissionRationale(request)
                showPermissionDialog { _, _ ->
                    request.proceed()
                }
            }

            override fun onBluetoothDisabled() {
                super.onBluetoothDisabled()
                LeScanner.requestEnableBluetooth(this@HomeActivity)
            }
        })
    }

    private fun showPermissionDialog(listener: DialogInterface.OnClickListener) {
        val msg = if (Build.VERSION.SDK_INT < 31) {
            R.string.scan_tips_no_location_permission
        } else {
            R.string.scan_tips_no_location_permission
        }
        AlertDialog.Builder(this).setCancelable(false).setMessage(msg)
            .setPositiveButton(R.string.proceed, listener)
            .setNegativeButton(R.string.cancel) { _, _ -> /**/ }.show()
    }

    //Android12以下系统需要开启位置服务
    private fun showLocationServiceDialog() {
        AlertDialog.Builder(this).setCancelable(false)
            .setMessage(R.string.scan_tips_location_service_disabled)
            .setPositiveButton(R.string.proceed) { _, _ ->
                LeScanner.requestEnableLocation(
                    this@HomeActivity
                )
            }.setNegativeButton(R.string.cancel) { _, _ -> /**/ }.show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        LeScanner.onRequestPermissionsResult(this, requestCode, permissions, grantResults)
    }

    companion object {
        private const val TAG = "HomeActivity"

        private const val REQ_SCAN_DEVICE = 11
    }

    override fun onScanStart() {
        runOnUiThread {
            vb.smf.isRefreshing = true
        }
    }

    @SuppressLint("MissingPermission")
    override fun onLeScan(scannerResult: LeScanResult?) {
        runOnUiThread {
            scannerResult?.let {
                val name = it.device.name
                val mac = it.device.address
                val rssi = it.rssi
                if (BleConsts.bleContains(name)) {
                    bleScanAdapter.addBle(LeBleData(name, mac, rssi, false))
                }
            }
        }
    }

    override fun onScanFailed(p0: Int) {
        showError("scan failed :$p0")
    }

    override fun onScanStop() {
        runOnUiThread {
            vb.smf.isRefreshing = false
        }
    }

    override fun onPause() {
        super.onPause()
        LeScanner.stopScan()
    }


    override fun onDisconnect(mac: String) {
        super.onDisconnect(mac)
        finishLoading(0)
        bleScanAdapter.disconnect(mac)
        showError(mac + getString(R.string.scan_disconnected))
    }

    override fun onConnectError(mac: String) {
        super.onConnectError(mac)
        finishLoading(0)
        bleScanAdapter.disconnect(mac)
        showError(mac + getString(R.string.scan_connection_error))
    }

    override fun onConnectTimeOut(mac: String) {
        super.onConnectTimeOut(mac)
        finishLoading(0)
        bleScanAdapter.disconnect(mac)
        showError(mac + getString(R.string.scan_connect_timeout))
    }


    override fun onServiceDiscovered(mac: String) {
        super.onServiceDiscovered(mac)
        mHandler.postDelayed({ BtUtil.instance.updateOtaMtu(mac) }, 200)
        bleScanAdapter.connected(mac)
        showToast("$mac connect success!")
    }

    override fun onMtuChanged(mac: String, mtu: Int, mtuStatus: Int) {
        super.onMtuChanged(mac, mtu, mtuStatus)
        finishLoading(0)
    }


    /**
     * 显示设备类型弹窗
     */
    private fun showDeviceList(bleMac: String) {
        val bleName = DeviceConsts.getBleName(bleMac)
        XpoUtils.showCenterList(
            this@HomeActivity, getDeviceTypeArray()
        ) { position, text ->

            when (DeviceConsts.deviceList[position].hexTag) {
                DeviceType.CTRL -> {
                    startTargetActy(bleMac, this@HomeActivity, CtrlActivity::class.java)
                }

                DeviceType.INV -> {
                    startTargetActy(bleMac, this@HomeActivity, InvActivity::class.java)
                }

                DeviceType.BAT -> {

                }
            }
        }
    }

    override fun onBleOff() {
        super.onBleOff()
        bleScanAdapter.clear()
        vb.smf.isRefreshing = false
        finishLoading(0)
        showError(getString(R.string.scan_bt_disabled))
    }

    override fun onBleOn() {
        super.onBleOn()
        scanBle()
    }
}