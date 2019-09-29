package com.yh.atttablelib.calendarview

import com.yh.atttablelib.ext.time2Millis
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by CYH on 2019-07-18 13:32
 */

object CalendarUtil {
    
    const val SECOND_OF_DAY = 24 * 60 * 60
    const val SECOND_MILLS_OF_DAY = SECOND_OF_DAY * 1000L
    
    /**
     * 运算 calendar1 - calendar2
     * test Pass
     *
     * @param calendar1 calendar1
     * @param calendar2 calendar2
     * @return calendar1 - calendar2
     */
    @JvmStatic
    fun differ(calendar1: Calendar?, calendar2: Calendar?): Int {
        if(calendar1 == null) {
            return Integer.MIN_VALUE
        }
        if(calendar2 == null) {
            return Integer.MAX_VALUE
        }
        val date = java.util.Calendar.getInstance()
        
        date.set(calendar1.year, calendar1.month - 1, calendar1.day)//
        
        val startTimeMills = date.timeInMillis//获得起始时间戳
        
        date.set(calendar2.year, calendar2.month - 1, calendar2.day)//
        
        val endTimeMills = date.timeInMillis//获得结束时间戳
        
        return ((startTimeMills - endTimeMills) / SECOND_MILLS_OF_DAY).toInt()
    }
    
    /**
     * 获取某个日期是星期几
     * 测试通过
     *
     * @param calendar 某个日期
     * @return 返回某个日期是星期几
     */
    @JvmStatic
    fun getWeekFormCalendar(calendar: Calendar): Int {
        val date = java.util.Calendar.getInstance()
        date.set(calendar.year, calendar.month - 1, calendar.day)
        return date.get(java.util.Calendar.DAY_OF_WEEK) - 1
    }
    
    /**
     * 判断一个日期是否是周末，即周六日
     *
     * @param calendar calendar
     * @return 判断一个日期是否是周末，即周六日
     */
    @JvmStatic
    fun isWeekend(calendar: Calendar): Boolean {
        val week = getWeekFormCalendar(calendar)
        return week == 0 || week == 6
    }
    
    @JvmStatic
    fun getDate(pattern: String, date: Date): Int {
        return formatDate(date, pattern).toInt()
    }
    
    @JvmStatic
    fun formatDate(date: Date?, pattern: String = "yyyy-MM-dd HH:mm"): String {
        return formatDate(date?.time, pattern)
    }
    
    @JvmStatic
    fun formatDate(
        time: Int?, pattern: String = "yyyy-MM-dd HH:mm"
    ): String = formatDate(time?.toLong(), pattern)
    
    @JvmStatic
    fun formatDate(time: Long?): String = formatDate(time, "yyyy-MM-dd HH:mm")
    
    @JvmStatic
    fun formatDate(time: Long?, pattern: String): String {
        if(null != time && time > 0) {
            return SimpleDateFormat(pattern, Locale.CHINESE).format(time2Millis(time))
        }
        return ""
    }
}
