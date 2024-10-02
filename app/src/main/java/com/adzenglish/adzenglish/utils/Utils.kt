package com.adzenglish.adzenglish.utils

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.adzenglish.adzenglish.R
import com.adzenglish.adzenglish.customview.BackgroundDrawableSpan
import kotlin.math.log

object Utils {
    val listNickname = arrayOf("Thuyền trưởng","Thuyền phó","Hoa tiêu trưởng","Kỹ sư trưởng","Bếp trưởng")
    var map = intArrayOf(
        2,
        2,
        2,
        2,
        2,
        2,
        2,
        2,
        2,
        2,
        1,
        2,
        2,
        2,
        2,
        2,
        2,
        2,
        2,
        2,
        1,
        2,
        1,
        2,
        2,
        2,
        2,
        2,
        2,
        2,
        2,
        2
    )
    val charList = charArrayOf(
        'q',
        'w',
        'e',
        'r',
        't',
        'y',
        'u',
        'i',
        'o',
        'p',
        '1',
        'a',
        's',
        'd',
        'f',
        'g',
        'h',
        'j',
        'k',
        'l',
        '1',
        '1',
        '1',
        'z',
        'x',
        'c',
        'v',
        'b',
        'n',
        'm'
    )
    var listQuestionOne = arrayListOf(
        "Trình độ Tiếng Anh của bạn ở mức nào?",
        "Tôi mới bắt đầu học",
        "Tôi biết vài từ",
        "Tôi có thể đọc và nói"
    )
    var listQuestionTwo = arrayListOf(
        "Vì sao bạn muốn học Tiếng Anh?",
        "✈️ Để thoải mái đi du lịch",
        "\uD83D\uDCBC Để có sự nghiệp thành công",
        "\uD83E\uDDD1\u200D\uD83C\uDF93 Để đậu các kỳ thi",
        "\uD83C\uDFA5 Đọc tin, xem phim, đọc sách",
        "☺️ Cho vui"
    )

    var listQuestionFour = arrayListOf(
        "Bạn hiểu Tiếng Anh giao tiếp ở mức nào?",
        "Tôi không hiểu gì cả",
        "Tôi chỉ hiểu từng từ một",
        "Tôi hiểu được ý chính"
    )

    var listQuestionFive = arrayListOf(
        "Bạn thích thể loại nào dưới đây?",
        "\uD83C\uDF39 Tình cảm",
        "\uD83D\uDC7B Kỳ bí",
        "\uD83C\uDF7F Gia đình",
        "\uD83E\uDD96 Phiêu lưu"
    )

    fun startAnimation(view: View) {
        val scaleXIn = ObjectAnimator.ofFloat(view, "scaleX", 1.0f, 1.5f)
        val scaleYIn = ObjectAnimator.ofFloat(view, "scaleY", 1.0f, 1.5f)
        val rotateRight = ObjectAnimator.ofFloat(view, "rotation", 0f, 15f).apply {
            duration = 150
            repeatCount = 1
            repeatMode = ObjectAnimator.REVERSE
        }

        val scaleXOut = ObjectAnimator.ofFloat(view, "scaleX", 1.5f, 1.0f)
        val scaleYOut = ObjectAnimator.ofFloat(view, "scaleY", 1.5f, 1.0f)
        val animatorSet = AnimatorSet()
        animatorSet.play(scaleXIn).with(scaleYIn)
        animatorSet.play(rotateRight).after(scaleXIn)
        animatorSet.play(scaleXOut).with(scaleYOut).after(rotateRight)
        animatorSet.duration = 600
        animatorSet.start()
    }
    fun setBoldAndBackgroundSpannable(text: String, context: Context): SpannableStringBuilder {
        val spannableContent: SpannableStringBuilder
        fun getTextColor(bgrType: String): Int {
            return when (bgrType) {
                "B", "G","Y", "P", "BG", "YR" -> ContextCompat.getColor(context, R.color.white)
                else -> ContextCompat.getColor(context, R.color.blue_01)
            }
        }

        fun getBackgroundColor(bgrType: String): Int {
            return when (bgrType) {
                "B" -> ContextCompat.getColor(context, R.color.blue_02)
                "G" -> ContextCompat.getColor(context, R.color.green_01)
                "P" -> ContextCompat.getColor(context, R.color.pink)
                "Y" -> ContextCompat.getColor(context, R.color.yellow)
                "BG" -> ContextCompat.getColor(context, R.color.green_01)
                "YR", "BY" -> ContextCompat.getColor(context, R.color.yellow)
                else -> ContextCompat.getColor(context, R.color.white)
            }
        }

        fun applySpans(
            spannableContent: SpannableStringBuilder,
            start: Int,
            end: Int,
            backgroundColor: Int,
            bgrType: String
        ) {
            val textColor = getTextColor(bgrType)

            spannableContent.setSpan(
                StyleSpan(Typeface.BOLD),
                start,
                end,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
            )
            spannableContent.setSpan(
                BackgroundDrawableSpan(context, backgroundColor, textColor),
                start,
                end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }


        if (text.contains("#") && text.contains("plus") && text.contains("*")) {
            val modifiedText = text.replace("plus", " +").replace("#", "").replace("*", "")
            spannableContent = SpannableStringBuilder(modifiedText)
            val patternWithB = Regex(";([^;]+);/([A-Z]?)(-?)([A-Z])([A-Z]?)")
            val rangesToRemove = mutableListOf<IntRange>()
            val matchResultWithB = patternWithB.findAll(modifiedText)
            for (match in matchResultWithB) {
                val firstChar = match.groupValues[2]
                val secondChar = match.groupValues[3]
                val third = match.groupValues[4]
                val four = match.groupValues[5]
                val bgrType = third.plus(four)
                val firstKey = firstChar.plus(secondChar)
                if (firstKey == "B-") {

                    when (bgrType) {
                        "B" -> {
                            applySpans(
                                spannableContent,
                                match.range.first + 1,
                                match.range.last - 4,
                                getBackgroundColor(bgrType),
                                bgrType
                            )
                            rangesToRemove.add(IntRange(match.range.last - 4, match.range.last))
                        }

                        "G" -> {
                            applySpans(
                                spannableContent,
                                match.range.first + 1,
                                match.range.last - 4,
                                getBackgroundColor(bgrType),
                                bgrType
                            )
                            rangesToRemove.add(IntRange(match.range.last - 4, match.range.last))
                        }

                        "P" -> {
                            applySpans(
                                spannableContent,
                                match.range.first + 1,
                                match.range.last - 4,
                                getBackgroundColor(bgrType),
                                bgrType
                            )
                            rangesToRemove.add(IntRange(match.range.last - 4, match.range.last))
                        }

                        "BG" -> {
                            applySpans(
                                spannableContent,
                                match.range.first + 1,
                                match.range.last - 5,
                                getBackgroundColor(bgrType),
                                bgrType
                            )
                            rangesToRemove.add(IntRange(match.range.last - 5, match.range.last))

                        }

                        "YR" -> {
                            applySpans(
                                spannableContent,
                                match.range.first + 1,
                                match.range.last - 5,
                                getBackgroundColor(bgrType),
                                bgrType
                            )
                            rangesToRemove.add(IntRange(match.range.last - 5, match.range.last))
                        }

                        else -> {
                            applySpans(
                                spannableContent,
                                match.range.first + 1,
                                match.range.last - 4,
                                getBackgroundColor(bgrType),
                                bgrType
                            )
                            rangesToRemove.add(IntRange(match.range.last - 4, match.range.last))
                        }
                    }
                } else {
                    when (bgrType) {
                        Constants.KEY_TYPE_COLOR_B, Constants.KEY_TYPE_COLOR_W -> {
                            applySpans(
                                spannableContent,
                                match.range.first + 1,
                                match.range.last - 2,
                                getBackgroundColor(bgrType),
                                bgrType
                            )
                            rangesToRemove.add(IntRange(match.range.last - 2, match.range.last))
                        }

                        Constants.KEY_TYPE_COLOR_G -> {
                            applySpans(
                                spannableContent,
                                match.range.first + 1,
                                match.range.last - 2,
                                getBackgroundColor(bgrType),
                                bgrType
                            )
                            rangesToRemove.add(IntRange(match.range.last - 2, match.range.last))
                        }

                        Constants.KEY_TYPE_COLOR_P -> {
                            applySpans(
                                spannableContent,
                                match.range.first + 1,
                                match.range.last - 2,
                                getBackgroundColor(bgrType),
                                bgrType
                            )
                            rangesToRemove.add(IntRange(match.range.last - 2, match.range.last))
                        }

                        Constants.KEY_TYPE_COLOR_BG -> {
                            applySpans(
                                spannableContent,
                                match.range.first + 1,
                                match.range.last - 3,
                                getBackgroundColor(bgrType),
                                bgrType
                            )
                            rangesToRemove.add(IntRange(match.range.last - 3, match.range.last))

                        }

                        Constants.KEY_TYPE_COLOR_YR -> {
                            applySpans(
                                spannableContent,
                                match.range.first + 1,
                                match.range.last - 3,
                                getBackgroundColor(bgrType),
                                bgrType
                            )
                            rangesToRemove.add(IntRange(match.range.last - 3, match.range.last))
                        }

                        else -> {
                            applySpans(
                                spannableContent,
                                match.range.first + 1,
                                match.range.last - 2,
                                getBackgroundColor(bgrType),
                                bgrType
                            )
                            rangesToRemove.add(IntRange(match.range.last - 2, match.range.last))
                        }
                    }
                }
            }

            for (range in rangesToRemove.reversed()) {
                spannableContent.delete(range.first, range.last + 1)
            }

        } else if (text.contains("#") && text.contains("*") && !text.contains("plus")) {
            val modifiedText = text.replace("#", "").replace("*", "")
            spannableContent = SpannableStringBuilder(modifiedText)
            val patternSubjectTwo = Regex(";([^;]+);/([A-Z])")
            val matchResultTwo = patternSubjectTwo.findAll(modifiedText)
            val rangesToRemoveTwo = mutableListOf<IntRange>()
            for (match in matchResultTwo) {
                val start = match.range.first + 1
                val end = match.range.last - 2
                val bgrType = match.groupValues[2]

                spannableContent.setSpan(
                    ForegroundColorSpan(ContextCompat.getColor(context, R.color.yellow)),
                    start,
                    end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                rangesToRemoveTwo.add(IntRange(match.range.first, match.range.first + 1))
                rangesToRemoveTwo.add(IntRange(match.range.last - 2, match.range.last + 1))
            }

            for (range in rangesToRemoveTwo.reversed()) {
                spannableContent.delete(range.first, range.last)
            }
        } else if (text.contains("#") && !text.contains("*") && !text.contains("plus")) {
            spannableContent = SpannableStringBuilder(text)
            val patternWithB = Regex(";([^;]+);/B-([A-Z])")
            val matchResultTwo = patternWithB.findAll(text)
            val rangesToRemoveTwo = mutableListOf<IntRange>()

            for (match in matchResultTwo) {
                val start = match.range.first + 2
                val end = match.range.last - 3
                val bgrType = match.groupValues[2]

                spannableContent.setSpan(
                    ForegroundColorSpan(ContextCompat.getColor(context, R.color.yellow)),
                    start,
                    end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                spannableContent.setSpan(
                    BackgroundDrawableSpan(
                        context,
                        getBackgroundColor(bgrType),
                        getTextColor(bgrType)
                    ),
                    start,
                    end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                rangesToRemoveTwo.add(IntRange(match.range.first, match.range.first + 2))
                rangesToRemoveTwo.add(IntRange(match.range.last - 3, match.range.last + 1))
            }

            for (range in rangesToRemoveTwo.reversed()) {
                spannableContent.delete(range.first, range.last)
            }

        } else if (text.contains("#") && !text.contains("*") && text.contains("plus")) {
            val modifiedText = text.replace("#", "").replace("plus", " +")
            spannableContent = SpannableStringBuilder(modifiedText)
            val patternWithB = Regex(";([^;]+);/B-([A-Z])([A-Z]?)")
            val matchResultTwo = patternWithB.findAll(modifiedText)
            val rangesToRemoveTwo = mutableListOf<IntRange>()
            for (match in matchResultTwo) {
                val firstChar = match.groupValues[2]
                val secondChar = match.groupValues[3]
                when (val bgrType = firstChar.plus(secondChar)) {
                    Constants.KEY_TYPE_COLOR_B, Constants.KEY_TYPE_COLOR_W -> {
                        spannableContent.setSpan(
                            BackgroundDrawableSpan(
                                context,
                                getBackgroundColor(bgrType),
                                getTextColor(bgrType)
                            ),
                            match.range.first + 1,
                            match.range.last - 3,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                        rangesToRemoveTwo.add(IntRange(match.range.last - 4, match.range.last + 1))
                    }

                    Constants.KEY_TYPE_COLOR_G -> {
                        spannableContent.setSpan(
                            BackgroundDrawableSpan(
                                context,
                                getBackgroundColor(bgrType),
                                getTextColor(bgrType)
                            ),
                            match.range.first + 1,
                            match.range.last - 3,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                        rangesToRemoveTwo.add(IntRange(match.range.last - 4, match.range.last + 1))
                    }

                    Constants.KEY_TYPE_COLOR_P -> {
                        spannableContent.setSpan(
                            BackgroundDrawableSpan(
                                context,
                                getBackgroundColor(bgrType),
                                getTextColor(bgrType)
                            ),
                            match.range.first + 1,
                            match.range.last - 3,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                        rangesToRemoveTwo.add(IntRange(match.range.last - 4, match.range.last + 1))
                    }
                    Constants.KEY_TYPE_COLOR_Y -> {
                        spannableContent.setSpan(
                            BackgroundDrawableSpan(
                                context,
                                getBackgroundColor(bgrType),
                                getTextColor(bgrType)
                            ),
                            match.range.first + 1,
                            match.range.last - 3,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                        rangesToRemoveTwo.add(IntRange(match.range.last - 4, match.range.last + 1))
                    }

                    Constants.KEY_TYPE_COLOR_BG -> {
                        spannableContent.setSpan(
                            BackgroundDrawableSpan(
                                context,
                                getBackgroundColor(bgrType),
                                getTextColor(bgrType)
                            ),
                            match.range.first + 1,
                            match.range.last - 3,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                        rangesToRemoveTwo.add(IntRange(match.range.last - 5, match.range.last + 1))
                    }

                    Constants.KEY_TYPE_COLOR_YR -> {
                        spannableContent.setSpan(
                            BackgroundDrawableSpan(
                                context,
                                getBackgroundColor(bgrType),
                                getTextColor(bgrType)
                            ),
                            match.range.first + 1,
                            match.range.last - 3,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                        rangesToRemoveTwo.add(IntRange(match.range.last - 5, match.range.last + 1))
                    }

                    Constants.KEY_TYPE_COLOR_BY -> {
                        spannableContent.setSpan(
                            BackgroundDrawableSpan(
                                context,
                                getBackgroundColor(bgrType),
                                getTextColor(bgrType)
                            ),
                            match.range.first + 1,
                            match.range.last - 3,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                        rangesToRemoveTwo.add(IntRange(match.range.last - 5, match.range.last + 1))
                    }

                    else -> {
                        spannableContent.setSpan(
                            BackgroundDrawableSpan(
                                context,
                                getBackgroundColor(bgrType),
                                getTextColor(bgrType)
                            ),
                            match.range.first + 1,
                            match.range.last - 3,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                    }
                }
            }

            for (range in rangesToRemoveTwo.reversed()) {
                spannableContent.delete(range.first, range.last)
            }
        } else {
            val modifiedText = text.replace("*", "")
            spannableContent = SpannableStringBuilder(modifiedText)
            val patternSubjectTwo = Regex(";([^;]+);/([A-Z])")
            val matchResultTwo = patternSubjectTwo.findAll(modifiedText)
            val rangesToRemoveTwo = mutableListOf<IntRange>()
            for (match in matchResultTwo) {
                val start = match.range.first + 1
                val end = match.range.last - 2
//                val bgrType = match.groupValues[2]
                spannableContent.setSpan(
                    ForegroundColorSpan(ContextCompat.getColor(context, R.color.yellow)),
                    start,
                    end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                rangesToRemoveTwo.add(IntRange(match.range.first, match.range.first + 1))
                rangesToRemoveTwo.add(IntRange(match.range.last - 2, match.range.last + 1))
            }
            for (range in rangesToRemoveTwo.reversed()) {
                spannableContent.delete(range.first, range.last)
            }
        }

        return spannableContent
    }

    fun showPopup(
        textView: TextView,
        word: String,
        context: Context,
        listWorkVi: List<String>,
        listWorkEn: List<String>
    ) {
        val inflater = LayoutInflater.from(context)
        val popupView = inflater.inflate(R.layout.popup_explanation_text, null)
        val explanationTextView: TextView = popupView.findViewById(R.id.tvExplain)
        val indexOf = listWorkVi.indexOf(word)
        explanationTextView.text = when (word) {
            listWorkVi[indexOf] -> listWorkEn[indexOf]
            else -> "không rõ"
        }
        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        popupWindow.isFocusable = true

        val layout = textView.layout
        val text: CharSequence = textView.text
        val start = text.toString().indexOf(word)
        val end = start + word.length

        if (start == -1) {
            return
        }

        val startOffset = layout.getPrimaryHorizontal(start)
        val endOffset = layout.getPrimaryHorizontal(end)
        val line = layout.getLineForOffset(start)
        val baseline = layout.getLineBaseline(line)
        val ascent = layout.getLineAscent(line)

        val location = IntArray(2)
        textView.getLocationOnScreen(location)

        val offsetX = (startOffset + endOffset) / 2

        popupView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val popupWidth = popupView.measuredWidth

        val popupX = location[0] + offsetX.toInt() - (popupWidth / 2)
        val popupY = location[1] + baseline + ascent + textView.lineHeight + 10

        textView.post {
            popupWindow.showAtLocation(textView, Gravity.NO_GRAVITY, popupX, popupY)
        }
    }

    fun setTextColorSpannableQuickTask(text: String, context: Context): SpannableStringBuilder {
        val textChange = text.replace("+", " ").replace(Regex("[()R]"), "")
        val spannableContent = SpannableStringBuilder(textChange)
        val patternWithB = Regex("([^;/]+)/([A-Z])")
        val rangesToRemove = mutableListOf<IntRange>()
        val matchResultWith = patternWithB.findAll(textChange)
        for (match in matchResultWith) {
            val start = match.range.first
            val end = match.range.last - 1
            when (match.groupValues[2]) {
                "B" -> {
                    spannableContent.setSpan(
                        ForegroundColorSpan(ContextCompat.getColor(context, R.color.blue_02)),
                        start,
                        end,
                        Spannable.SPAN_INCLUSIVE_INCLUSIVE
                    )
                    rangesToRemove.add(IntRange(match.range.last - 1, match.range.last + 1))
                }

                "G", "Y" -> {
                    spannableContent.setSpan(
                        ForegroundColorSpan(ContextCompat.getColor(context, R.color.green_01)),
                        start,
                        end,
                        Spannable.SPAN_INCLUSIVE_INCLUSIVE
                    )
                    rangesToRemove.add(IntRange(match.range.last - 1, match.range.last + 1))
                }
            }

        }
        for (range in rangesToRemove.reversed()) {
            spannableContent.delete(range.first, range.last)
        }
        return spannableContent
    }

    fun setBackgroundSpannableQuickTask(text: String, context: Context): SpannableStringBuilder {

        val regexQuotesWorkVi = "\\(([^)]+)\\)".toRegex()
        val newInput = text.replace(regexQuotesWorkVi) {
            "..."
        }
        val textChange = newInput.replace("+", " ").replace(Regex("[()R]"), "")
        Log.d("checkText", "setBackgroundSpannableQuickTask: :newInput = $newInput")
        Log.d("checkText", "setBackgroundSpannableQuickTask: :textChange = $textChange")
        val spannableContent = SpannableStringBuilder(textChange)
        val patternWithB = Regex("([^;/]+)/([A-Z])")
        val rangesToRemove = mutableListOf<IntRange>()
        val matchResultWith = patternWithB.findAll(textChange)
        for (match in matchResultWith) {
            val start = match.range.first
            val end = match.range.last - 1
            Log.d("checkText", "setBackgroundSpannableQuickTask: :match.range.last - 1 = ${match.range.last - 1}")
            Log.d("checkText", "setBackgroundSpannableQuickTask: :match.range.last + 1 = ${match.range.last + 1}")
            when (match.groupValues[2]) {
                "B" -> {
                    spannableContent.setSpan(
                        ForegroundColorSpan(ContextCompat.getColor(context, R.color.blue_02)),
                        start,
                        end,
                        Spannable.SPAN_INCLUSIVE_INCLUSIVE
                    )
                    rangesToRemove.add(IntRange(match.range.last - 1, match.range.last + 1))
                }


                "G", "Y"-> {
                    spannableContent.setSpan(
                        BackgroundDrawableSpan(
                            context,
                            ContextCompat.getColor(context, R.color.white),
                            ContextCompat.getColor(context, R.color.green_01)
                        ),
                        start  + 1,
                        end,
                        Spannable.SPAN_INCLUSIVE_INCLUSIVE
                    )
                    rangesToRemove.add(IntRange(match.range.last - 1, match.range.last + 1))
                }
            }

        }
        for (range in rangesToRemove.reversed()) {
            spannableContent.delete(range.first, range.last)
        }
        return spannableContent
    }
}

