package com.renogy.device.rdpdemo.util

import android.content.Context
import android.view.View
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.lxj.xpopup.impl.LoadingPopupView
import com.lxj.xpopup.interfaces.OnSelectListener

/**
 * @author sr
 * @Date 2023/2/26 17:45
 * @Description 显示弹窗
 */
object XpoUtils {

    /**
     * 显示无外键操作的弹窗
     * @param context 上下文
     * @param basePopupView 你的dialog
     */
    fun showWithoutDismiss(
        context: Context,
        basePopupView: BasePopupView?
    ): BasePopupView {
        return XPopup.Builder(context).autoDismiss(false).autoOpenSoftInput(false)
            .dismissOnBackPressed(false).dismissOnTouchOutside(false)
            .asCustom(basePopupView).show()
    }


    /**
     * 显示默认弹窗
     * @param context 上下文
     * @param basePopupView 你的dialog
     */
    fun showDefault(context: Context, basePopupView: BasePopupView?): BasePopupView? {
        return XPopup.Builder(context).autoDismiss(true).autoOpenSoftInput(false)
            .dismissOnBackPressed(true).dismissOnTouchOutside(true)
            .asCustom(basePopupView).show()
    }

    /**
     * 显示默认依附弹窗
     * @param context 上下文
     * @param basePopupView 你的dialog
     * @param attachView 依附的view
     */
    fun showAttach(
        context: Context, attachView: View, basePopupView: BasePopupView?
    ): BasePopupView? {
        return XPopup.Builder(context).autoDismiss(true).autoOpenSoftInput(false)
            .dismissOnBackPressed(true).dismissOnTouchOutside(true).atView(attachView)
            .asCustom(basePopupView).show()
    }

    fun showAttachView(
        context: Context,
        attachView: View,
        titles: Array<String>,
        selectListener: OnSelectListener
    ) {
        XPopup.Builder(context).atView(attachView).asAttachList(titles, null, selectListener).show()
    }

    /**
     * 加载视图
     * @param context 上下文
     */
    fun showLoading(context: Context): LoadingPopupView {
        return XPopup.Builder(context)
            .autoDismiss(true)
            .autoOpenSoftInput(false)
            .dismissOnTouchOutside(true)
            .dismissOnBackPressed(true).asLoading()
    }

    fun showCenterList(context: Context, titles: Array<String>, selectListener: OnSelectListener) {
        XPopup.Builder(context).asCenterList("", titles, selectListener).show()
    }

}