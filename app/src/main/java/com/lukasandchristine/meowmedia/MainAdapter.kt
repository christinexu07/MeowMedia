package com.lukasandchristine.meowmedia

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.VideoView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.lukasandchristine.meowmedia.data.Posts
import com.squareup.picasso.Picasso

class MainAdapter(var postsList: List<Posts>): RecyclerView.Adapter<MainAdapter.ViewHolder>() {
    companion object {
        const val TAG = "MainAdapter"
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val imageViewPost: ImageView = view.findViewById(R.id.imageView_itemPost_post)
        val videoViewPost: VideoView = view.findViewById(R.id.videoView_itemPost_post)
        val layout: ConstraintLayout = view.findViewById(R.id.layout_itemPost)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        val holder = ViewHolder(view)
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = postsList[position]
        val context = holder.layout.context
        holder.videoViewPost.isVisible = true
        holder.imageViewPost.isVisible = true

        if(post.isVideo) {
            holder.imageViewPost.isVisible = false
            holder.videoViewPost.setVideoPath(post.postContent)
        } else {
            holder.videoViewPost.isVisible = false
            Picasso
                .get()
                .load(post.postContent)
                .into(holder.imageViewPost)
        }
    }

    override fun getItemCount() = postsList.size
}