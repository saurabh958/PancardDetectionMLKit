package com.example.aadharpancardapp.utils

import android.content.Context
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import com.example.aadharpancardapp.R
import com.example.aadharpancardapp.databinding.OtpBottomsheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mukeshsolanki.OnOtpCompletionListener
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

    fun showOtpBottomSheet(
        context: Context,
        message: String,
        enteredOtp: (String) -> Unit,
        resendOtp: () -> Unit,
        wrongAadhar: () -> Unit
    ): BottomSheetDialog {

        val view = LayoutInflater.from(context).inflate(R.layout.otp_bottomsheet, null)
        val dialog = BottomSheetDialog(context)
        dialog.setContentView(view)
        var otp = ""


        val binding = OtpBottomsheetBinding.bind(view)

        binding.tvResendOtp.setOnClickListener {
            resendOtp()
            dialog.dismiss()
        }

        binding.otpView.setOtpCompletionListener {
            otp = it
            Log.d("TEST!", "showOtpBottomSheet: $it")
        }

        binding.btnConfirm.setOnClickListener {
            enteredOtp(otp)
            dialog.dismiss()
        }

        if (dialog.isShowing) {
            dialog.dismiss()
        }
        dialog.show()
        return dialog

    }

}