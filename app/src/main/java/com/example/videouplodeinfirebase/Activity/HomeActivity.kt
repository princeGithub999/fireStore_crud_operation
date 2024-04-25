package com.example.videouplodeinfirebase.Activity

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.videouplodeinfirebase.Adapter.UserDataAdapter
import com.example.videouplodeinfirebase.UserDataModel.UserDataModle
import com.example.videouplodeinfirebase.databinding.HomeActivityBinding
import com.google.firebase.firestore.FirebaseFirestore

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: HomeActivityBinding
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = HomeActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getUserData()

        binding.goAddDataPageButton.setOnClickListener {
            startActivity(Intent(this, AddDataActivity::class.java))
        }



    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getUserData() {
        showProgressDailog(true)
        db.collection("youTub")
            .get()
            .addOnSuccessListener { document ->
                val list = ArrayList<UserDataModle>()
                for (a in document) {
                    val data = a.toObject(UserDataModle::class.java)
                    list.add(data)
                }


                val userDataAdapter = UserDataAdapter(this, list)
                binding.recycleView.layoutManager = LinearLayoutManager(this)

                binding.recycleView.adapter = userDataAdapter
                Toast.makeText(this, "getData", Toast.LENGTH_SHORT).show()
                userDataAdapter.notifyDataSetChanged()
                showProgressDailog(false)
                return@addOnSuccessListener
            }
            .addOnFailureListener {
                Toast.makeText(this, "not get data", Toast.LENGTH_SHORT).show()
            }
    }

    private var progressDialog: ProgressDialog? = null

    private fun showProgressDailog(show: Boolean) {
        if (show) {
            if (progressDialog == null || progressDialog!!.isShowing) {
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