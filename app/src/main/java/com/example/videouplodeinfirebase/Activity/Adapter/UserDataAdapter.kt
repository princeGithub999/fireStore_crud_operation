package com.example.videouplodeinfirebase.Activity.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.MediaController
import android.widget.TextView
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.request.RequestOptions
import com.example.videouplodeinfirebase.Activity.UpdateActivity
import com.example.videouplodeinfirebase.R
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class UserDataAdapter(private val context: Context, private var userList: List<UserDataModle>) : RecyclerView.Adapter<UserDataAdapter.ViewHolder>() {

    class ViewHolder(itemView: View,):RecyclerView.ViewHolder(itemView) {
        var video:VideoView = itemView.findViewById(R.id.videoListView)
        val image: ShapeableImageView = itemView.findViewById(R.id.imageList)
        val userName:TextView = itemView.findViewById(R.id.userName_textView)
        val title:TextView = itemView.findViewById(R.id.title_textView)
        val updateButton:ImageButton = itemView.findViewById(R.id.updateUserDataButton)
        val deleteButton:ImageButton = itemView.findViewById(R.id.deleteUserData_Button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       val view = LayoutInflater.from(parent.context).inflate(R.layout.data_list,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
       return userList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.userName.text = userList[position].userName
        holder.title.text = userList[position].title

        val storeg = FirebaseStorage.getInstance().reference
        storeg.child(userList[position].childnameImage!!)
            .downloadUrl
            .addOnSuccessListener {
                Glide.with(context)
                    .load(it)
                    .apply(RequestOptions())
                    .into(holder.image)
                return@addOnSuccessListener
            }
            .addOnFailureListener {
                Toast.makeText(context, "Don't Get Image", Toast.LENGTH_SHORT).show()
            }

        storeg.child(userList[position].childNameVideo!!)
            .downloadUrl
            .addOnSuccessListener {
                holder.video.setVideoURI(it)
                val mediaController = MediaController(context)
                mediaController.setAnchorView(mediaController)
                holder.video.setMediaController(mediaController)
            }

        holder.updateButton.setOnClickListener {
            var intent = Intent(context,UpdateActivity::class.java)
            intent.putExtra("userName",userList[position].userName)
            intent.putExtra("userTitle",userList[position].title)
            intent.putExtra("id",userList[position].id)
            intent.putExtra("image",userList[position].childnameImage)
            intent.putExtra("video",userList[position].childNameVideo)
            context.startActivity(intent)
        }

        holder.deleteButton.setOnClickListener {
                FirebaseFirestore.getInstance()
                    .collection("youTub")
                    .document(userList[position].id!!)
                    .delete()
                    .addOnSuccessListener {
                        Toast.makeText(context, "Delete", Toast.LENGTH_SHORT).show()
                        FirebaseFirestore.getInstance().collection("youTub")
                            .get()
                            .addOnSuccessListener { document ->
                                val list = ArrayList<UserDataModle>()
                                for (a in document) {
                                    val data = a.toObject(UserDataModle::class.java)
                                    list.add(data)
                                }
                                refpage(list)
                            }
                            }
                    .addOnFailureListener {
                        Toast.makeText(context, "no delete", Toast.LENGTH_SHORT).show()
                    }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun refpage(list: List<UserDataModle>){
        userList = list
        notifyDataSetChanged()
    }

}


