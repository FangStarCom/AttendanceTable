package com.yh.atttablelib

import android.graphics.Color

/**
 * Created by CYH on 2019-06-26 14:18
 */
enum class AttTableStatus(
    @JvmField
    val desc: String,
    @JvmField
    val prefix: String,
    @JvmField
    val color: Int,
    @JvmField
    val isForeground: Boolean
) {
    
    Normal("", "", Color.TRANSPARENT, false),
    CHECK("", "", Color.TRANSPARENT, false),
    ASK_FOR_LEAVE("请假", "假", Color.parseColor("#FF215BF1"), true),
    REST("休息", "休", Color.parseColor("#FF215BF1"), true),
    GO_OUT("外出", "外", Color.parseColor("#FF215BF1"), true),
    BE_LATE("迟到", "迟", Color.parseColor("#FFFD3A47"), true),
    LEAVE_EARLY("早退", "退", Color.parseColor("#FFFD3A47"), true),
    NOT_PRESENT("旷工", "旷", Color.parseColor("#FFFD3A47"), true),
    HOLIDAY("节假日", "节", Color.parseColor("#FFB5B6BA"), false);
}