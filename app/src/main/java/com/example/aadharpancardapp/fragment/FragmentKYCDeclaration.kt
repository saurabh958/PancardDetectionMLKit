package com.example.aadharpancardapp.fragment

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.aadharpancardapp.R
import com.example.aadharpancardapp.databinding.FragmentKycDeclarationBinding
import com.example.aadharpancardapp.utils.Utility.getPurpleText

class FragmentKYCDeclaration : Fragment() {

    private lateinit var binding: FragmentKycDeclarationBinding
    private var isTermsAndConditionChecked = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_kyc_declaration, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentKycDeclarationBinding.bind(view)
        initView()
        initListener()
        onClickListener()
    }

    private fun onClickListener() {
        binding.btnContinue.setOnClickListener {
            verifyTermsAndCondition()
        }
    }

    private fun verifyTermsAndCondition() {
        if(isTermsAndConditionChecked) {
            findNavController().navigate(R.id.fragment_upload_pan_card)
        } else {
            Toast.makeText(requireContext(),resources.getString(R.string.terms_condition_msg), Toast.LENGTH_SHORT).show()
        }
    }

    private fun initListener() {
        binding.checkBoxKyc.setOnCheckedChangeListener { buttonView, isChecked ->
            isTermsAndConditionChecked = isChecked
            Log.d("TEST!", "initListener: isChecked is${isTermsAndConditionChecked}")
        }
    }

    private fun initView() {
        val customText = SpannableStringBuilder()
        customText.append(" ${resources.getString(R.string.tv_complete)}")
            customText.append(
                " ${getPurpleText(requireContext(),resources.getString(R.string.kvc))}"
            )
        customText.append(" " + resources.getString(R.string.get_your_account))
        customText.append(
                " " + getPurpleText(
                    context = requireContext(),
                    requireText = resources.getString(R.string.five_min)
                )
            )
        binding.tvCompleteYourKycText.text = customText

    }

}