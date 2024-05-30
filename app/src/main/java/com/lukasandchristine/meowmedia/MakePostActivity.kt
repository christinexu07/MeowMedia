package com.lukasandchristine.meowmedia

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.isVisible
import com.backendless.Backendless
import com.backendless.async.callback.AsyncCallback
import com.backendless.exceptions.BackendlessFault
import com.backendless.persistence.DataQueryBuilder
import com.lukasandchristine.meowmedia.data.Posts
import com.lukasandchristine.meowmedia.data.Users
import com.lukasandchristine.meowmedia.databinding.ActivityMakePostBinding


class MakePostActivity : AppCompatActivity() {
    companion object {
        const val TAG = "MakePostActivity"
    }
    private lateinit var binding: ActivityMakePostBinding
    private lateinit var userObject: Users
    private var postCount: Int = userObject.postsList?.size!!
    private var isVideo = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMakePostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imageViewMakePostContent.isVisible = false
        binding.videoViewMakePostContent.isVisible = false

        getUserInfo()
        setListeners()
    }

    private fun setListeners() {
        binding.buttonMakePostMakePost.setOnClickListener{
            uploadContent()
            makePost()
        }

        binding.imageViewMakePostContent.setOnClickListener{
            chooseContent()
        }
    }

    private fun uploadContent() {
        val photo: Bitmap = binding.imageViewMakePostContent.drawable.toBitmap()
        Backendless.Files.Android.upload(
            photo,
            Bitmap.CompressFormat.PNG,
            100,
            "${userObject.username}_${postCount + 1}.png",
            "Posts"
        )
    }

    private fun makePost() {
        val post = Posts()
        post.postTitle = binding.textViewMakePostTitle.text.toString()
        post.postDescription = binding.textViewMakePostDescription.text.toString()
        post.postContent = "https://stockyteaching-us.backendless.app/api/files/Posts/${userObject.username}_${postCount + 1}"
        post.isVideo = isVideo

        Backendless.Data.of(Posts::class.java).save(post, object : AsyncCallback<Posts?> {
            override fun handleResponse(response: Posts?) {
                Log.d(TAG, "New Post has been saved")
                finish()
            }

            override fun handleFault(fault: BackendlessFault) {
                Log.d(TAG, "handleFault: Code ${fault.code}\n${fault.detail}")
            }
        })
    }

    private fun chooseContent() {
        val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                Log.d("PhotoPicker", "Selected URI: $uri")
                val cr = this.contentResolver
                val mime = cr.getType(uri)!!
                if(mime.startsWith("image")) {
                    binding.videoViewMakePostContent.isVisible = false
                    binding.imageViewMakePostContent.isVisible = true
                    isVideo = false
                    val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(
                        contentResolver, Uri.parse(
                            uri.toString()
                        ))
                    binding.imageViewMakePostContent.setImageBitmap(bitmap)
                }
                if(mime.startsWith("video")) {
                    binding.videoViewMakePostContent.isVisible = true
                    binding.imageViewMakePostContent.isVisible = false
                    isVideo = true
                    binding.videoViewMakePostContent.setVideoPath(uri.path)
                }
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
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