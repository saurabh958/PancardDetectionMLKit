package com.example.aadharpancardapp.fragment

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import com.example.aadharpancardapp.R
import com.example.aadharpancardapp.databinding.FragmentUploadPanConfirmationBinding
import com.example.aadharpancardapp.fragment.FragmentUploadPanCard.Companion.IMAGE_URI

class FragmentUploadPanConfirmation : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private var cameraImageUriFinal: Uri? = null
    private lateinit var binding: FragmentUploadPanConfirmationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            cameraImageUriFinal = it.getString(IMAGE_URI)?.toUri()
            //param2 = it.getString(ARG_PARAM2)
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
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentUploadPanConfirmation.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(cameraUri: String) =
            FragmentUploadPanConfirmation().apply {
                arguments = Bundle().apply {
                    putString(IMAGE_URI, cameraUri)
                }
            }
    }
}