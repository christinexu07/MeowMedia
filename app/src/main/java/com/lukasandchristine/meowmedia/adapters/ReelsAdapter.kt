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

class ReelsAdapter(private var postsList: List<Posts>): RecyclerView.Adapter<ReelsAdapter.ViewHolder>() {
    companion object {
        const val TAG = "ReelsAdapter"
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageViewReel: ImageView = view.findViewById(R.id.imageView_itemPost_image)
        val videoViewReel: VideoView = view.findViewById(R.id.videoView_itemPost_video)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_reel, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = postsList[position]
        if (post.isVideo) {
            holder.imageViewReel.visibility = View.GONE
            holder.videoViewReel.setVideoPath(post.postContent)
            holder.videoViewReel.start()

            holder.videoViewReel.setOnClickListener {
                if (holder.videoViewReel.isPlaying) {
                    holder.videoViewReel.pause()
                } else {
                    holder.videoViewReel.start()
                }
            }
        } else {
            holder.videoViewReel.visibility = View.GONE
            holder.imageViewReel.visibility = View.VISIBLE
            Picasso
                .get()
                .load(post.postContent)
                .into(holder.imageViewReel)
        }
    }

    fun likeVideo(position: Int) {
        postsList[position].likeCount++
        notifyItemChanged(position)
    }

    override fun getItemCount() = postsList.size
}