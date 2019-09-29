package com.yh.attendancetable

import android.app.Activity
import android.os.Bundle
import android.text.TextUtils
import com.yh.atttablelib.AttTableStatus
import com.yh.atttablelib.calendarview.Calendar
import com.yh.atttablelib.calendarview.CalendarUtil
import com.yh.atttablelib.ext.formatDate
import com.yh.atttablelib.ext.parseDate
import kotlinx.android.synthetic.main.act_attendance_table.*
import java.util.*

/**
 * Created by CYH on 2019-09-27 17:13
 */
class AttendanceTableAct : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.act_attendance_table)

        setupAttViewData()

    }

    private fun setupAttViewData() {

        val multiSelect = ArrayList<Calendar>()
        for (index in 0..20) {
            val cal = java.util.Calendar.getInstance()
            cal.timeInMillis = (cal.timeInMillis + index * 24 * 60 * 60 * 1000)
            val year = cal.get(java.util.Calendar.YEAR)
            val month = cal.get(java.util.Calendar.MONTH) + 1
            val day = cal.get(java.util.Calendar.DAY_OF_MONTH)

            val leftState =
                AttTableStatus.values()[(Math.random() * 100).toInt() % AttTableStatus.values().size]
            val rightState =
                AttTableStatus.values()[(Math.random() * 100).toInt() % AttTableStatus.values().size]

            val calendar = saveScheme(
                year,
                month,
                day,
                leftState,
                rightState
            )
            if (null != calendar) {
                calendar.isSelected = true
                multiSelect.add(calendar)
            }
        }
        val startTime = parseDate("2019-09-01", "yyyy-MM-dd")
        val endTime = parseDate("2019-09-30", "yyyy-MM-dd")

        if (null != startTime && null != endTime) {
            makeFull(startTime, endTime, multiSelect)
        }

        mAttView.setupDate(multiSelect)
    }

    private fun makeFull(
        startTime: java.util.Calendar, endTime: java.util.Calendar, multiSelect: ArrayList<Calendar>
    ) {
        for (dayMillis in startTime.timeInMillis..endTime.timeInMillis step (CalendarUtil.SECOND_MILLS_OF_DAY)) {
            val cur = java.util.Calendar.getInstance()
            cur.timeInMillis = dayMillis

            val year = cur.get(java.util.Calendar.YEAR)
            val month = cur.get(java.util.Calendar.MONTH) + 1
            val day = cur.get(java.util.Calendar.DAY_OF_MONTH)

            val calendar = Calendar()
            calendar.year = year
            calendar.month = month
            calendar.day = day

            if (multiSelect.contains(calendar)) {
                continue
            }

            multiSelect.add(calendar)
        }
    }

    private fun saveScheme(
        year: Int,
        month: Int,
        index: Int,
        vararg attTableStatuses: AttTableStatus
    ): Calendar? {
        if (null == attTableStatuses.find { AttTableStatus.Normal != it }) {
            return null
        }
        if (null == attTableStatuses.find { AttTableStatus.CHECK != it }) {
            val calendar = Calendar()
            calendar.year = year
            calendar.month = month
            calendar.day = index
            return calendar
        }
        return getSchemeCalendar(
            year, month, index, *attTableStatuses
        )
    }

    private fun getSchemeCalendar(
        year: Int, month: Int, day: Int, vararg attTableStatuses: AttTableStatus
    ): Calendar {
        val calendar = Calendar()
        calendar.year = year
        calendar.month = month
        calendar.day = day
        val sb = StringBuilder()
        attTableStatuses.forEachIndexed { index, status ->
            if (AttTableStatus.Normal != status) {
                sb.append(status.desc)
                if (index < attTableStatuses.size - 1) {
                    sb.append(" - ")
                }
            }
            calendar.addScheme(status.ordinal, status.color, status.prefix)
        }
        if (!TextUtils.isEmpty(sb)) {
            calendar.scheme = sb.toString()
        }
        return calendar
    }

}