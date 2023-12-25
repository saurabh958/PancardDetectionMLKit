package com.example.aadharpancardapp.fragment

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import com.example.aadharpancardapp.R
import com.example.aadharpancardapp.databinding.FragmentUploadPanConfirmationBinding
import com.example.aadharpancardapp.fragment.FragmentUploadPanCard.Companion.IMAGE_URI
import com.example.aadharpancardapp.utils.Utility
import com.mukeshsolanki.OnOtpCompletionListener
import com.mukeshsolanki.OtpView

class FragmentUploadPanConfirmation : Fragment() {

    private var cameraImageUriFinal: Uri? = null
    private lateinit var binding: FragmentUploadPanConfirmationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            cameraImageUriFinal = it.getString(IMAGE_URI)?.toUri()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_upload_pan_confirmation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentUploadPanConfirmationBinding.bind(view)
        initView()
    }

    private fun initView() {
        binding.imgSelected.setImageURI(cameraImageUriFinal)
        binding.btnFinalSubmit.setOnClickListener {
            Utility.showOtpBottomSheet(
                requireContext(),
                resources.getString(R.string.enter_otp_sent_to_your_aadhaar_linked_mobile_number),
                ::otpSuccess,
                ::handleResendOtp,
                ::handleWrongAadhar
            )
        }
    }

    private fun handleWrongAadhar() {

    }

    private fun handleResendOtp() {

    }

    private fun otpSuccess(s: String) {
        Toast.makeText(requireContext(),resources.getString(R.string.kyc_success), Toast.LENGTH_SHORT).show()
    }

}