package com.lukasandchristine.meowmedia.loginandregistration

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.backendless.Backendless
import com.backendless.BackendlessUser
import com.backendless.async.callback.AsyncCallback
import com.backendless.exceptions.BackendlessFault
import com.backendless.persistence.DataQueryBuilder
import com.lukasandchristine.meowmedia.Constants
import com.lukasandchristine.meowmedia.MainActivity
import com.lukasandchristine.meowmedia.data.Users
import com.lukasandchristine.meowmedia.databinding.ActivityRegistrationBinding

class RegistrationActivity : AppCompatActivity() {
    companion object {
        const val TAG = "RegistrationActivity"
    }

    private lateinit var binding: ActivityRegistrationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Backendless.initApp(this, Constants.APPLICATION_ID, Constants.API_KEY)

        val username = intent.getStringExtra(LoginActivity.EXTRA_USERNAME) ?: ""
        val password = intent.getStringExtra(LoginActivity.EXTRA_PASSWORD) ?: ""

        binding.editTextRegistrationUsername.setText(username)
        binding.editTextTextPassword.setText(password)

        setListeners()
    }

    private fun setListeners() {
        binding.buttonRegistrationRegister.setOnClickListener {
            val password = binding.editTextTextPassword.text.toString()
            val confirm = binding.editTextRegistrationConfirmPassword.text.toString()
            val username = binding.editTextRegistrationUsername.text.toString()
            val email = binding.editTextRegistrationEmail.text.toString()
            if(validateFields(email, username, password, confirm))  {
                val user = BackendlessUser()
                user.setProperty("username", username)
                user.email = email
                user.password = password

                Backendless.UserService.register(user, object: AsyncCallback<BackendlessUser?> {
                    override fun handleResponse(registeredUser: BackendlessUser?) {
                        Log.d(TAG, "handleResponse RegistrationActivity: ${registeredUser?.getProperty("username")} has registered.")

                        val resultIntent = Intent().apply {
                            putExtra(LoginActivity.EXTRA_PASSWORD, password)
                            putExtra(LoginActivity.EXTRA_USERNAME, username)
                        }
                        setResult(Activity.RESULT_OK, resultIntent)
                        finish()
                    }

                    override fun handleFault(fault: BackendlessFault) {
                        Log.d(TAG, "handleFault RegistrationActivity: Code ${fault.code}\n${fault.detail}")
                        Toast.makeText(this@RegistrationActivity, fault.message, Toast.LENGTH_LONG).show()
                    }
                })
            }
        }
    }

    private fun validateFields(email: String, username: String, password: String, confirm: String): Boolean {
        when {
            !RegistrationUtil.validateEmail(email) -> {
                Toast.makeText(this, "Invalid Email!", Toast.LENGTH_SHORT).show()
                return false
            }
            !RegistrationUtil.validateUsername(username) -> {
                Toast.makeText(this, "Invalid Username!", Toast.LENGTH_SHORT).show()
                return false
            }
            !RegistrationUtil.validatePassword(password, confirm) -> {
                Toast.makeText(this, "Invalid Password!", Toast.LENGTH_SHORT).show()
                return false
            }
            else -> {
                var containsUsername = false
                val queryBuilder = DataQueryBuilder.create()
                queryBuilder.whereClause = "username = '$username'"
                Backendless.Data.of(Users::class.java).find(queryBuilder, object: AsyncCallback<List<Users>> {
                    override fun handleResponse(userList: List<Users>?) {
                        Log.d(MainActivity.TAG, "handleResponse validateFields: $userList")
                        if (userList != null) {
                            if(userList.isNotEmpty() && userList[0].username == username) {
                                containsUsername = true
                                Toast.makeText(this@RegistrationActivity, "Username already exists!", Toast.LENGTH_SHORT).show()
                                return
                            } else {
                                containsUsername = false
                                return
                            }
                        }
                    }

                    override fun handleFault(fault: BackendlessFault) {
                        Log.d(TAG, "handleFault validateFields: Code ${fault.code}\n${fault.detail}")
                    }
                })
                return !containsUsername
            }
        }
    }
}