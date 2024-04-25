package com.example.videouplodeinfirebase.Activity

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.MediaController
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.videouplodeinfirebase.UserDataModel.UserDataModle
import com.example.videouplodeinfirebase.databinding.ActivityAddDataBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage


import java.util.UUID

class AddDataActivity : AppCompatActivity() {

    private var videoUri: Uri? = null
    private var imageUri: Uri? = null
    private val db = FirebaseFirestore.getInstance()
    private val id = UUID.randomUUID().toString()
    private val childNameVideo = "video/${UUID.randomUUID()}.mp4"
    private val childnameImage = "image/${UUID.randomUUID()}.lpg"
    private val storeRef = FirebaseStorage.getInstance().reference
    private val videoRequestCode = 10
    private val imageRequestCode = 20

    private lateinit var binding: ActivityAddDataBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddDataBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.videoView.setOnClickListener {
            slectVideo()
        }
        binding.addDataButton.setOnClickListener {
            addData()

        }
        binding.imageView.setOnClickListener {
            slectImage()
        }
    }

    private fun slectVideo() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, videoRequestCode)

    }

    private fun slectImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, imageRequestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            val video = data?.data

            when(requestCode) {

                videoRequestCode->{

                    try {
                        this.videoUri = video
                        binding.videoView.setVideoURI(this.videoUri)
                        binding.videoView.start()
                        val mediaController = MediaController(this)
                        binding.videoView.setMediaController(mediaController)
                        uplodeVideo()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                imageRequestCode->{
                    val image = data?.data
                    try {
                        this.imageUri = image
                        binding.imageView.setImageURI(this.imageUri)
                        uplodeImage()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

        }
    }

    private fun uplodeVideo() {
        showProgressDialog(true)
        if (videoUri == null) {
            Toast.makeText(this, "NO slect video", Toast.LENGTH_SHORT).show()
            return
        }
        val fileRef = storeRef.child(childNameVideo)
        val uplodeVideo = fileRef.putFile(videoUri!!)
        uplodeVideo.addOnSuccessListener {
            Toast.makeText(this, "Uplode success video", Toast.LENGTH_SHORT).show()
            showProgressDialog(false)
            return@addOnSuccessListener
        }
            .addOnFailureListener {
                Toast.makeText(this, "Don't uplode video", Toast.LENGTH_SHORT).show()
                showProgressDialog(false)
                return@addOnFailureListener
            }
    }
    private fun uplodeImage(){
        showProgressDialog(true)
        if (imageUri == null){
            Toast.makeText(this, "No slect image", Toast.LENGTH_SHORT).show()
        }
        val imageRef = storeRef.child(childnameImage)
        val uplodeImage = imageRef.putFile(imageUri!!)
        uplodeImage.addOnSuccessListener {
            Toast.makeText(this, "Image uplode success", Toast.LENGTH_SHORT).show()
            showProgressDialog(false)
            return@addOnSuccessListener
        }
            .addOnFailureListener {
                Toast.makeText(this, "Don't uplode image", Toast.LENGTH_SHORT).show()
                showProgressDialog(false)
                return@addOnFailureListener
            }
    }

    private fun addData() {
        val userName = binding.userName.text.toString()
        val title = binding.title.text.toString()

        storeRef.downloadUrl.addOnSuccessListener {
            val map = UserDataModle(id,userName,title,childNameVideo,childnameImage)
            db.collection("youTub")
                .document(id).set(map)
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