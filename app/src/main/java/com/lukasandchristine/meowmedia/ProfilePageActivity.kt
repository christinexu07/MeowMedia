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
        const val CLICKED_USER_ID = "EXTRA_USER_ID"
    }

    private lateinit var binding: ActivityProfilePageBinding
    private lateinit var userObject: Users
    private lateinit var posts: List<Posts>
    private var isSelf: Boolean = false
    private lateinit var userId: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfilePageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Backendless.initApp(this, Constants.APPLICATION_ID, Constants.API_KEY)

        getPosts()

        userId = if(isSelf) {
            Backendless.UserService.CurrentUser().userId!!

        } else {
            intent.getStringExtra(CLICKED_USER_ID)!!
        }
        getUserInfo()
        setFields()
    }

    private fun setFields() {
        binding.textViewProfilePageTitle.text = userObject.username
        binding.textViewProfilePagePostCount.text = posts.size.toString()
        binding.textViewProfilePageFollowerCount.text = userObject.followerCount.toString()
        binding.textViewProfilePageFollowingCount.text = userObject.followingCount.toString()
        binding.textViewProfilePageDescription.text = userObject.profileDescription

        if(userObject.profilePicture == "") {
            Picasso
                .get()
                .load("https://stockyteaching-us.backendless.app/api/files/Profile%20Pictures/default_pfp.png")
                .into(binding.imageViewProfilePageProfilePicture)
        } else {
            Picasso
                .get()
                .load("https://stockyteaching-us.backendless.app/api/files/Profile%20Pictures/${userObject.username}.png")
                .into(binding.imageViewProfilePageProfilePicture)
        }

        Picasso
            .get()
            .load("https://stockyteaching-us.backendless.app/api/files/Posts/username_${posts.size - 1}.png")
            .into(binding.imageViewProfilePageOne)
        Picasso
            .get()
            .load("https://stockyteaching-us.backendless.app/api/files/Posts/username_${posts.size - 2}.png")
            .into(binding.imageViewProfilePageTwo)
        Picasso
            .get()
            .load("https://stockyteaching-us.backendless.app/api/files/Posts/username_${posts.size - 3}.png")
            .into(binding.imageViewProfilePageThree)
        Picasso
            .get()
            .load("https://stockyteaching-us.backendless.app/api/files/Posts/username_${posts.size - 4}.png")
            .into(binding.imageViewProfilePageFour)
        Picasso
            .get()
            .load("https://stockyteaching-us.backendless.app/api/files/Posts/username_${posts.size - 5}.png")
            .into(binding.imageViewProfilePageFive)
        Picasso
            .get()
            .load("https://stockyteaching-us.backendless.app/api/files/Posts/username_${posts.size - 6}.png")
            .into(binding.imageViewProfilePageSix)
        Picasso
            .get()
            .load("https://stockyteaching-us.backendless.app/api/files/Posts/username_${posts.size - 7}.png")
            .into(binding.imageViewProfilePageSeven)
        Picasso
            .get()
            .load("https://stockyteaching-us.backendless.app/api/files/Posts/username_${posts.size - 8}.png")
            .into(binding.imageViewProfilePageEight)
        Picasso
            .get()
            .load("https://stockyteaching-us.backendless.app/api/files/Posts/username_${posts.size - 9}.png")
            .into(binding.imageViewProfilePageNine)
    }

    private fun getPosts() {
        val whereClause = "ownerId = '$userId'"
        val queryBuilder = DataQueryBuilder.create()
        queryBuilder.setPageSize(25)
        queryBuilder.whereClause = whereClause
        Backendless.Data.of(Posts::class.java).find(queryBuilder, object: AsyncCallback<List<Posts>> {
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