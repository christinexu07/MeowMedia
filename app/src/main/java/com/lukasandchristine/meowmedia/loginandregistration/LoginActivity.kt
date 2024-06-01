package com.lukasandchristine.meowmedia.loginandregistration

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.backendless.Backendless
import com.backendless.BackendlessUser
import com.backendless.async.callback.AsyncCallback
import com.backendless.exceptions.BackendlessFault
import com.lukasandchristine.meowmedia.activities.MainActivity
import com.lukasandchristine.meowmedia.databinding.ActivityLoginBinding
import com.lukasandchristine.meowmedia.misc.Constants

class LoginActivity : AppCompatActivity() {
    companion object {
        const val TAG = "LoginActivity"
        const val EXTRA_USERNAME = "username"
        const val EXTRA_PASSWORD = "password"
    }

    private val startRegistrationForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data!!
            binding.editTextLoginUsername.setText(intent.getStringExtra(EXTRA_USERNAME))
            binding.editTextLoginPassword.setText(intent.getStringExtra(EXTRA_PASSWORD))
        }
    }
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Backendless.initApp(this, Constants.APPLICATION_ID, Constants.API_KEY)

        setListeners()
    }

    private fun setListeners() {
        binding.buttonLoginSignup.setOnClickListener {
            val registrationIntent = Intent(this, RegistrationActivity::class.java)
            registrationIntent.putExtra(EXTRA_USERNAME, binding.editTextLoginUsername.text.toString())
            registrationIntent.putExtra(EXTRA_PASSWORD, binding.editTextLoginPassword.text.toString())
            startRegistrationForResult.launch(registrationIntent)
        }

        binding.buttonLoginLogin.setOnClickListener {
            Backendless.UserService.login(
                binding.editTextLoginUsername.text.toString(),
                binding.editTextLoginPassword.text.toString(),
                object : AsyncCallback<BackendlessUser?> {
                    override fun handleResponse(user: BackendlessUser?) {
                        Log.d(TAG, "handleResponse LoginActivity: ${user?.getProperty("username")} has logged in.")
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                    }

                    override fun handleFault(fault: BackendlessFault) {
                        Log.d(TAG, "handleFault LoginActivity: Code ${fault.code}\n${fault.detail}")
                    }
                })
        }
    }
}