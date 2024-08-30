package com.brunomendonca.houstonnetworktest.presentation.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import coil.ImageLoader
import coil.load
import coil.request.CachePolicy
import coil.util.CoilUtils
import com.brunomendonca.houstonnetworktest.databinding.ActivityImageBinding
import okhttp3.OkHttpClient

class ImageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityImageBinding
    private val imagesUrls = listOf<String>(
        "https://houston-hub-cdn.ppay.me/app_tour/04bdbc-picpay.png",
//        "https://houston-hub-cdn.picpay.com/banners/dd4e10-invest28080.png",
//        "https://houston-hub-cdn.picpay.com/banners/281ff2-investi1907.png",
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.swipeImage.setOnRefreshListener {
            updateImage()
            binding.swipeImage.isRefreshing = false
        }

        updateImage()
    }

    private fun updateImage() {
        val image = imagesUrls.random()
        val imageLoader = ImageLoader
            .Builder(this)
//            .okHttpClient {
//                OkHttpClient.Builder().cache(CoilUtils.createDefaultCache(this)).build()
//            }
//            .memoryCachePolicy(CachePolicy.ENABLED)
            .build()

        binding.imgImage.load(image, imageLoader)
    }
}