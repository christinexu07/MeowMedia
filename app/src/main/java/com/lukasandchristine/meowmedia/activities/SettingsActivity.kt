package com.lukasandchristine.meowmedia.activities

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
import com.backendless.BackendlessUser
import com.backendless.async.callback.AsyncCallback
import com.backendless.exceptions.BackendlessFault
import com.backendless.persistence.DataQueryBuilder
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.lukasandchristine.meowmedia.data.Users
import com.lukasandchristine.meowmedia.databinding.ActivitySettingsBinding
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingsActivity : AppCompatActivity() {
    companion object {
        const val TAG = "SettingsActivity"
        const val EXTRA_USER = "EXTRA_USER"
    }

    private lateinit var binding: ActivitySettingsBinding
    private var userObject: Users? = Users()
    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            Log.d("PhotoPicker", "Selected URI: $uri")
            val cr = this.contentResolver
            val mime = cr.getType(uri)!!
            if(mime.startsWith("image")) {
                val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(
                    contentResolver, Uri.parse(
                        uri.toString()
                    ))
                binding.imageViewPostProfilePicture.setImageBitmap(bitmap)
            }
        } else {
            Log.d("PhotoPicker", "No media selected")
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userObject = intent.getParcelableExtra(EXTRA_USER)
        if(userObject == null) {
            getUserInfo()
        } else {
            setFields()
            setListeners()
        }
    }

    private fun setFields() {
        binding.editTextSettingsUsername.setText(userObject?.username)
        binding.editTextSettingsDescription.setText(userObject?.profileDescription)
        if(userObject?.profilePicture == null) {
            Glide
                .with(this)
                .load("https://stockyteaching-us.backendless.app/api/files/Profile%20Pictures/default_pfp.png")
                .apply(RequestOptions.circleCropTransform()).into(binding.imageViewPostProfilePicture)
        } else {
            Glide.with(this)
                .load("https://stockyteaching-us.backendless.app/api/files/Profile%20Pictures/${userObject?.username}.png")
                .apply(RequestOptions.circleCropTransform()).into(binding.imageViewPostProfilePicture)
        }
    }

    private fun setListeners() {
        binding.imageViewPostProfilePicture.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        binding.buttonSettingsConfirmPhoto.setOnClickListener {
            uploadContent()
            userObject?.profilePicture = "${userObject?.username}.png"
            updateUser()
        }
        binding.buttonSettingsConfirmUsername.setOnClickListener {
            userObject?.username = binding.editTextSettingsUsername.text.toString()
            updateUser()
        }
        binding.buttonSettingsConfirmDescription.setOnClickListener {
            userObject?.profileDescription = binding.editTextSettingsDescription.text.toString()
            updateUser()
        }
        binding.imageViewSettingsBack.setOnClickListener {
            finish()
        }
    }

    private fun updateUser() {
        val user = BackendlessUser()
        user.setProperty("objectId", userObject!!.objectId)
        user.setProperty("username", userObject!!.username)
        user.setProperty("profileDescription", userObject!!.profileDescription)
        user.setProperty("profilePicture", userObject!!.profilePicture)
        Backendless.UserService.update(user, object: AsyncCallback<BackendlessUser?> {
            override fun handleResponse(user: BackendlessUser?) {
                Log.d(TAG, "handleResponse updateUserRecord: successful update")
            }

            override fun handleFault(fault: BackendlessFault) {
                Log.d(TAG, "handleFault updateUserRecord: Code ${fault.code}\n${fault.detail}")
            }
        })
    }

    private fun uploadContent() {
        val photo: Bitmap = binding.imageViewPostProfilePicture.drawable.toBitmap()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Backendless.Files.Android.upload(
                    photo,
                    Bitmap.CompressFormat.PNG,
                    100,
                    "${userObject?.username}.png",
                    "Profile Pictures"
                )
            } catch (e: Exception) {
                throw e
            }
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
                setFields()
                setListeners()
            }

            override fun handleFault(fault: BackendlessFault) {
                Log.d(TAG, "handleFault getUserInfo: Code ${fault.code}\n${fault.detail}")
            }
        })
    }
}