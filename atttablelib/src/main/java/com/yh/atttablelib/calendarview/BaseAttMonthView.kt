package com.yh.atttablelib.calendarview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import com.yh.atttablelib.BuildConfig
import com.yh.atttablelib.R
import com.yh.atttablelib.ext.drawTextWithRectCenter
import java.util.*

/**
 * Created by CYH on 2019-07-16 13:19
 */

@Suppress("MemberVisibilityCanBePrivate")
abstract class BaseAttMonthView(
    @NonNull
    context: Context,
    @Nullable
    attrs: AttributeSet? = null
) : View(context, attrs) {

    protected val mDefTxtSize = 14F

    private var mCurrentDate: Calendar
    protected val mCalendarData = arrayListOf<Calendar>()
    protected var mMonthStartOffset = 0

    private val mRectPaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = dipToPx(0.5f)
            //绘制长度为4的实线后再绘制长度为4的空白区域，0位间隔
            pathEffect = DashPathEffect(floatArrayOf(dipToPx(1F), dipToPx(3F)), 0f)
            color = Color.parseColor("#FFFF0000")
            isAntiAlias = true
        }
    }

    protected var mItemHeight: Int = dipToPx(56f).toInt()
    protected var mWeekBarHeight: Int = dipToPx(40f).toInt()
    protected var mLineCount: Int = 0
    protected var mHeight: Int = 0
    protected var mItemWidth = 0
    protected var mWeekTextSize = 0

    private val mWeekPaint = Paint()

    init {
        if (null != attrs) {
            val array = context.obtainStyledAttributes(attrs,
                R.styleable.BaseAttMonthView
            )

            mItemHeight = array.getDimension(
                R.styleable.BaseAttMonthView_calendar_height, mItemHeight.toFloat()
            )
                .toInt()

            mWeekBarHeight = array.getDimension(
                R.styleable.BaseAttMonthView_week_bar_height, dipToPx(40f)
            )
                .toInt()

            mWeekTextSize = array.getDimensionPixelSize(
                R.styleable.BaseAttMonthView_week_text_size, dipToPx(12f).toInt()
            )
            array.recycle()
        }

        mCurrentDate = Calendar()
        val d = Date()
        mCurrentDate.year = CalendarUtil.getDate("yyyy", d)
        mCurrentDate.month = CalendarUtil.getDate("MM", d)
        mCurrentDate.day = CalendarUtil.getDate("dd", d)
        mCurrentDate.isCurrentDay = true

        mWeekPaint.isAntiAlias = true
        mWeekPaint.style = Paint.Style.FILL
        mWeekPaint.textAlign = Paint.Align.CENTER
        mWeekPaint.color = Color.parseColor("#FFCFD3DE")
        mWeekPaint.isFakeBoldText = true
        mWeekPaint.textSize = dipToPx(mDefTxtSize)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (mLineCount != 0) {
            super.onMeasure(
                widthMeasureSpec, MeasureSpec.makeMeasureSpec(mHeight, MeasureSpec.EXACTLY)
            )
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }

    fun setupDate(calendars: ArrayList<Calendar>) {
        if (calendars.isEmpty()) {
            return
        }
        calendars.sortBy { it.timeInMillis }

        mCalendarData.clear()
        mCalendarData.addAll(calendars)

        val firstWeek = CalendarUtil.getWeekFormCalendar(mCalendarData.first())
        mMonthStartOffset = firstWeek - 1
        if (mMonthStartOffset < 0) {
            mMonthStartOffset += 7
        }

        val count = mCalendarData.size + mMonthStartOffset
        mLineCount = count / 7
        if (count % 7 > 0) {
            mLineCount += 1
        }

        mHeight = (mLineCount * mItemHeight) + mWeekBarHeight

        mCalendarData.forEach {
            it.isCurrentDay = false
            it.isFeatureDate = false
        }
        if (mCalendarData.contains(mCurrentDate)) {
            mCalendarData.find { it == mCurrentDate }
                ?.isCurrentDay = true
            mCalendarData.filterNot { it < mCurrentDate }
                .forEach { it.isFeatureDate = true }
        } else {
            if (mCalendarData.first() > mCurrentDate) {
                mCalendarData.forEach { it.isFeatureDate = true }
            }
        }

        invalidate()
        requestLayout()
    }

    private fun getWeekString(index: Int, weekStart: Int): String {
        val weeks = context.resources.getStringArray(R.array.week_string_array)

        if (weekStart == CalendarViewDelegate.WEEK_START_WITH_SUN) {
            return weeks[index]
        }
        return if (weekStart == CalendarViewDelegate.WEEK_START_WITH_MON) {
            weeks[if (index == 6) 0 else index + 1]
        } else weeks[if (index == 0) 6 else index - 1]
    }

    override fun onDraw(canvas: Canvas?) {
        if (null == canvas) {
            return
        }
        if (mLineCount == 0) {
            return
        }
        onPreviewHook()
        mItemWidth = (width - paddingStart - paddingEnd) / 7

        for (week in 0 until 7) {
            canvas.drawTextWithRectCenter(
                getWeekString(
                    week, CalendarViewDelegate.WEEK_START_WITH_MON
                ), (week * mItemWidth + mItemWidth / 2f), mWeekBarHeight / 2f, mWeekPaint
            )
        }

        mCalendarData.forEachIndexed { index, calendar ->
            val preCalendar = mCalendarData.getOrNull(index - 1)
            val nexCalendar = mCalendarData.getOrNull(index + 1)
            draw(
                canvas,
                calendar,
                (index + mMonthStartOffset) / 7,
                (index + mMonthStartOffset) % 7,
                preCalendar?.isSelected ?: false,
                nexCalendar?.isSelected ?: false
            )
        }
    }

    private fun draw(
        canvas: Canvas,
        calendar: Calendar,
        row: Int,
        column: Int,
        isPreSelected: Boolean,
        isNextSelected: Boolean
    ) {
        val x = column * mItemWidth
        val y = row * mItemHeight + mWeekBarHeight
//        onLoopStart(x, y)
        val isSelected = calendar.isSelected
        val hasScheme = calendar.hasScheme()

        if (isSelected) {
            onDrawSelected(canvas, calendar, x, y, false, isPreSelected, isNextSelected)
        }
        if (hasScheme) {
            onDrawScheme(canvas, calendar, x, y, isSelected)
        }
        onDrawText(canvas, calendar, x, y, hasScheme, isSelected)

        @Suppress("ConstantConditionIf") if (BuildConfig.BUILD_TYPE == "dev") {
            canvas.drawRect(
                x.toFloat(),
                y.toFloat(),
                (x + mItemWidth).toFloat(),
                (y + mItemHeight).toFloat(),
                mRectPaint
            )
        }
    }

    private fun dipToPx(dpValue: Float): Float {
        val scale = context.resources.displayMetrics.density
        return dpValue * scale + 0.5f
    }

    protected open fun onPreviewHook() {
        // TODO: 2017/11/16
    }

    /**
     * 绘制选中的日期
     *
     * @param canvas         canvas
     * @param calendar       日历日历calendar
     * @param x              日历Card x起点坐标
     * @param y              日历Card y起点坐标
     * @param hasScheme      hasScheme 非标记的日期
     * @param isSelectedPre  上一个日期是否选中
     * @param isSelectedNext 下一个日期是否选中
     * @return 是否继续绘制onDrawScheme，true or false
     */
    protected abstract fun onDrawSelected(
        canvas: Canvas,
        calendar: Calendar,
        x: Int,
        y: Int,
        hasScheme: Boolean,
        isSelectedPre: Boolean,
        isSelectedNext: Boolean
    )

    /**
     * 绘制标记的日期,这里可以是背景色，标记色什么的
     *
     * @param canvas     canvas
     * @param calendar   日历calendar
     * @param x          日历Card x起点坐标
     * @param y          日历Card y起点坐标
     * @param isSelected 是否选中
     */
    protected abstract fun onDrawScheme(
        canvas: Canvas, calendar: Calendar, x: Int, y: Int, isSelected: Boolean
    )

    /**
     * 绘制日历文本
     *
     * @param canvas     canvas
     * @param calendar   日历calendar
     * @param x          日历Card x起点坐标
     * @param y          日历Card y起点坐标
     * @param hasScheme  是否是标记的日期
     * @param isSelected 是否选中
     */
    protected abstract fun onDrawText(
        canvas: Canvas, calendar: Calendar, x: Int, y: Int, hasScheme: Boolean, isSelected: Boolean
    )
}