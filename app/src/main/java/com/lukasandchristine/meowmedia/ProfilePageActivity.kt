package com.lukasandchristine.meowmedia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.lukasandchristine.meowmedia.databinding.ActivityProfilePageBinding

class ProfilePageActivity : AppCompatActivity() {
    companion object {
        const val TAG = "ProfilePageActivity"
    }

    private lateinit var binding: ActivityProfilePageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfilePageBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}