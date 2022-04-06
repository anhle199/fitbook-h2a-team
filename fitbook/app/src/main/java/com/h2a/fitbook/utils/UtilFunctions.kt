package com.h2a.fitbook.utils

import android.graphics.Color
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.widget.TextView
import com.github.mikephil.charting.utils.ColorTemplate

object UtilFunctions {

    fun makeUnderlineForTextView(textView: TextView, text: String) {
        val textUnderlined = SpannableString(text)
        textUnderlined.setSpan(UnderlineSpan(), 0, textUnderlined.length, 0)
        textView.text = textUnderlined
    }

    // Returns all colors when `size` parameter is null.
    fun generateColorSet(size: Int? = null): List<Int> {
        val colors = ArrayList<Int>()
        colors.addAll(ColorTemplate.MATERIAL_COLORS.toList())
        colors.addAll(ColorTemplate.COLORFUL_COLORS.toList())
        colors.addAll(ColorTemplate.JOYFUL_COLORS.toList())
        colors.addAll(ColorTemplate.LIBERTY_COLORS.toList())
        colors.addAll(ColorTemplate.PASTEL_COLORS.toList())
        colors.addAll(ColorTemplate.VORDIPLOM_COLORS.toList())

        size?.let {
            val safeSize = if (it > 0) it else 0
            return colors.subList(0, safeSize).toList()
        }

        return colors
    }

}
