package com.example.videouplodeinfirebase.Activity

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.MediaController
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.videouplodeinfirebase.databinding.ActivityUpdateBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage



class UpdateActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdateBinding
    private var childNameUpdateVideo: String? = null
    private var childnameUpdateImage: String? = null
    private var videoUri: Uri? = null
    private var imageUri: Uri? = null
    private var id: String? = null
    private val storeRef = FirebaseStorage.getInstance().reference
    private val videoUpdateRequestCode = 30
    private val imageUpdateRequestCode = 40
    private val db =  FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userName = intent.getStringExtra("userName")
        val userTitle = intent.getStringExtra("userTitle")
        id = intent.getStringExtra("id")
        childnameUpdateImage = intent.getStringExtra("image")
        childNameUpdateVideo = intent.getStringExtra("video")

        binding.updateUserName.setText(userName)
        binding.updateTitle.setText(userTitle)

        FirebaseStorage.getInstance().reference
            .child(childnameUpdateImage!!)
            .downloadUrl
            .addOnSuccessListener {
                Glide.with(this)
                    .load(it)
                    .apply(RequestOptions())
                    .into(binding.updateImageView)
            }.addOnFailureListener {

            }

            FirebaseStorage.getInstance().reference
                .child(childNameUpdateVideo!!)
                .downloadUrl
                .addOnSuccessListener {
                    binding.updateVideoView.setVideoURI(it)
                    val mediaController = MediaController(this)
                    mediaController.setAnchorView(mediaController)
                    binding.updateVideoView.setMediaController(mediaController)
                }

        binding.updateSlectImage.setOnClickListener {
            updateSlectImage()
        }
        binding.updateVideoSlect.setOnClickListener {
            updateSlectVideo()
        }
        binding.updateDataButton.setOnClickListener {
            updateUserData()
        }
    }
    private fun updateSlectVideo() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, videoUpdateRequestCode)

    }
    private fun updateSlectImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, imageUpdateRequestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            val video = data?.data

            when(requestCode) {
                videoUpdateRequestCode->{
                    try {
                        this.videoUri = video
                        binding.updateVideoView.setVideoURI(this.videoUri)
                        binding.updateVideoView.start()
                        val mediaController = MediaController(this)
                        binding.updateVideoView.setMediaController(mediaController)
                        updateUplodeVideo()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                imageUpdateRequestCode->{
                    val image = data?.data
                    try {
                        this.imageUri = image
                        binding.updateImageView.setImageURI(this.imageUri)
                            updateUplodeImage()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

        }
    }
    private fun updateUplodeVideo() {
        showProgressDialog(true)
        if (videoUri == null) {
            Toast.makeText(this, "NO slect video", Toast.LENGTH_SHORT).show()
            return
        }
        val fileRef = storeRef.child(childNameUpdateVideo!!)
        val uplodeVideo = fileRef.putFile(videoUri!!)

        uplodeVideo.addOnSuccessListener {
            Toast.makeText(this, "Uplode success video", Toast.LENGTH_SHORT).show()
            showProgressDialog(false)
            return@addOnSuccessListener
        }
            .addOnFailureListener {
                Toast.makeText(this, "Don't uplode video", Toast.LENGTH_SHORT).show()
                return@addOnFailureListener
            }
    }

    private fun updateUplodeImage(){
        showProgressDialog(true)
        if (imageUri == null){
            Toast.makeText(this, "No slect image", Toast.LENGTH_SHORT).show()
        }
        val imageRef = storeRef.child(childnameUpdateImage!!)
        val uplodeImage = imageRef.putFile(imageUri!!)
        uplodeImage.addOnSuccessListener {
            Toast.makeText(this, "Image uplode success", Toast.LENGTH_SHORT).show()
            showProgressDialog(false)
            return@addOnSuccessListener
        }
            .addOnFailureListener {
                Toast.makeText(this, "Don't uplode image", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateUserData() {
        val userName = binding.updateUserName.text.toString()
        val title = binding.updateTitle.text.toString()

        val data = hashMapOf(
            "userName" to userName,
            "title" to title,
            "childNameVideo" to childNameUpdateVideo,
            "childnameImage" to childnameUpdateImage
        )
        db.collection("youTub")
                .document(id!!).update(data as Map<String, Any>)
            .addOnSuccessListener {
                Toast.makeText(this, "Add data success", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, HomeActivity::class.java))
                showProgressDialog(false)
                return@addOnSuccessListener
            }
            .addOnFailureListener {
                Toast.makeText(this, "Don't add data", Toast.LENGTH_SHORT).show()
            }
    }


    private var progressDialog: ProgressDialog? = null
    private fun showProgressDialog(show: Boolean) {
        if (show) {
            if (progressDialog == null || !progressDialog!!.isShowing) {
                progressDialog = ProgressDialog(this)
                progressDialog!!.setMessage("Loading...")
                progressDialog!!.setCancelable(false)
                progressDialog!!.show()
            }
        } else {
            progressDialog?.dismiss()
            progressDialog = null
        }
    }
}

