package com.lukasandchristine.meowmedia.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.backendless.Backendless
import com.backendless.async.callback.AsyncCallback
import com.backendless.exceptions.BackendlessFault
import com.backendless.persistence.DataQueryBuilder
import com.lukasandchristine.meowmedia.misc.DoubleClickListener
import com.lukasandchristine.meowmedia.adapters.ReelsAdapter
import com.lukasandchristine.meowmedia.data.Posts
import com.lukasandchristine.meowmedia.data.Users
import com.lukasandchristine.meowmedia.databinding.ActivityReelsBinding


class ReelsActivity : AppCompatActivity() {
    companion object {
        const val TAG = "ReelsActivity"
        const val EXTRA_USER = "EXTRA_USER"
    }

    private lateinit var binding: ActivityReelsBinding
    private var userObject: Users? = Users()
    private lateinit var postsList: List<Posts>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReelsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userObject = intent.getParcelableExtra(MakePostActivity.EXTRA_USER)

        getPosts()
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

        binding.imageButtonReelsHome.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra(MainActivity.EXTRA_USER, userObject)
            }
            startActivity(intent)
            finish()
        }
        binding.imageButtonReelsAdd.setOnClickListener {
            val intent = Intent(this, MakePostActivity::class.java).apply {
                putExtra(MakePostActivity.EXTRA_USER, userObject)
            }
            startActivity(intent)
            finish()
        }
        binding.imageButtonReelsProfile.setOnClickListener {
            val intent = Intent(this, ProfilePageActivity::class.java).apply {
                putExtra(ProfilePageActivity.EXTRA_USER, userObject)
            }
            startActivity(intent)
            finish()
        }
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

    private fun getPosts() {
        val userId = Backendless.UserService.CurrentUser().userId!!
        val whereClause = "ownerId != '$userId'"
        val queryBuilder = DataQueryBuilder.create()
        queryBuilder.setPageSize(25)
        queryBuilder.whereClause = whereClause
        Backendless.Data.of(Posts::class.java).find(queryBuilder, object:
            AsyncCallback<List<Posts>> {
            override fun handleResponse(postList: List<Posts>?) {
                Log.d(MainActivity.TAG, "handleResponse getPosts: $postList")
                postsList = postList!!.filter{
                    it.isVideo
                }
                refreshList()
                setListeners()
            }

            override fun handleFault(fault: BackendlessFault) {
                Log.d(MainActivity.TAG, "handleFault getPosts: Code ${fault.code}\n${fault.detail}")
            }
        })
    }

    private fun getUserInfo() {
        val userId = Backendless.UserService.CurrentUser().userId!!
        val whereClause = "ownerId = '$userId'"
        val queryBuilder = DataQueryBuilder.create()
        queryBuilder.whereClause = whereClause
        Backendless.Data.of(Users::class.java).find(queryBuilder, object: AsyncCallback<List<Users>> {
            override fun handleResponse(userList: List<Users>?) {
                Log.d(MainActivity.TAG, "handleResponse getUserInfo: $userList")
                userObject = userList?.get(0)!!
            }

            override fun handleFault(fault: BackendlessFault) {
                Log.d(MainActivity.TAG, "handleFault getUserInfo: Code ${fault.code}\n${fault.detail}")
            }
        })
    }
}