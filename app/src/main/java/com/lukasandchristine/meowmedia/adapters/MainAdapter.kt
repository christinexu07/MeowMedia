package com.lukasandchristine.meowmedia.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView
import com.lukasandchristine.meowmedia.R
import com.lukasandchristine.meowmedia.data.Posts
import com.squareup.picasso.Picasso

class MainAdapter(private var postsList: List<Posts>): RecyclerView.Adapter<MainAdapter.ViewHolder>() {
    companion object {
        const val TAG = "MainAdapter"
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val imageViewPost: ImageView = view.findViewById(R.id.imageView_itemReel_image)
        val videoViewPost: VideoView = view.findViewById(R.id.videoView_itemReel_video)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = postsList[position]

        if(post.isVideo) {
            holder.imageViewPost.visibility = View.GONE
            holder.videoViewPost.visibility = View.VISIBLE
            holder.videoViewPost.setVideoPath(post.postContent)
        } else {
            holder.videoViewPost.visibility = View.GONE
            holder.imageViewPost.visibility = View.VISIBLE
            Picasso
                .get()
                .load(post.postContent)
                .into(holder.imageViewPost)
        }
    }

    override fun getItemCount() = postsList.size
}