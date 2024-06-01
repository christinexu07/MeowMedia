package com.lukasandchristine.meowmedia.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.backendless.Backendless
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

        if(userObject == null) {
            getUserInfo()
        }
        if(post != null) {
            getAuthorInfo()
        }
    }

    override fun onStop() {
        super.onStop()
        updateUserRecord()
        updatePostAuthorRecord()
        updatePostRecord()
        finish()
    }

    private fun setListeners() {
        binding.imageViewPostBack.setOnClickListener {
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

    private fun updateUserRecord() {
        Backendless.Data.of(Users::class.java).save(userObject, object: AsyncCallback<Users> {
            override fun handleResponse(savedUser: Users) {
                savedUser.email = userObject!!.email
                savedUser.followerCount = userObject!!.followerCount
                savedUser.followingCount = userObject!!.followingCount
                savedUser.postsList = userObject!!.postsList
                savedUser.profileDescription = userObject!!.profileDescription
                savedUser.profilePicture = userObject!!.profilePicture
                savedUser.username = userObject!!.username
                savedUser.ownerId = userObject!!.ownerId
                savedUser.followingList = userObject!!.followingList
                Backendless.Data.of(Users::class.java)
                    .save(savedUser, object: AsyncCallback<Users?> {
                        override fun handleResponse(response: Users?) {
                            Log.d(TAG, "handleResponse updateUserRecord: successful update")
                        }

                        override fun handleFault(fault: BackendlessFault) {
                            Log.d(TAG, "handleFault updateUserRecord: Code ${fault.code}\n${fault.detail}")
                        }
                    })
            }
            override fun handleFault(fault: BackendlessFault) {
                Log.d(TAG, "handleFault updateUserRecord: Code ${fault.code}\n${fault.detail}")
            }
        })
    }

    private fun updatePostAuthorRecord() {
        Backendless.Data.of(Users::class.java).save(postAuthor, object: AsyncCallback<Users> {
            override fun handleResponse(savedUser: Users) {
                savedUser.email = postAuthor.email
                savedUser.followerCount = postAuthor.followerCount
                savedUser.followingCount = postAuthor.followingCount
                savedUser.postsList = postAuthor.postsList
                savedUser.profileDescription = postAuthor.profileDescription
                savedUser.profilePicture = postAuthor.profilePicture
                savedUser.username = postAuthor.username
                savedUser.ownerId = postAuthor.ownerId
                savedUser.followingList = postAuthor.followingList
                Backendless.Data.of(Users::class.java)
                    .save(savedUser, object: AsyncCallback<Users?> {
                        override fun handleResponse(response: Users?) {
                            Log.d(TAG, "handleResponse updatePostAuthorRecord: successful update")
                        }

                        override fun handleFault(fault: BackendlessFault) {
                            Log.d(TAG, "handleFault updatePostAuthorRecord: Code ${fault.code}\n${fault.detail}")
                        }
                    })
            }
            override fun handleFault(fault: BackendlessFault) {
                Log.d(TAG, "handleFault updatePostAuthorRecord: Code ${fault.code}\n${fault.detail}")
            }
        })
    }

    private fun updatePostRecord() {
        Backendless.Data.of(Posts::class.java).save(post, object: AsyncCallback<Posts> {
            override fun handleResponse(savedPost: Posts) {
                savedPost.ownerId = post!!.ownerId
                savedPost.postTitle = post!!.postTitle
                savedPost.postDescription = post!!.postDescription
                savedPost.postContent = post!!.postContent
                savedPost.isVideo = post!!.isVideo
                savedPost.likeCount = post!!.likeCount
                savedPost.comments = post!!.comments
                Backendless.Data.of(Posts::class.java)
                    .save(savedPost, object : AsyncCallback<Posts?> {
                        override fun handleResponse(response: Posts?) {
                            Log.d(TAG, "handleResponse updatePostRecord: successful update")
                        }

                        override fun handleFault(fault: BackendlessFault) {
                            Log.d(TAG, "handleFault updatePostRecord: Code ${fault.code}\n${fault.detail}")
                        }
                    })
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