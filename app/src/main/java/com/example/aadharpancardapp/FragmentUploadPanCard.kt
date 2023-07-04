package com.example.aadharpancardapp

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.fragment.findNavController
import com.example.aadharpancardapp.databinding.FragmentUploadPanCardBinding
import com.example.aadharpancardapp.utils.Utility
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.File
import java.util.regex.Pattern
import kotlin.Exception

class FragmentUploadPanCard : Fragment() {

    private lateinit var binding: FragmentUploadPanCardBinding
    private lateinit var registerForGalleryImage: ActivityResultLauncher<Intent>
    private lateinit var registerForCameraImage: ActivityResultLauncher<Uri>
    private var cameraImageUriFinal: Uri? = null
    private lateinit var registerForPermission: ActivityResultLauncher<String>
    private lateinit var textRecognizer : TextRecognizer
    private var panCardNo = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_upload_pan_card, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentUploadPanCardBinding.bind(view)
        initView()
        initListeners()
        initOnCLickListener()
    }

    private fun initView() {
        textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    }

    private fun initOnCLickListener() {
        binding.imgBackButton.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnBrowse.setOnClickListener {
            chooseFromGallery()
        }

        binding.btnRemove.setOnClickListener {
            resetImageViewtoDefault()
        }

        binding.btnChange.setOnClickListener {
            resetImageViewtoDefault()
        }

        binding.tvTakePhoto.setOnClickListener {
            checkForRequiredPermissions()
        }
        binding.btnFinalSubmit.setOnClickListener {
            finalSubmit()
            clearUri()
        }
    }

    private fun clearUri() {
        cameraImageUriFinal = null
    }

    private fun finalSubmit() {
        if(cameraImageUriFinal == null) {
            Toast.makeText(requireContext(), "Please select Image First", Toast.LENGTH_SHORT).show()
        } else {
            val getExtractedTextPair = proceedForExtraction()
            val isPanCard = detectPanCard(getExtractedTextPair)
            if(isPanCard) {
                Toast.makeText(requireContext(), "Pan Card Detected", Toast.LENGTH_SHORT).show()
                clearUri()
            } else {
                Toast.makeText(requireContext(), "Please Enter Valid Document Only!", Toast.LENGTH_SHORT).show()
                resetImageViewtoDefault()
                clearUri()
            }
        }
    }

    private fun detectPanCard(extractedTextPair: Pair<String, MutableList<Text.TextBlock>>): Boolean {
        val textList = arrayListOf<String>()
        extractedTextPair.second.forEach {
            textList.add(it.text.lowercase())
        }
        return if(extractedTextPair.first.contains(PAN,ignoreCase = true) || extractedTextPair.first.contains(
                INCOME_TAX_DEPARTMENT,ignoreCase = true)) {
            val panCardPatter = Pattern.compile(Utility.panRegex)
            textList.forEach {
                if(Utility.regexMatcher(panCardPatter,it)) {
                    panCardNo = it
                }
            }
            true
        } else {
            false
        }
        /*if(textList.contains(PAN) || textList.contains(INCOME_TAX_DEPARTMENT)) {
            val panCardPatter = Pattern.compile(Utility.panRegex)
            textList.forEach {
                if(Utility.regexMatcher(panCardPatter,it)) {
                    panCardNo = it
                }
            }
            return true
        } else {
            return false
        }*/
    }

    private fun proceedForExtraction(): Pair<String, MutableList<Text.TextBlock>> {
        var recognisedText = ""
        var recognisedTextList = mutableListOf<Text.TextBlock>()
        try {
            val inputImage = InputImage.fromFilePath(requireContext(), cameraImageUriFinal!!)
            val textTaskResult = textRecognizer.process(inputImage)
                .addOnSuccessListener {text ->
                    recognisedText = text.text
                    recognisedTextList  = text.textBlocks
                    Log.d("TEST!", "proceedForExtraction: extractText is $recognisedText and list is $recognisedTextList")
                }
                .addOnFailureListener{
                    Toast.makeText(requireContext(), "Failed to Extract Data", Toast.LENGTH_SHORT).show()
                }
        } catch (e:Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Failed to Extract Data", Toast.LENGTH_SHORT).show()
        }
        return Pair(recognisedText,recognisedTextList)
    }

    private fun invokeCamera() {
        val cameraImageUri = createImageAndGetFileUri()
        cameraImageUri?.let {
            cameraImageUriFinal = it
            registerForCameraImage.launch(it)
        } ?: Toast.makeText(
            requireContext(),
            resources.getString(R.string.failed_to_get_image),
            Toast.LENGTH_SHORT
        ).show()

    }

    private fun checkForRequiredPermissions() {
        if (checkPermission(Manifest.permission.CAMERA)) {
            invokeCamera()
        } else {
            if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                val dial = AlertDialog.Builder(requireContext())
                dial.setMessage(resources.getString(R.string.need_camera_permssn))
                dial.setPositiveButton(
                    resources.getString(R.string.ok)
                ) { dialog, _ ->
                    dialog.dismiss()
                    ActivityCompat.requestPermissions(
                        requireActivity(),
                        arrayOf(Manifest.permission.CAMERA),
                        201
                    )
                }
                dial.setNegativeButton(
                    resources.getString(R.string.cancel)
                ) { dialog, _ ->
                    dialog.dismiss()
                }
                dial.create()
                dial.show()
            } else {
                registerForPermission.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }


    private fun createImageAndGetFileUri(): Uri? {
        var cameraUri: Uri? = null
        val image = File(requireContext().filesDir,CHILD_FILE_NAME)
        try {
            cameraUri = FileProvider.getUriForFile(
                requireContext(),AUTHORITY_FILE_PROVIDER,
                image
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return cameraUri
    }

    private fun resetImageViewtoDefault() {
        binding.cvBrowseDoc.visibility = View.VISIBLE
        binding.imgSelected.visibility = View.GONE
        binding.btnChange.visibility = View.GONE
        binding.btnRemove.visibility = View.GONE
    }

    private fun chooseFromGallery() {
        val intentForImage = getIntentForImage()
        registerForGalleryImage.launch(intentForImage)
    }

    private fun getIntentForImage(): Intent {
        val imageIntent =
            Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
                action = Intent.ACTION_GET_CONTENT
                putExtra(
                    Intent.EXTRA_MIME_TYPES,
                    listOfMime
                )
                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
            }
        return imageIntent
    }

    private fun initListeners() {
        registerForGalleryImage =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    val intentData = it.data
                    if (intentData != null) {
                        if (intentData.data != null) {
                            //Log.d("TEST!", "initListeners: selected Data is ${intentData.data}")
                            imageSuccessViewHandling()
                            cameraImageUriFinal = intentData.data!!
                            binding.imgSelected.setImageURI(intentData.data)
                        }
                    }
                }
            }

        registerForCameraImage = registerForActivityResult(ActivityResultContracts.TakePicture()) {
            if (it) {
                imageSuccessViewHandling()
                binding.imgSelected.setImageURI(cameraImageUriFinal)
            }
        }

        registerForPermission =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                if (it) {
                    invokeCamera()
                } else {
                    Toast.makeText(
                        requireContext(),
                        resources.getString(R.string.give_camera_permssn),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun imageSuccessViewHandling() {
        binding.imgSelected.visibility = View.VISIBLE
        binding.btnRemove.visibility = View.VISIBLE
        binding.btnChange.visibility = View.VISIBLE
        binding.cvBrowseDoc.visibility = View.GONE
    }

    companion object {
        //Currently Only for Image
        private val listOfMime = arrayOf("image/*")
        private const val CHILD_FILE_NAME = "camera_photo.png"
        private const val AUTHORITY_FILE_PROVIDER = "com.example.aadharpancardapp.fileProvider"
        private const val PAN = "permanent account number"
        private const val INCOME_TAX_DEPARTMENT = "income tax department"
    }


}