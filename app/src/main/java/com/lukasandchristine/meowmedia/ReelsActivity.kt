package com.lukasandchristine.meowmedia

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.backendless.Backendless
import com.backendless.async.callback.AsyncCallback
import com.backendless.exceptions.BackendlessFault
import com.backendless.persistence.DataQueryBuilder
import com.lukasandchristine.meowmedia.adapters.ReelsAdapter
import com.lukasandchristine.meowmedia.data.Posts
import com.lukasandchristine.meowmedia.databinding.ActivityReelsBinding


class ReelsActivity : AppCompatActivity() {
    companion object {
        const val TAG = "ReelsActivity"
    }

    private lateinit var binding: ActivityReelsBinding
    private lateinit var postsList: List<Posts>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReelsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadPosts()
        refreshList()
        setListeners()
    }

    private fun setListeners() {
        val postListAdapter = ReelsAdapter(postsList)
        binding.recyclerViewReelsReels.adapter = postListAdapter

        binding.recyclerViewReelsReels.addOnItemTouchListener(
            DoubleClickListener(
                onSingleClick = {
                    // Handle single click
                },
                onDoubleClick = { position ->
                    // Handle double click
                    postListAdapter.likeVideo(position)
                }
            )
        )
    }

    private fun refreshList() {
        val postListAdapter = ReelsAdapter(postsList)

        val recyclerView: RecyclerView = binding.recyclerViewReelsReels
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val snapHelper: SnapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(binding.recyclerViewReelsReels)
        recyclerView.adapter = postListAdapter
        postListAdapter.notifyDataSetChanged()
    }

    private fun loadPosts() {
        val userId = Backendless.UserService.CurrentUser().userId!!
        val whereClause = "ownerId != '$userId'"
        val queryBuilder = DataQueryBuilder.create()
        queryBuilder.setPageSize(25)
        queryBuilder.whereClause = whereClause
        Backendless.Data.of(Posts::class.java).find(queryBuilder, object:
            AsyncCallback<List<Posts>> {
            override fun handleResponse(postList: List<Posts>?) {
                Log.d(MainActivity.TAG, "handleResponse: $postList")
                postsList = postList!!.filter{
                    it.isVideo
                }
            }

            override fun handleFault(fault: BackendlessFault) {
                Log.d(MainActivity.TAG, "handleFault: Code ${fault.code}\n${fault.detail}")
            }
        })
    }
}