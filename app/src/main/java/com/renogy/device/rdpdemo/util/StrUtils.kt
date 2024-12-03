package com.renogy.device.rdpdemo.util

import java.util.regex.Matcher
import java.util.regex.Pattern


/**
 * @author Create by 17474 on 2024/12/3.
 * Email： lishuwentimor1994@163.com
 * Describe：工具类
 */
object StrUtils {

    fun getParamsName(s: String): String {
        if (!s.contains("#")) return s
        // 正则表达式模式
        val pattern: Pattern = Pattern.compile("#(.*?)#")
        val matcher: Matcher = pattern.matcher(s)
        if (matcher.find()) {
            return matcher.group(1) ?: s
        }
        return s
    }
}