package com.lukasandchristine.meowmedia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.backendless.Backendless
import com.backendless.async.callback.AsyncCallback
import com.backendless.exceptions.BackendlessFault
import com.backendless.persistence.DataQueryBuilder
import com.lukasandchristine.meowmedia.data.Posts
import com.lukasandchristine.meowmedia.data.Users
import com.lukasandchristine.meowmedia.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    companion object {
        const val TAG = "MainActivity"
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var userObject: Users
    private lateinit var posts: List<Posts>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadDataFromBackendless()
    }

    private fun loadDataFromBackendless() {
        getUserInfo()
        getPosts()
    }

    private fun getPosts() {
        val userId = Backendless.UserService.CurrentUser().userId!!
        val whereClause = "ownerId != '$userId'"
        val queryBuilder = DataQueryBuilder.create()
        queryBuilder.setPageSize(25)
        queryBuilder.whereClause = whereClause
        Backendless.Data.of(Posts::class.java).find(queryBuilder, object: AsyncCallback<List<Posts>> {
            override fun handleResponse(postList: List<Posts>?) {
                Log.d(TAG, "handleResponse: $postList")
                posts = postList!!
            }

            override fun handleFault(fault: BackendlessFault) {
                Log.d(TAG, "handleFault: Code ${fault.code}\n${fault.detail}")
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
                Log.d(TAG, "handleResponse: $userList")
                userObject = userList?.get(0)!!
            }

            override fun handleFault(fault: BackendlessFault) {
                Log.d(TAG, "handleFault: Code ${fault.code}\n${fault.detail}")
            }
        })
    }
}