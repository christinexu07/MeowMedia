package com.lukasandchristine.meowmedia.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.backendless.Backendless
import com.backendless.async.callback.AsyncCallback
import com.backendless.exceptions.BackendlessFault
import com.backendless.persistence.DataQueryBuilder
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.lukasandchristine.meowmedia.data.Posts
import com.lukasandchristine.meowmedia.data.Users
import com.lukasandchristine.meowmedia.databinding.ActivityProfilePageBinding
import com.squareup.picasso.Picasso
import java.text.DecimalFormat
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow

class ProfilePageActivity : AppCompatActivity() {
    companion object {
        const val TAG = "ProfilePageActivity"
        const val EXTRA_USER = "EXTRA_USER"
    }

    private lateinit var binding: ActivityProfilePageBinding
    private var userObject: Users? = Users()
    private lateinit var posts: List<Posts>
    private var userId: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfilePageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userObject = intent.getParcelableExtra(MakePostActivity.EXTRA_USER)
        if(userObject == null) {
            getUserInfo()
        } else {
            getPosts()
        }
    }

    private fun setListeners() {
        userId = Backendless.UserService.CurrentUser().userId!!
        getUserInfo()
        binding.imageButtonProfilePageHome.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra(MainActivity.EXTRA_USER, userObject)
            }
            startActivity(intent)
            finish()
        }
        binding.imageButtonProfilePageAdd.setOnClickListener {
            val intent = Intent(this, MakePostActivity::class.java).apply {
                putExtra(MakePostActivity.EXTRA_USER, userObject)
            }
            startActivity(intent)
            finish()
        }
        binding.imageButtonProfilePageReels.setOnClickListener {
            val intent = Intent(this, ReelsActivity::class.java).apply {
                putExtra(ReelsActivity.EXTRA_USER, userObject)
            }
            startActivity(intent)
            finish()
        }
    }
    private fun abbreviateNum(num: Int): String{
        val suffix = charArrayOf(' ', 'K', 'M', 'B', 'T', 'P', 'E')
        val numValue: Long = num.toLong()
        val value = floor(log10(numValue.toDouble())).toInt()
        val base = value / 3
        return if (value >= 3 && base < suffix.size) {
            DecimalFormat("#0.0").format(numValue / 10.0.pow((base * 3).toDouble())) + suffix[base]
        } else {
            DecimalFormat("#,##0").format(numValue)
        }
    }
    private fun setFields() {
        binding.textViewProfilePageTitle.text = userObject?.username
        binding.textViewProfilePagePostCount.text = posts.size.toString()

        binding.textViewProfilePageFollowerCount.text =
            userObject?.followerCount?.let { abbreviateNum(it) }
        binding.textViewProfilePageFollowingCount.text =
            userObject?.followingCount?.let { abbreviateNum(it) }
        binding.textViewProfilePageDescription.text = userObject?.profileDescription

        if(userObject?.profilePicture == null) {
            Glide
                .with(this)
                .load("https://stockyteaching-us.backendless.app/api/files/Profile%20Pictures/default_pfp.png")
                .apply(RequestOptions.circleCropTransform()).into(binding.imageViewProfilePageProfilePicture)
        } else {
            Glide.with(this)
                .load("https://stockyteaching-us.backendless.app/api/files/Profile%20Pictures/${userObject?.username}.png")
                .apply(RequestOptions.circleCropTransform()).into(binding.imageViewProfilePageProfilePicture)
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
                Log.d(MainActivity.TAG, "handleResponse getPosts: $postList")
                posts = postList!!
                setListeners()
                setFields()
            }

            override fun handleFault(fault: BackendlessFault) {
                Log.d(MainActivity.TAG, "handleFault getPosts: Code ${fault.code}\n${fault.detail}")
            }
        })
    }

    private fun getUserInfo() {
        userId = Backendless.UserService.CurrentUser().userId!!
        val whereClause = "ownerId = '$userId'"
        val queryBuilder = DataQueryBuilder.create()
        queryBuilder.whereClause = whereClause
        Backendless.Data.of(Users::class.java).find(queryBuilder, object: AsyncCallback<List<Users>> {
            override fun handleResponse(userList: List<Users>?) {
                Log.d(MainActivity.TAG, "handleResponse getUserInfo: $userList")
                userObject = userList?.get(0)!!
                getPosts()
            }

            override fun handleFault(fault: BackendlessFault) {
                Log.d(MainActivity.TAG, "handleFault getUserInfo: Code ${fault.code}\n${fault.detail}")
            }
        })
    }
}