package com.yh.atttablelib

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import androidx.annotation.Nullable
import com.yh.atttablelib.calendarview.BaseAttMonthView
import com.yh.atttablelib.calendarview.Calendar

/**
 * Created by CYH on 2019-06-26 13:27
 */
class AttMonthViewFull(
    context: Context,
    @Nullable
    attrs: AttributeSet? = null
) : BaseAttMonthView(context, attrs) {

    companion object {
        private const val STATUS_TEXT_SIZE = 10F
    }

    private val mMonthTextPaint = Paint()
    private val mSelectedPaint = Paint()

    private val mWhitePaint = Paint()
    private val mAttStatusPaint = Paint()
    private val mCurDayPointPaint = Paint()
    private val mCurDayBgPaint = Paint()
    private val mInvalidDayPaint = Paint()
    private var mSchemeTextPaint = Paint()
    private var mCurDayTextPaint = Paint()

    private var mRadiusBackground: Float = dipToPx(20f).toFloat()
    private var mRadiusForeground: Float = dipToPx(18f).toFloat()
    private var mRadiusPadding: Float = dipToPx(2f).toFloat()
    private var mCurDayPointRadius: Float = dipToPx(2f).toFloat()

    init {
        mMonthTextPaint.isAntiAlias = true
        mMonthTextPaint.isFakeBoldText = false
        mMonthTextPaint.style = Paint.Style.FILL
        mMonthTextPaint.textAlign = Paint.Align.CENTER
        mMonthTextPaint.textSize = dipToPx(mDefTxtSize).toFloat()
        mMonthTextPaint.color = Color.parseColor("#FF212325")

        mCurDayBgPaint.isAntiAlias = true
        mCurDayBgPaint.isFakeBoldText = false
        mCurDayBgPaint.strokeWidth = dipToPx(1f).toFloat()

        mSchemeTextPaint.isAntiAlias = true
        mSchemeTextPaint.isFakeBoldText = false
        mSchemeTextPaint.color = Color.WHITE
        mSchemeTextPaint.style = Paint.Style.FILL
        mSchemeTextPaint.textAlign = Paint.Align.CENTER
        mSchemeTextPaint.textSize = mMonthTextPaint.textSize

        mAttStatusPaint.isAntiAlias = true
        mAttStatusPaint.style = Paint.Style.FILL
        mAttStatusPaint.textAlign = Paint.Align.CENTER
        mAttStatusPaint.textSize = dipToPx(STATUS_TEXT_SIZE).toFloat()

        mCurDayPointPaint.isAntiAlias = true
        mCurDayPointPaint.isFakeBoldText = false
        mCurDayPointPaint.style = Paint.Style.FILL
        mCurDayPointPaint.color = Color.parseColor("#FF215BF1")

        mWhitePaint.isAntiAlias = true
        mWhitePaint.style = Paint.Style.FILL
        mWhitePaint.textAlign = Paint.Align.CENTER
        mWhitePaint.color = Color.WHITE
        mWhitePaint.isFakeBoldText = false
        mWhitePaint.textSize = mMonthTextPaint.textSize

        mInvalidDayPaint.isAntiAlias = true
        mInvalidDayPaint.isFakeBoldText = false
        mInvalidDayPaint.style = Paint.Style.FILL
        mInvalidDayPaint.textAlign = Paint.Align.CENTER
        mInvalidDayPaint.color = Color.parseColor("#FFE1E1E1")
        mInvalidDayPaint.textSize = mMonthTextPaint.textSize

        mCurDayTextPaint.isAntiAlias = true
        mCurDayTextPaint.isFakeBoldText = false
        mCurDayTextPaint.textAlign = Paint.Align.CENTER
        mCurDayTextPaint.textSize = mMonthTextPaint.textSize
        mCurDayTextPaint.color = Color.parseColor("#FF215BF1")

        mSelectedPaint.isAntiAlias = true
        mSelectedPaint.color = Color.parseColor("#FFECF1FB")
        mSelectedPaint.style = Paint.Style.FILL
        mSelectedPaint.strokeWidth = 2f
        //关闭硬件加速
        setLayerType(LAYER_TYPE_SOFTWARE, mSelectedPaint)
        mSelectedPaint.maskFilter = BlurMaskFilter(50f, BlurMaskFilter.Blur.SOLID)
    }

    override fun onDrawSelected(
        canvas: Canvas,
        calendar: Calendar,
        x: Int,
        y: Int,
        hasScheme: Boolean,
        isSelectedPre: Boolean,
        isSelectedNext: Boolean
    ) {

        val cx = x + mItemWidth / 2
        val cy = y + mRadiusBackground
        if (isSelectedPre) {
            if (isSelectedNext) {
                canvas.drawRect(
                    x.toFloat(),
                    (cy - mRadiusBackground),
                    (x + mItemWidth).toFloat(),
                    (cy + mRadiusBackground),
                    mSelectedPaint
                )
            } else {//最后一个，the last
                canvas.drawRect(
                    x.toFloat(),
                    (cy - mRadiusBackground),
                    cx.toFloat(),
                    (cy + mRadiusBackground),
                    mSelectedPaint
                )
                canvas.drawCircle(cx.toFloat(), cy, mRadiusBackground, mSelectedPaint)
            }
        } else {
            if (isSelectedNext) {
                canvas.drawRect(
                    cx.toFloat(),
                    (cy - mRadiusBackground),
                    (x + mItemWidth).toFloat(),
                    (cy + mRadiusBackground),
                    mSelectedPaint
                )
            }
            canvas.drawCircle(cx.toFloat(), cy, mRadiusBackground, mSelectedPaint)
        }
    }

    override fun onDrawScheme(
        canvas: Canvas, calendar: Calendar, x: Int, y: Int, isSelected: Boolean
    ) {
        val schemes = calendar.schemes
        if (schemes == null || schemes.size == 0) {
            return
        }

        val space = dipToPx(6f)
        val cx = x + mItemWidth / 2f
        val txtSize = dipToPx(STATUS_TEXT_SIZE)
        val top = y + mRadiusBackground * 2f

        var schemeBgColor = 0
        schemes.forEachIndexed { index, scheme ->
            if (AttTableStatus.values().size <= scheme.type) {
                return@forEachIndexed
            }
            if (AttTableStatus.Normal.ordinal == scheme.type) {
                return@forEachIndexed
            }
            if (schemeBgColor == 0 || AttTableStatus.values()[scheme.type].isForeground) {
                schemeBgColor = scheme.shcemeColor
            }

            mAttStatusPaint.color = scheme.shcemeColor

            canvas.drawText(
                scheme.scheme,
                (cx - txtSize / 2 - space / 2) + (txtSize * index) + (space * index),
                top + txtSize / 2 + space,
                mAttStatusPaint
            )
        }
        if (0 != schemeBgColor) {
            mAttStatusPaint.color = schemeBgColor
            canvas.drawCircle(cx, y + mRadiusBackground, mRadiusForeground, mAttStatusPaint)
        }
    }

    override fun onDrawText(
        canvas: Canvas, calendar: Calendar, x: Int, y: Int, hasScheme: Boolean, isSelected: Boolean
    ) {
        val cx = x + mItemWidth / 2
        val top = y + mRadiusForeground * 1.5F

        if (calendar.isCurrentDay) {
            if (!hasScheme) {
                mCurDayBgPaint.style = Paint.Style.FILL
                mCurDayBgPaint.color = Color.WHITE
                canvas.drawArc(
                    RectF(
                        cx - mRadiusForeground,
                        y.toFloat() + mRadiusPadding,
                        cx + mRadiusForeground,
                        y + mRadiusForeground * 2 + mRadiusPadding
                    ), 0F, 360F, true, mCurDayBgPaint
                )
            }
            mCurDayBgPaint.style = Paint.Style.STROKE
            mCurDayBgPaint.color = Color.parseColor("#FF215BF1")
            canvas.drawArc(
                RectF(
                    cx - mRadiusBackground + mRadiusPadding,
                    y.toFloat() + mRadiusPadding,
                    cx + mRadiusBackground - mRadiusPadding,
                    y + mRadiusBackground * 2 - mRadiusPadding
                ), 0F, 360F, false, mCurDayBgPaint
            )
            canvas.drawArc(
                RectF(
                    cx - mCurDayPointRadius,
                    y + mRadiusForeground * 1.75F - mCurDayPointRadius + mRadiusPadding,
                    cx + mCurDayPointRadius,
                    y + mRadiusForeground * 1.75F + mCurDayPointRadius + mRadiusPadding
                ), 0F, 360F, true, if (hasScheme) mWhitePaint else mCurDayPointPaint
            )
            canvas.drawText(
                calendar.day.toString(),
                cx.toFloat(),
                top,
                if (hasScheme) mWhitePaint else mCurDayTextPaint
            )
        } else {
            when {
                hasScheme -> canvas.drawText(
                    calendar.day.toString(), cx.toFloat(), top, mSchemeTextPaint
                )
                isSelected -> canvas.drawText(
                    calendar.day.toString(), cx.toFloat(), top, mMonthTextPaint
                )
                calendar.isFeatureDate -> canvas.drawText(
                    calendar.day.toString(), cx.toFloat(), top, mInvalidDayPaint
                )
                else -> canvas.drawText(
                    calendar.day.toString(), cx.toFloat(), top, mMonthTextPaint
                )
            }
        }
    }

    /**
     * dp转px
     *
     * @param dpValue dp
     * @return px
     */
    private fun dipToPx(dpValue: Float): Int {
        val scale = resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }
}