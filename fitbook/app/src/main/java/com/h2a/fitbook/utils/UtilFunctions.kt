package com.h2a.fitbook.utils

import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.widget.TextView

object UtilFunctions {

    fun makeUnderlineForTextView(textView: TextView, text: String) {
        val textUnderlined = SpannableString(text)
        textUnderlined.setSpan(UnderlineSpan(), 0, textUnderlined.length, 0)
        textView.text = textUnderlined
    }

}
