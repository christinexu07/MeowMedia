package com.lukasandchristine.meowmedia.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.backendless.Backendless
import com.backendless.async.callback.AsyncCallback
import com.backendless.exceptions.BackendlessFault
import com.backendless.persistence.DataQueryBuilder
import com.lukasandchristine.meowmedia.adapters.MainAdapter
import com.lukasandchristine.meowmedia.data.Posts
import com.lukasandchristine.meowmedia.data.Users
import com.lukasandchristine.meowmedia.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    companion object {
        const val TAG = "MainActivity"
        const val EXTRA_USER = "EXTRA_USER"
    }

    private lateinit var binding: ActivityMainBinding
    private var userObject: Users? = Users()
    private lateinit var postsList: List<Posts>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userObject = intent.getParcelableExtra(EXTRA_USER)
        if(userObject == null) {
            getUserInfo()
        } else {
            setListeners()
        }
    }

    override fun onResume() {
        super.onResume()
        getUserInfo()
    }

    private fun setListeners() {
        binding.imageButtonMainAdd.setOnClickListener {
            val intent = Intent(this, MakePostActivity::class.java).apply {
                putExtra(MakePostActivity.EXTRA_USER, userObject)
            }
            startActivity(intent)
        }
        binding.imageButtonMainReels.setOnClickListener {
            val intent = Intent(this, ReelsActivity::class.java).apply {
                putExtra(ReelsActivity.EXTRA_USER, userObject)
            }
            startActivity(intent)
        }
        binding.imageButtonMainProfile.setOnClickListener {
            val intent = Intent(this, ProfilePageActivity::class.java).apply {
                putExtra(ProfilePageActivity.EXTRA_USER, userObject)
            }
            startActivity(intent)
        }
    }

    private fun refreshList() {
        val postListAdapter = MainAdapter(postsList)

        val recyclerView: RecyclerView = binding.recyclerViewMainPostsList
        recyclerView.layoutManager = GridLayoutManager(this, 3)
        recyclerView.adapter = postListAdapter
        postListAdapter.notifyDataSetChanged()
    }

    private fun getOtherPosts() {
        val userId = Backendless.UserService.CurrentUser().userId!!
        val whereClause = "ownerId != '$userId'"
        val queryBuilder = DataQueryBuilder.create()
        queryBuilder.setPageSize(25)
        queryBuilder.whereClause = whereClause
        Backendless.Data.of(Posts::class.java).find(queryBuilder, object: AsyncCallback<List<Posts>> {
            override fun handleResponse(postList: List<Posts>?) {
                Log.d(TAG, "handleResponse getOtherPosts: $postList")
                postsList = postList!!.sortedBy { -it.created }
                refreshList()
                setListeners()
            }

            override fun handleFault(fault: BackendlessFault) {
                Log.d(TAG, "handleFault getPosts: Code ${fault.code}\n${fault.detail}")
            }
        })
    }

    private fun getSelfPosts() {
        val userId = Backendless.UserService.CurrentUser().userId!!
        val whereClause = "ownerId = '$userId'"
        val queryBuilder = DataQueryBuilder.create()
        queryBuilder.whereClause = whereClause
        Backendless.Data.of(Posts::class.java).find(queryBuilder, object: AsyncCallback<List<Posts>> {
            override fun handleResponse(posts: List<Posts>?) {
                Log.d(TAG, "handleResponse getSelfPosts: $posts")
                postsList = posts!!
            }

            override fun handleFault(fault: BackendlessFault) {
                Log.d(TAG, "handleFault getPosts: Code ${fault.code}\n${fault.detail}")
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
                Log.d(TAG, "handleResponse getUserInfo: $userList")
                userObject = userList?.get(0)!!
                getOtherPosts()
                getSelfPosts()
            }

            override fun handleFault(fault: BackendlessFault) {
                Log.d(TAG, "handleFault getUserInfo: Code ${fault.code}\n${fault.detail}")
            }
        })
    }
}