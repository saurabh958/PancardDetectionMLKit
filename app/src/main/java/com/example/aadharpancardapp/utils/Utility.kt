package com.example.aadharpancardapp.utils

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.TypefaceSpan
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.example.aadharpancardapp.R
import java.util.regex.Matcher
import java.util.regex.Pattern

object Utility {

    val panRegex = "[a-zA-Z]{3}[pPHh][a-zA-Z][0-9]{4}[A-Z]{1}"
    val newRegex = "^[A-Z]{5}[0-9]{4}[A-Z]$"
    val caseHandleRegex = "^[a-zA-Z]{5}[0-9]{4}[a-zA-Z]$"

    fun getPurpleText(context: Context, requireText: String): SpannableString {
        val purpleString = SpannableString(requireText)
        /*val customFont = ResourcesCompat.getFont(context,R.font.opensans_extrabold)
            ?.let { if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                TypefaceSpan(it)
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.resources.getFont(R.font.opensans_extrabold)
                } else {
                    TODO("VERSION.SDK_INT < O")
                }
            }
            }*/
        purpleString.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(context, R.color.button_color)),
            0,
            purpleString.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        //purpleString.setSpan(customFont,0,purpleString.length,0)
        return purpleString
    }

    fun regexMatcher(pattern: Pattern, string: String): Boolean {
        val m: Matcher = pattern.matcher(string)
        return m.find() && m.group(0) != null
    }

}