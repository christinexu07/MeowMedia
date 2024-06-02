package com.lukasandchristine.meowmedia.adapters

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.VideoView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.backendless.Backendless
import com.backendless.async.callback.AsyncCallback
import com.backendless.exceptions.BackendlessFault
import com.backendless.persistence.DataQueryBuilder
import com.lukasandchristine.meowmedia.R
import com.lukasandchristine.meowmedia.activities.MainActivity
import com.lukasandchristine.meowmedia.activities.PostActivity
import com.lukasandchristine.meowmedia.data.Posts
import com.lukasandchristine.meowmedia.data.Users
import com.squareup.picasso.Picasso

class MainAdapter(private var postsList: List<Posts>): RecyclerView.Adapter<MainAdapter.ViewHolder>() {
    companion object {
        const val TAG = "MainAdapter"
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val imageViewPost: ImageView = view.findViewById(R.id.imageView_itemPost_image)
        val videoViewPost: VideoView = view.findViewById(R.id.videoView_itemPost_video)
        val layout: ConstraintLayout = view.findViewById(R.id.layout_itemPost)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = postsList[position]
        val context = holder.layout.context

        if(post.isVideo) {
            holder.imageViewPost.visibility = View.GONE
            holder.videoViewPost.visibility = View.VISIBLE
            holder.videoViewPost.setVideoPath(post.postContent)
            holder.videoViewPost.start()
            Log.d(TAG, "onBindViewHolder video: ${post.postContent}")
        } else {
            holder.videoViewPost.visibility = View.GONE
            holder.imageViewPost.visibility = View.VISIBLE
            Picasso
                .get()
                .load("${post.postContent}")
                .into(holder.imageViewPost)
            Log.d(TAG, "onBindViewHolder image: ${post.postContent}")
        }

        holder.imageViewPost.setOnClickListener {
            Log.d(TAG, "onClick: $post")
            val intent = Intent(context, PostActivity::class.java).apply {
                putExtra(PostActivity.EXTRA_POST, post)
                putExtra(PostActivity.EXTRA_USER, getUserInfo(position))
            }
            context.startActivity(intent)
        }
    }

    private fun getUserInfo(position: Int): Users {
        var userObject = Users()
        val userId = postsList[position].ownerId
        val whereClause = "ownerId = '$userId'"
        val queryBuilder = DataQueryBuilder.create()
        queryBuilder.whereClause = whereClause
        Backendless.Data.of(Users::class.java).find(queryBuilder, object:
            AsyncCallback<List<Users>> {
            override fun handleResponse(userList: List<Users>?) {
                Log.d(MainActivity.TAG, "handleResponse getUserInfo: $userList")
                userObject = userList?.get(0)!!
            }

            override fun handleFault(fault: BackendlessFault) {
                Log.d(MainActivity.TAG, "handleFault getUserInfo: Code ${fault.code}\n${fault.detail}")
            }
        })
        return userObject
    }

    override fun getItemCount() = postsList.size
}