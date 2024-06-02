package com.lukasandchristine.meowmedia.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.backendless.Backendless
import com.backendless.BackendlessUser
import com.backendless.async.callback.AsyncCallback
import com.backendless.exceptions.BackendlessFault
import com.backendless.persistence.DataQueryBuilder
import com.lukasandchristine.meowmedia.data.Posts
import com.lukasandchristine.meowmedia.data.Users
import com.lukasandchristine.meowmedia.databinding.ActivityPostBinding
import com.squareup.picasso.Picasso
import java.text.DecimalFormat
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow


class PostActivity : AppCompatActivity() {
    companion object {
        const val TAG = "PostActivity"
        const val EXTRA_USER = "EXTRA_USER"
        const val EXTRA_POST = "EXTRA_POST"
    }

    private lateinit var binding: ActivityPostBinding
    private var post: Posts? = null
    private var userObject: Users? = null
    private lateinit var postAuthor: Users
    private var followingList: MutableList<Users> = mutableListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imageViewPostFilledHeart.visibility = View.GONE
        binding.imageViewPostHollowHeart.visibility = View.VISIBLE
        binding.buttonPostUnfollow.visibility = View.GONE
        binding.buttonPostFollow.visibility = View.VISIBLE

        post = intent.getParcelableExtra(EXTRA_POST)
        userObject = intent.getParcelableExtra(EXTRA_USER)
        Log.d(TAG, "onCreate: $post")
        Log.d(TAG, "onCreate: $userObject")

        getUserInfo()
        if(post != null) {
            getAuthorInfo()
        }
    }

    private fun setListeners() {
        binding.imageViewPostBack.setOnClickListener {
            updateUserRecord()
            updatePostAuthorRecord()
            updatePostRecord()
            updateFollowingRecord()
            finish()
        }
        binding.buttonPostFollow.setOnClickListener {
            binding.buttonPostFollow.visibility = View.GONE
            binding.buttonPostUnfollow.visibility = View.VISIBLE
            postAuthor.followerCount++
            userObject!!.followingCount++
        }
        binding.buttonPostUnfollow.setOnClickListener {
            binding.buttonPostFollow.visibility = View.VISIBLE
            binding.buttonPostUnfollow.visibility = View.GONE
            postAuthor.followerCount--
            userObject!!.followingCount--
        }
        binding.imageViewPostHollowHeart.setOnClickListener {
            binding.imageViewPostFilledHeart.visibility = View.VISIBLE
            binding.imageViewPostHollowHeart.visibility = View.GONE
            post!!.likeCount++
            binding.textViewPostLikeCount.text = abbreviateNum(post!!.likeCount)
        }
        binding.imageViewPostFilledHeart.setOnClickListener {
            binding.imageViewPostFilledHeart.visibility = View.GONE
            binding.imageViewPostHollowHeart.visibility = View.VISIBLE
            post!!.likeCount--
            binding.textViewPostLikeCount.text = abbreviateNum(post!!.likeCount)
        }
    }

    private fun updateFollowingRecord() {
        followingList.add(postAuthor)
        userObject?.followingList = followingList
        Log.d(TAG, "userObject updateFollowingRecord: $userObject")
        Log.d(TAG, "followingList: $followingList")
        Backendless.Data.of(Users::class.java).save(userObject, object: AsyncCallback<Users?> {
            override fun handleResponse(response: Users?) {
                Log.d(TAG, "handleResponse updateFollowingRecord: successful save")
            }

            override fun handleFault(fault: BackendlessFault) {
                Log.d(TAG, "handleFault updateFollowingRecord: Code ${fault.code}\n${fault.detail}")
            }
        })
    }

    private fun updateUserRecord() {
        val user = BackendlessUser()
        user.setProperty("objectId", userObject!!.objectId)
        user.setProperty("followingCount", userObject!!.followingCount)
        Log.d(TAG, "userObject: $userObject")
        Backendless.UserService.update(user, object: AsyncCallback<BackendlessUser?> {
            override fun handleResponse(user: BackendlessUser?) {
                Log.d(TAG, "handleResponse updateUserRecord: successful update")
            }

            override fun handleFault(fault: BackendlessFault) {
                Log.d(TAG, "handleFault updateUserRecord: Code ${fault.code}\n${fault.detail}")
            }
        })
    }

    private fun updatePostAuthorRecord() {
        val user = BackendlessUser()
        user.setProperty("objectId", postAuthor.objectId)
        user.setProperty("followerCount", postAuthor.followerCount)
        Backendless.UserService.update(user, object: AsyncCallback<BackendlessUser?> {
            override fun handleResponse(user: BackendlessUser?) {
                Log.d(TAG, "handleResponse updatePostAuthorRecord: successful update")
            }

            override fun handleFault(fault: BackendlessFault) {
                Log.d(TAG, "handleFault updatePostAuthorRecord: Code ${fault.code}\n${fault.detail}")
            }
        })
    }

    private fun updatePostRecord() {
        val newPost = Posts(
            post!!.ownerId,
            post!!.postTitle,
            post!!.postDescription,
            post!!.postContent,
            post!!.isVideo,
            post!!.likeCount,
            post!!.comments,
        )

        Backendless.Data.of(Posts::class.java).remove(post!!, object : AsyncCallback<Long?> {
            override fun handleResponse(response: Long?) {
                Log.d(TAG, "handleResponse deletePostRecord: successful delete")
            }

            override fun handleFault(fault: BackendlessFault) {
                Log.d(TAG, "handleFault deletePostRecord: Code ${fault.code}\n${fault.detail}")
            }
        })

        Backendless.Data.of(Posts::class.java).save(newPost, object : AsyncCallback<Posts?> {
            override fun handleResponse(response: Posts?) {
                Log.d(TAG, "handleResponse updatePostRecord: successful save")
            }

            override fun handleFault(fault: BackendlessFault) {
                Log.d(TAG, "handleFault updatePostRecord: Code ${fault.code}\n${fault.detail}")
            }
        })
    }

    private fun setFields() {
        binding.textViewPostUsername.text = postAuthor.username
        binding.textViewPostTitle.text = post!!.postTitle
        binding.textViewPostDescription.text = post!!.postDescription
        binding.textViewPostLikeCount.text = abbreviateNum(post!!.likeCount)
        Picasso.get()
            .load(post!!.postContent)
            .into(binding.imageViewPostContent)
    }

    private fun getAuthorInfo() {
        val userId = post!!.ownerId
        val whereClause = "ownerId = '$userId'"
        val queryBuilder = DataQueryBuilder.create()
        queryBuilder.whereClause = whereClause
        Backendless.Data.of(Users::class.java).find(queryBuilder, object:
            AsyncCallback<List<Users>> {
            override fun handleResponse(userList: List<Users>?) {
                Log.d(TAG, "handleResponse getUserInfo: $userList")
                postAuthor = userList?.get(0)!!
                setFields()
                setListeners()
            }

            override fun handleFault(fault: BackendlessFault) {
                Log.d(TAG, "handleFault getUserInfo: Code ${fault.code}\n${fault.detail}")
            }
        })
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

    private fun getUserInfo() {
        val userId = Backendless.UserService.CurrentUser().userId!!
        val whereClause = "ownerId = '$userId'"
        val queryBuilder = DataQueryBuilder.create()
        queryBuilder.whereClause = whereClause
        Backendless.Data.of(Users::class.java).find(queryBuilder, object: AsyncCallback<List<Users>> {
            override fun handleResponse(userList: List<Users>?) {
                Log.d(TAG, "handleResponse getUserInfo: $userList")
                userObject = userList?.get(0)!!
            }

            override fun handleFault(fault: BackendlessFault) {
                Log.d(TAG, "handleFault getUserInfo: Code ${fault.code}\n${fault.detail}")
            }
        })
    }
}