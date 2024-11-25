package com.renogy.device.rdpdemo

import android.os.Handler
import android.os.Looper
import android.os.Message
import androidx.annotation.IntDef
import java.util.concurrent.ConcurrentHashMap

/**
 * @author Create by 17474 on 2024/2/23.
 * Email： lishuwentimor1994@163.com
 * Describe：自动轮询蓝牙命令服务
 * @param cmdList 命令列表
 * @param cmdMaxDelay 响应最大等待时间 默认2000ms，超过2s该命令将会超时，继续发送下一条指令
 * @param cmdMinDelay 响应最低等待时间 默认50ms
 * @param cmdPollingInterval 命令轮询时间间隔，默认10s，您可以根据cmdList的长短来动态设置，一般命令的发送到响应的时间为200ms
 * @param callBack 回调
 * @param cmdType 命令发送类型，单设备，多设备，仅测试
 */
class SendCmdService(
    var cmdList: MutableList<SendCmdEntity>? = null,
    var cmdMaxDelay: Long = CMD_MAX_DELAY_DEFAULT,
    var cmdMinDelay: Long = CMD_MIN_DELAY_DEFAULT,
    var cmdPollingInterval: Long = CMD_POLLING_INTERVAL_DEFAULT,
    var callBack: CallBack? = null,
    var cmdType: Int = CMD_SINGLE_DEVICE,
) {

    companion object {
        const val TAG = "SendCmdService"
        const val CMD_TIME_OUT_MSG = 0x99
        const val CMD_POLLING_INTERVAL_DEFAULT = 10 * 1000L
        const val CMD_MIN_DELAY_DEFAULT = 50L
        const val CMD_MAX_DELAY_DEFAULT = 2000L

        const val CMD_SINGLE_DEVICE = 0
        const val CMD_MULTI_DEVICE = 1
    }

    @IntDef(
        CMD_SINGLE_DEVICE, CMD_MULTI_DEVICE
    )
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    annotation class CMDType()


    private var handler = Handler(Looper.getMainLooper()) { msg ->
        handleMsg(msg)
        false
    }

    //是否是多命令
    private fun boMultiCmd(): Boolean {
        return cmdType == CMD_MULTI_DEVICE
    }

    //命令列表长度
    fun getCmdSize(): Int {
        return cmdList?.size ?: let {
            0
        }
    }

    //是否停止发送命令
    var boStop = false

    //默认都是轮询发送命令的
    var boLoop = true

    //扫描到的蓝牙设备
    private val cmdTime: MutableMap<Int, Long> = ConcurrentHashMap<Int, Long>()

    //发送指令
    internal fun startCmd() {
        this.boStop = false
        cmdList?.let {
            if (it.size > 0) {
                val startIndex = if (boMultiCmd()) getUnSkipCmd(0) else 0
                //所有的命令都超时，这时就需要进入轮询了
                if (startIndex == -2) {
                    callBack?.onInterval()
                    if (boLoop) {
                        handler.postDelayed({ startCmd() }, cmdPollingInterval)
                    }
                    return
                }
                //发送命令，开始倒计时
                callBack?.onStart(it[startIndex])
                cmdTime[startIndex] = System.currentTimeMillis()
                sendDelayMsg(startIndex, cmdMaxDelay)
            } else {
                stopCmd()
                callBack?.onError("命令列表size==0")
            }
        } ?: let {
            stopCmd()
            callBack?.onError("命令列表为空,请设置命令列表")
        }
    }

    //开始下一个命令
    internal fun startNextCmd(bleMac: String, deviceAddress: String, cmdTag: String) {
        if (boStop) {
            return
        }
        val index = indexOf(SendCmdEntity(bleMac, deviceAddress, cmdTag))
        if (index == -1) {
            stopCmd()
            callBack?.onError(
                "startNext命令列表为空请设置命令列表：$bleMac\n" +
                        "cmdTag：$cmdTag"
            )
            return
        }

        if (index == -2) {
            callBack?.onError(
                "startNext没有找到该命令：$bleMac\n" +
                        "cmdTag：$cmdTag"
            )
            return
        }
        //过滤掉响应慢的指令的影响
        val sendTimes = cmdTime[index]
        if (sendTimes != null && System.currentTimeMillis() - sendTimes.toLong() < cmdMinDelay) {
            callBack?.onError("命令响应过快，$cmdMinDelay ms内 bleMac：$bleMac\ncmdTag：$cmdTag")
            return
        }
        //所有的消息清空
        handler.removeCallbacksAndMessages(null)
        //发送指令
        sendCmd(index, false)
    }

    /**
     * 发送命令
     * @param index 当前命令所在命令列表中的位置
     * @param boTimeOut 当前命令是否超时
     * 分为两种情况，一个是多设备，一个是单设备
     * 其中多设备为了保证响应效率，提升用户体验，需要使用到"CMD_MULTI_DEVICE"多设备模式
     * 当其中的设备有超时情况存在时，我们动态设置命令的状态并跳过该设备的命令进行读取操作
     * 可以极大的提升读取设备数据的速度，给与用户更好的用户体验，
     * 该模式"CMD_MULTI_DEVICE"只会在电池级联，设备首页使用到，默认是不会开启这个功能的，
     * 因为开启这个功能会增加多次轮询造成不必要的性能消耗
     */
    private fun sendCmd(index: Int, boTimeOut: Boolean) {
        cmdList?.let {
            if (it.size > 0) {
                cmdTime.clear()
                if (boTimeOut) {
                    val element = it[index]
                    callBack?.onTimeOut(element)
                    if (boMultiCmd()) {
                        //把属于超时命令对应的设备的所有命令都设为跳过状态
                        setCmdSkip(element.getKey())
                    }
                }
                //最后一个命令，默认间隔10s再去读取数据
                if (index == it.size - 1) {
                    callBack?.onInterval()
                    if (boLoop) {
                        handler.postDelayed({ startCmd() }, cmdPollingInterval)
                    }
                    return
                }
                //根据命令类型设置index
                val nextIndex = if (boMultiCmd()) getUnSkipCmd(index + 1) else index + 1
                //所有的命令都超时，这时就需要进入轮询了
                if (nextIndex == -2) {
                    callBack?.onInterval()
                    if (boLoop) {
                        handler.postDelayed({ startCmd() }, cmdPollingInterval)
                    }
                    return
                }
                //发送命令，开始倒计时
                callBack?.onStart(it[nextIndex])
                cmdTime[nextIndex] = System.currentTimeMillis()
                sendDelayMsg(nextIndex, cmdMaxDelay)
            } else {
                stopCmd()
                callBack?.onError("sendCmd命令列表size==0")
            }
        } ?: let {
            stopCmd()
            callBack?.onError("sendCmd命令列表为空请设置命令列表")
        }
    }

    /**
     * 处理信息
     * @param msg 消息
     */
    private fun handleMsg(msg: Message) {
        when (msg.what) {
            CMD_TIME_OUT_MSG -> {
                sendCmd(msg.arg1, true)
            }

            else -> {
                callBack?.onError("没有找到msg.what=${msg.what}对应指令")
            }
        }
    }


    /**
     * 设置需要跳过的命令
     * @param key 该设备对应的命令集合
     */
    private fun setCmdSkip(key: String) {
        cmdList?.forEach {
            if (it.getKey() == key) {
                it.boSkip = true
            }
        }
    }


    /**
     * 设置所有命令状态
     * @param boSkip true 跳过, false 不跳过
     */
    private fun setCmdSkip(boSkip: Boolean) {
        cmdList?.forEach {
            it.boSkip = boSkip
        }
    }

    /**
     * 在cmdList列表中从 startIndex 位置开始获取下一个可以发送的命令
     * @param startIndex 起始位
     */
    private fun getUnSkipCmd(startIndex: Int): Int {
        cmdList?.let {
            if (it.size == 0) return -1
            for (item in startIndex until it.size) {
                if (!it[item].boSkip) {
                    return item
                }
            }
            return -2
        } ?: let {
            return -1
        }
    }


    /**
     * @param index 第几个命令
     * @param delay 超时时间
     */
    private fun sendDelayMsg(index: Int, delay: Long) {
        val msg = handler.obtainMessage(CMD_TIME_OUT_MSG)
        msg.arg1 = index
        handler.sendMessageDelayed(msg, delay)
    }


    /**
     * 获取元素所在的列表位置，不在返回-1
     * @param element 元素
     */
    private fun indexOf(element: SendCmdEntity): Int {
        cmdList?.let {
            if (it.size == 0) return -1
            for ((index, item) in it.withIndex()) {
                if (element == item) {
                    return index
                }
            }
            return -2
        } ?: let {
            return -1
        }
    }

    /**
     * @param element 校验是否存在该命令
     * @return true 存在 false 不存在
     */
    fun contains(element: SendCmdEntity): Boolean {
        return indexOf(element) > 0
    }

    //停止命令发送
    fun stopCmd() {
        this.boStop = true
        handler.removeCallbacksAndMessages(null)
        cmdTime.clear()
    }

    //手动刷新
    fun refresh() {
        stopCmd()
        setCmdSkip(boSkip = false)
        startCmd()
    }

    //是否能够刷新或者开始发送命令
    fun boRefresh(): Boolean {
        return getCmdSize() > 0 && boLoop
    }

    open class SimpleCallBack : CallBack {

        /*
          这里使用要注意代码的执行顺序，这个方法回调时并不表示所有响应的数据已经解析完毕，待优化（先解析，后发送）
         */
        override fun onInterval() {
        }

        /**
         * 服务开始发送指令
         */
        override fun onStart(sendCmdEntity: SendCmdEntity?) {
        }

        /**
         * 异常信息
         */
        override fun onError(s: String) {
        }
        /**
         * 超时的指令，当您有多个蓝牙的指令需要发送时，并且每个蓝牙都有多条指令，
         * 当其中一条蓝牙的指令超时时，您可以更新该蓝牙对应的所有指令都超时
         * 这是该服务将不会继续发送超时的指令，而是跳到下一个不超时的蓝牙指令
         * 仅测试使用
         */
        override fun onTimeOut(sendCmdEntity: SendCmdEntity?) {

        }
    }


    interface CallBack {

        //开始发送命令
        fun onStart(sendCmdEntity: SendCmdEntity?)

        //命令发送结束，等待时间
        fun onInterval()

        //设备命令超时
        fun onTimeOut(sendCmdEntity: SendCmdEntity?)

        //错误原因
        fun onError(s: String)
    }

    data class Builder(private var cmdList: MutableList<SendCmdEntity>? = null) {
        private var cmdMaxDelay: Long = CMD_MAX_DELAY_DEFAULT
        private var cmdMinDelay: Long = CMD_MIN_DELAY_DEFAULT
        private var cmdPollingInterval: Long = CMD_POLLING_INTERVAL_DEFAULT
        private var callBack: CallBack? = null
        private var cmdType: Int = CMD_SINGLE_DEVICE

        fun setCmdMaxDelay(cmdMaxDelay: Long) {
            this.cmdMaxDelay = cmdMaxDelay
        }

        fun setCmdMinDelay(cmdMinDelay: Long) {
            this.cmdMinDelay = cmdMinDelay
        }

        fun setCmdPollingInterval(cmdPollingInterval: Long) {
            this.cmdPollingInterval = cmdPollingInterval
        }

        fun setCallBack(callBack: CallBack?) {
            this.callBack = callBack
        }

        fun setCmdType(@CMDType cmdType: Int) {
            this.cmdType = cmdType
        }

        fun build(): SendCmdService {
            return SendCmdService(
                cmdList,
                cmdMaxDelay,
                cmdMinDelay,
                cmdPollingInterval,
                callBack,
                cmdType
            )
        }
    }


}