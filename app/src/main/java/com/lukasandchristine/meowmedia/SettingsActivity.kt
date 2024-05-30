package com.lukasandchristine.meowmedia

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.backendless.Backendless
import com.backendless.async.callback.AsyncCallback
import com.backendless.exceptions.BackendlessFault
import com.backendless.persistence.DataQueryBuilder
import com.lukasandchristine.meowmedia.data.Users
import com.lukasandchristine.meowmedia.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
    companion object {
        const val TAG = "SettingsActivity"
        const val EXTRA_USER = "EXTRA_USER"
    }

    private lateinit var binding: ActivitySettingsBinding
    private var userObject: Users? = Users()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userObject = intent.getParcelableExtra(EXTRA_USER)
        if(userObject == null) {
            getUserInfo()
        } else {
            setListeners()
        }
    }

    private fun setListeners() {

    }

    private fun getUserInfo() {
        val userId = Backendless.UserService.CurrentUser().userId!!
        val whereClause = "ownerId = '$userId'"
        val queryBuilder = DataQueryBuilder.create()
        queryBuilder.whereClause = whereClause
        Backendless.Data.of(Users::class.java).find(queryBuilder, object: AsyncCallback<List<Users>> {
            override fun handleResponse(userList: List<Users>?) {
                Log.d(MainActivity.TAG, "handleResponse getUserInfo: $userList")
                userObject = userList?.get(0)!!
                setListeners()
            }

            override fun handleFault(fault: BackendlessFault) {
                Log.d(MainActivity.TAG, "handleFault getUserInfo: Code ${fault.code}\n${fault.detail}")
            }
        })
    }
}