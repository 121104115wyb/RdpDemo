package com.renogy.device.rdpdemo.ui.activity

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewbinding.ViewBinding
import com.ble.utils.ToastUtil
import com.renogy.device.rdpdemo.databinding.ActivityBaseBinding
import com.renogy.device.rdpdemo.entity.EventEntity
import com.renogy.device.rdpdemo.util.EventBusUtil
import com.renogy.device.rdpdemo.util.XpoUtils

import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

/**
 * @author Create by 17474 on 2021/4/23.
 * Email： lishuwentimor1994@163.com
 * Describe：基类Activity封装
 */
abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity(), ViewBehavior {
    protected lateinit var bindView: ActivityBaseBinding
    protected lateinit var vb: VB

    companion object {
        private val STICK_BUS = Collections.synchronizedSet(HashSet<Any>())
    }

    private val loading by lazy {
        XpoUtils.showLoading(this@BaseActivity)
    }
    protected var handler = Handler(
        Looper.getMainLooper()
    ) {
        try {
            onHandlerReceived(it)
            false
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    //handler 消息需要在这里处理
    open fun onHandlerReceived(msg: Message) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initParams(intent.extras)
        if (registerEvBus() && !hasRegister()) {
            EventBusUtil.register(this)
        }
        bindView = ActivityBaseBinding.inflate(layoutInflater)
        setContentView(bindView.root)
        vb = viewBinding
        initView()
        initData()
    }

    private fun hasRegister(): Boolean {
        return EventBus.getDefault().isRegistered(this)
    }

    open fun registerEvBus(): Boolean {
        return false
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun evBus(e: EventEntity?) {
        e?.let {
            onReceiveEvent(it)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun stickEvBus(e: EventEntity?) {
        e?.let {
            STICK_BUS.add(it)
            onReceiveStickEvent(it)
        }
    }

    override fun onStop() {
        super.onStop()
        if (isFinishing) {
            onFinish()
        }
    }

    //页面正在回收
    open fun onFinish() {
        EventBusUtil.unregister(this)
        STICK_BUS.onEach {
            EventBus.getDefault().removeStickyEvent(it)
        }
    }

    /**
     * 接收事件
     *
     * @param eventEntity 传递的数据
     */
    open fun onReceiveEvent(eventEntity: EventEntity) {}

    /**
     * 接收粘性事件
     *
     * @param eventEntity 传递的数据
     */
    open fun onReceiveStickEvent(eventEntity: EventEntity) {}

    override fun showToast(s: String?) {
        if (!TextUtils.isEmpty(s)) {
            ToastUtil.show(this,s)
        }
    }

    override fun showError(s: String?) {
        if (!TextUtils.isEmpty(s)) {
            ToastUtil.show(this,s)
        }
    }

    override fun showSnack(title: String?, content: String?) {
//        CusSnackBar.createSuccess(bindView.snackView, title, content).show();
    }

    override fun showDialog(title: String?, content: String?) {
//        XpoUtils.showErrorDialog(this, title, content);
    }

    override fun showSuccess(s: String?) {
        if (!TextUtils.isEmpty(s)) {
            ToastUtil.show(this,s)
        }
    }

    override fun showEmptyUI() {}
    override fun showLoading(s: String?) {
        s?.let {
            loading.setTitle(it)
        } ?: let {
            loading.setTitle("")
        }
        if (loading.isDismiss) {
            loading.show()
        }
    }

    override fun finishLoading(delayDismiss: Long) {
        loading.delayDismiss(delayDismiss)
    }

    fun finishRefresh(delayRefresh: Long, swRef: SwipeRefreshLayout) {
        handler.postDelayed({ swRef.isRefreshing = false }, delayRefresh)
    }

    fun finishRefresh(swRef: SwipeRefreshLayout) {
        swRef.isRefreshing = false
    }

    protected open fun initParams(bundle: Bundle?){

    }

    //初始化view
    protected abstract fun initView()

    //初始化数据
    protected abstract fun initData()

    //绑定view
    protected abstract val viewBinding: VB
}