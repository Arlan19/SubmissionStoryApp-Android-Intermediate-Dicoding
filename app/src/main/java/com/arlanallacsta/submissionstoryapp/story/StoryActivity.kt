package com.arlanallacsta.submissionstoryapp.story

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.coroutineScope
import com.arlanallacsta.submissionstoryapp.MainActivity
import com.arlanallacsta.submissionstoryapp.api.ApiConfig
import com.arlanallacsta.submissionstoryapp.databinding.ActivityStoryBinding
import com.arlanallacsta.submissionstoryapp.datastore.UserPreference
import com.arlanallacsta.submissionstoryapp.repository.Repository
import com.arlanallacsta.submissionstoryapp.utils.Result
import com.arlanallacsta.submissionstoryapp.utils.UserViewModelFactory
import com.arlanallacsta.submissionstoryapp.utils.createCustomTempFile
import com.arlanallacsta.submissionstoryapp.utils.reduceFileImage
import com.arlanallacsta.submissionstoryapp.utils.uriToFile
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File

class StoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStoryBinding
    private  var getFile: File? = null
    private var job: Job = Job()
    private lateinit var viewModel: StoryViewModel
    private lateinit var userPreference: UserPreference
    private lateinit var currentPhotoPath: String

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQ_CODE_PERMISSIONS){
            if (!allPermissionGranted()){
                setMessage(this, "Permission not allowed")
                finish()
            }
        }
    }

    private fun allPermissionGranted() = REQ_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        userPreference = UserPreference(this)
        val repository = Repository(ApiConfig.getApiService())
        viewModel = ViewModelProvider(this, UserViewModelFactory(repository))[StoryViewModel::class.java]

        permissionGranted()

        binding.cameraButton.setOnClickListener {
            startTakePhoto()
        }
        binding.galleryButton.setOnClickListener{
            startGallery()
        }
        binding.uploadButton.setOnClickListener {
            if (getFile != null || !TextUtils.isEmpty(binding.etDescription.text.toString())){
                uploadStory(userPreference.token)
            }else{
                setMessage(this, "make sure all fields are filled")
            }
        }
    }

    private fun permissionGranted() {
        if (!allPermissionGranted()){
            ActivityCompat.requestPermissions(this, REQ_PERMISSIONS, REQ_CODE_PERMISSIONS)
        }
    }

    companion object{
        private val REQ_PERMISSIONS = arrayOf(android.Manifest.permission.CAMERA)
        private const val REQ_CODE_PERMISSIONS = 10
    }

    private fun setMessage(context: Context, text: String) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()

    }

    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        createCustomTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this@StoryActivity,
                "com.arlanallacsta.submissionstoryapp",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)
            getFile = myFile

            val result = BitmapFactory.decodeFile(getFile?.path)
            binding.previewImageView.setImageBitmap(result)
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri

            val myFile = uriToFile(selectedImg, this@StoryActivity)

            getFile = myFile

            binding.previewImageView.setImageURI(selectedImg)
        }
    }

    private fun uploadStory(authorization: String) {
        showLoading(true)
        val file = reduceFileImage(getFile as File)
        val description = binding.etDescription.text.toString().trim()
        lifecycle.coroutineScope.launchWhenResumed {
            if (job.isActive) job.cancel()
            job = launch {
                viewModel.uploadStories(authorization, description, file).collect{result ->
                    when(result){
                        is Result.Success ->{
                            showLoading(false)
                            startActivity(Intent(this@StoryActivity, MainActivity::class.java))
                            setMessage(this@StoryActivity, "Story Added successfully")
                            finish()
                        }
                        is Result.Error ->{
                            showLoading(false)
                            setMessage(this@StoryActivity, "Failed story added")
                        }
                        is Result.Loading ->{
                            showLoading(true)
                        }
                    }
                }
            }
        }

    }

    private fun showLoading(b: Boolean) {
        if (b) {
            binding.pbStory.visibility = View.VISIBLE
        } else {
            binding.pbStory.visibility = View.GONE
        }
    }
}