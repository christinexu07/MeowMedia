package com.lukasandchristine.meowmedia.activities

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import com.backendless.Backendless
import com.backendless.async.callback.AsyncCallback
import com.backendless.exceptions.BackendlessFault
import com.backendless.persistence.DataQueryBuilder
import com.lukasandchristine.meowmedia.data.Posts
import com.lukasandchristine.meowmedia.data.Users
import com.lukasandchristine.meowmedia.databinding.ActivityMakePostBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MakePostActivity : AppCompatActivity() {
    companion object {
        const val TAG = "MakePostActivity"
        const val EXTRA_USER = "EXTRA_USER"
    }
    private lateinit var binding: ActivityMakePostBinding
    private var userObject: Users? = Users()
    private var postCount: Int = 0
    private var isVideo = false
    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            Log.d("PhotoPicker", "Selected URI: $uri")
            val cr = this.contentResolver
            val mime = cr.getType(uri)!!
            if(mime.startsWith("image")) {
                binding.videoViewMakePostContent.visibility = View.GONE
                binding.imageViewMakePostContent.visibility = View.VISIBLE
                isVideo = false
                val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(
                    contentResolver, Uri.parse(
                        uri.toString()
                    ))
                binding.imageViewMakePostContent.setImageBitmap(bitmap)
            }
            if(mime.startsWith("video")) {
                binding.videoViewMakePostContent.visibility = View.VISIBLE
                binding.imageViewMakePostContent.visibility = View.GONE
                isVideo = true
                binding.videoViewMakePostContent.setVideoPath(uri.path)
                binding.videoViewMakePostContent.start()
            }
        } else {
            Log.d("PhotoPicker", "No media selected")
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMakePostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userObject = intent.getParcelableExtra(EXTRA_USER)
        Log.d(TAG, "onCreate: $userObject")
        if (userObject == null) {
            getUserInfo()
        } else {
            getPosts()
            setListeners()
        }

        binding.imageViewMakePostContent.visibility = View.VISIBLE
        binding.videoViewMakePostContent.visibility = View.GONE
    }

    private fun setListeners() {
        binding.buttonMakePostMakePost.setOnClickListener{
            uploadContent()
            Log.d(TAG, "Here")
            makePost()
        }
        binding.imageViewMakePostContent.setOnClickListener{
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        binding.videoViewMakePostContent.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.imageButtonMakePostHome.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra(MainActivity.EXTRA_USER, userObject)
            }
            startActivity(intent)
            finish()
        }
        binding.imageButtonMakePostReels.setOnClickListener {
            val intent = Intent(this, ReelsActivity::class.java).apply {
                putExtra(ReelsActivity.EXTRA_USER, userObject)
            }
            startActivity(intent)
            finish()
        }
        binding.imageButtonMakePostProfile.setOnClickListener {
            val intent = Intent(this, ProfilePageActivity::class.java).apply {
                putExtra(ProfilePageActivity.EXTRA_USER, userObject)
            }
            startActivity(intent)
            finish()
        }
    }

    private fun uploadContent() {
        val photo: Bitmap = binding.imageViewMakePostContent.drawable.toBitmap()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Backendless.Files.Android.upload(
                    photo,
                    Bitmap.CompressFormat.PNG,
                    100,
                    "${userObject?.username}_${postCount + 1}.png",
                    "Posts",
                    true
                )
            } catch (e: Exception) {
                throw e
            }
        }
    }

    private fun makePost() {
        val post = Posts()
        post.postTitle = binding.editTextMakePostTitle.text.toString()
        post.postDescription = binding.editTextMakePostDescription.text.toString()
        post.ownerId = userObject?.ownerId
        if(isVideo) {
            post.postContent = "https://stockyteaching-us.backendless.app/api/files/Posts/${userObject?.username}_${postCount + 1}.mp4"
        } else {
            post.postContent = "https://stockyteaching-us.backendless.app/api/files/Posts/${userObject?.username}_${postCount + 1}.png"
        }
        post.isVideo = isVideo
        Backendless.Data.of(Posts::class.java).save(post, object : AsyncCallback<Posts?> {
            override fun handleResponse(response: Posts?) {
                Log.d(TAG, "handleResponse makePost: $response")
                finish()
            }

            override fun handleFault(fault: BackendlessFault) {
                Log.d(TAG, "handleFault makePost: Code ${fault.code}\n${fault.detail}")
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
                getPosts()
                setListeners()
            }

            override fun handleFault(fault: BackendlessFault) {
                Log.d(TAG, "handleFault getUserInfo: Code ${fault.code}\n${fault.detail}")
            }
        })
    }

    private fun getPosts() {
        val userId = Backendless.UserService.CurrentUser().userId!!
        val whereClause = "ownerId = '$userId'"
        val queryBuilder = DataQueryBuilder.create()
        queryBuilder.whereClause = whereClause
        Backendless.Data.of(Posts::class.java).find(queryBuilder, object: AsyncCallback<List<Posts>> {
            override fun handleResponse(posts: List<Posts>?) {
                Log.d(TAG, "handleResponse getPosts: $posts")
                postCount = posts?.size!!
            }

            override fun handleFault(fault: BackendlessFault) {
                Log.d(TAG, "handleFault getPosts: Code ${fault.code}\n${fault.detail}")
            }
        })
    }
}