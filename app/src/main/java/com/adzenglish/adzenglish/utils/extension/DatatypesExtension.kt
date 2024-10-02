package com.adzenglish.adzenglish.utils.extension

import java.math.BigDecimal


fun Int.formatString(value: String) = String.format("%s", "$value $this")
fun Float.formatToString() = String.format("%.0f%s", this, "%")

fun Int.formatToDays() = String.format("Chuỗi ngày $this")
fun Int.formatToStringLevel() = String.format("Cấp độ $this")
fun BigDecimal.formatToStringFinishLesson() = String.format("%s","Hoàn thành $this %")
