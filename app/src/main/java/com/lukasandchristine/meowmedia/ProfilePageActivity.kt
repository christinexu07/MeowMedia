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
import com.lukasandchristine.meowmedia.databinding.ActivityProfilePageBinding
import com.squareup.picasso.Picasso

class ProfilePageActivity : AppCompatActivity() {
    companion object {
        const val TAG = "ProfilePageActivity"
    }

    private lateinit var binding: ActivityProfilePageBinding
    private lateinit var userObject: Users
    private lateinit var posts: List<Posts>
    private var isSelf: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfilePageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Backendless.initApp(this, Constants.APPLICATION_ID, Constants.API_KEY)

        getPosts()
        getUserInfo()
        setFields()
    }

    private fun setFields() {
        binding.textViewProfilePageTitle.text = userObject.username
        binding.textViewProfilePagePostCount.text = posts.size.toString()
        binding.textViewProfilePageFollowerCount.text = userObject.followerCount.toString()
        binding.textViewProfilePageFollowingCount.text = userObject.followingCount.toString()
        binding.textViewProfilePageDescription.text = userObject.profileDescription

        Picasso
            .get()
            .load("https://stockyteaching-us.backendless.app/api/files/Profile%20Pictures/default_pfp.png")
            .into(binding.imageViewProfilePageProfilePicture)
        Picasso
            .get()
            .load("https://stockyteaching-us.backendless.app/api/files/Posts/username_timestamp.png")
            .into(binding.imageViewProfilePageOne)
        Picasso
            .get()
            .load("https://stockyteaching-us.backendless.app/api/files/Posts/")
            .into(binding.imageViewProfilePageTwo)
        Picasso
            .get()
            .load("https://stockyteaching-us.backendless.app/api/files/Posts/")
            .into(binding.imageViewProfilePageThree)
        Picasso
            .get()
            .load("https://stockyteaching-us.backendless.app/api/files/Posts/")
            .into(binding.imageViewProfilePageFour)
        Picasso
            .get()
            .load("https://stockyteaching-us.backendless.app/api/files/Posts/")
            .into(binding.imageViewProfilePageFive)
        Picasso
            .get()
            .load("https://stockyteaching-us.backendless.app/api/files/Posts/")
            .into(binding.imageViewProfilePageSix)
        Picasso
            .get()
            .load("https://stockyteaching-us.backendless.app/api/files/Posts/")
            .into(binding.imageViewProfilePageSeven)
        Picasso
            .get()
            .load("https://stockyteaching-us.backendless.app/api/files/Posts/")
            .into(binding.imageViewProfilePageEight)
        Picasso
            .get()
            .load("https://stockyteaching-us.backendless.app/api/files/Posts/")
            .into(binding.imageViewProfilePageNine)
    }

    private fun getPosts() {
        val userId = Backendless.UserService.CurrentUser().userId!!
        val whereClause = "ownerId = '$userId'"
        val queryBuilder = DataQueryBuilder.create()
        queryBuilder.setPageSize(25)
        queryBuilder.whereClause = whereClause
        Backendless.Data.of(Posts::class.java).find(queryBuilder, object:
            AsyncCallback<List<Posts>> {
            override fun handleResponse(postList: List<Posts>?) {
                Log.d(MainActivity.TAG, "handleResponse: $postList")
                posts = postList!!
            }

            override fun handleFault(fault: BackendlessFault) {
                Log.d(MainActivity.TAG, "handleFault: Code ${fault.code}\n${fault.detail}")
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
                Log.d(MainActivity.TAG, "handleResponse: $userList")
                userObject = userList?.get(0)!!
            }

            override fun handleFault(fault: BackendlessFault) {
                Log.d(MainActivity.TAG, "handleFault: Code ${fault.code}\n${fault.detail}")
            }
        })
    }
}