package com.arlanallacsta.submissionstoryapp.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.arlanallacsta.submissionstoryapp.databinding.ActivityDetailBinding
import com.arlanallacsta.submissionstoryapp.main.ListStory
import com.bumptech.glide.Glide

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        data()
    }

    private fun data() {
        val intent = intent.getParcelableExtra<ListStory>(EXTRA_DATA)
        binding.tvNamaPengguna.text = intent?.name
        binding.tvDescription.text = intent?.description
        Glide.with(this@DetailActivity).load(intent?.photoUrl).circleCrop().into(binding.imgGambarUser)
    }

    companion object{
        const val EXTRA_DATA = "extra_data"
    }
}