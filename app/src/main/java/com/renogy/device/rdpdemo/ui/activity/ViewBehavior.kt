package com.renogy.device.rdpdemo.ui.activity

//页面可能的一些交互
interface ViewBehavior {

    //提示（黑色）
    fun showToast(s: String?)
    //告警提示（红色）
    fun showError(s: String?)
    //成功提示（绿色）
    fun showSuccess(s: String?)
    fun showSnack(title: String?, content: String?)
    fun showDialog(title: String?, content: String?)
    fun showEmptyUI()
    fun showLoading(s: String?)
    fun finishLoading(delayDismiss: Long)
}