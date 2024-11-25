package com.renogy.device.rdpdemo.util

import com.renogy.device.rdpdemo.entity.EventEntity
import org.greenrobot.eventbus.EventBus

/**
 * Created by sr on 2023/03/09.
 * EventBus工具类
 */
object EventBusUtil {
    @JvmStatic
    fun register(subscriber: Any?) {
        EventBus.getDefault().register(subscriber)
    }


    @JvmStatic
    fun unregister(subscriber: Any?) {
        EventBus.getDefault().unregister(subscriber)
    }

    @JvmOverloads
    @JvmStatic
    fun postEvent(code: String, o: Any? = null) {
        EventBus.getDefault().post(EventEntity(code, o))
    }

    @JvmOverloads
    @JvmStatic
    fun postStickEvent(code: String, o: Any? = null) {
        EventBus.getDefault().postSticky(EventEntity(code, o))
    }
}