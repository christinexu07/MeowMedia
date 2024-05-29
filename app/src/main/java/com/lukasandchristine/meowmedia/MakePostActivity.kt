package com.lukasandchristine.meowmedia

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.SystemClock
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import com.backendless.Backendless
import com.backendless.async.callback.AsyncCallback
import com.backendless.exceptions.BackendlessFault
import com.backendless.files.BackendlessFile
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
    private var currentTimeMillis: Long = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMakePostBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
        currentTimeMillis = System.currentTimeMillis()
        Backendless.Files.Android.upload(
            photo,
            Bitmap.CompressFormat.PNG,
            100,
            "${userObject.username}_${currentTimeMillis}.png",
            "Posts"
        )
    }

    private fun makePost() {
        val post: Posts = Posts()
        post.postTitle = binding.textViewMakePostTitle.text.toString()
        post.postDescription = binding.textViewMakePostDescription.text.toString()

        post.postContent = "https://stockyteaching-us.backendless.app/api/files/Posts/${userObject.username}_${currentTimeMillis}"


        // save object synchronously
        val savedContact: Contact = Backendless.Data.of(Contact::class.java).save(contact)


        // save object asynchronously
        Backendless.Data.of(Contact::class.java).save(contact, object : AsyncCallback<Contact?> {
            override fun handleResponse(response: Contact?) {
                // new Contact instance has been saved
            }

            override fun handleFault(fault: BackendlessFault) {
                // an error has occurred, the error code can be retrieved with fault.getCode()
            }
        })
    }

    private fun chooseContent() {
        val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                Log.d("PhotoPicker", "Selected URI: $uri")
                val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(
                    contentResolver, Uri.parse(
                    uri.toString()
                ))
                val myImgView = binding.imageViewMakePostContent
                myImgView.setImageBitmap(bitmap)
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