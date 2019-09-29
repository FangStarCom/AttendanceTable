package com.yh.atttablelib.ext

import android.text.TextUtils
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by CYH on 2019-09-27 17:11
 */

fun getAMorPM(timeStr: String?): String? {
    if(TextUtils.isEmpty(timeStr)) {
        return null
    }
    val time = time2Millis(timeStr)
    if(time <= 0) {
        return null
    }
    return getAMorPM(time)
}

fun getAMorPM(time: Long): String {
    val calendar = Calendar.getInstance(Locale.CHINESE)
    calendar.timeInMillis = time
    if(Calendar.AM == calendar.get(Calendar.AM_PM)) {
        return "上午"
    }
    return "下午"
}

fun getWeek(millis: Long): String {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = millis
    return when(calendar.get(Calendar.DAY_OF_WEEK)) {
        1 -> "日"
        2 -> "一"
        3 -> "二"
        4 -> "三"
        5 -> "四"
        6 -> "五"
        7 -> "六"
        else -> ""
    }
}

private val NUMBER_STR = arrayOf("零", "一", "二", "三", "四", "五", "六", "七", "八", "九")
fun number2String(number: Int): String {
    return NUMBER_STR[number % NUMBER_STR.size]
}

fun getSecond(time: Long): Int = (time / (if(time < 9999999999L) 1L else 1000L)).toInt()

fun getCurSecond() = (System.currentTimeMillis() / 1000L).toInt()

fun time2Millis(time: String?): Long {
    if(time?.isEmpty() != false) {
        return 0
    }
    return time2Millis(time.toLong())
}

fun time2Millis(time: Long): Long = if(time < 9999999999L) 1000L * time else time

fun formatDate(
    time: String?, pattern: String = "yyyy-MM-dd HH:mm"
): String = formatDate(time2Millis(time), pattern)

fun formatDate(
    time: Int?, pattern: String = "yyyy-MM-dd HH:mm"
): String = formatDate(time?.toLong(), pattern)

fun formatDate(time: Long?): String = formatDate(time, "yyyy-MM-dd HH:mm")

fun formatDate(time: Long?, pattern: String): String {
    if(null != time && time > 0) {
        return SimpleDateFormat(pattern, Locale.CHINESE).format(time2Millis(time))
    }
    return ""
}

fun parseDate(
    str: String?, pattern: String = "yyyy-MM-dd HH:mm:ss"
): Calendar? {
    val cal = Calendar.getInstance()
    if(TextUtils.isEmpty(str)) {
        return null
    }
    val sdf = SimpleDateFormat(pattern, Locale.CHINESE)
    cal.time = sdf.parse(str)
    return cal
}
