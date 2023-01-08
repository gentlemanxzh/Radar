package com.example.asmdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.addcn.monitor.image.LargeImageManager
import com.addcn.monitor.network.okhttp.interceptor.LargeImageInterceptor
import com.bumptech.glide.Glide

class LargeImageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_large_image)
        //监听文件大小
        LargeImageManager.getInstance().fileSizeThreshold = 1000.0
        LargeImageManager.getInstance().memorySizeThreshold = 1000.0
        //添加网络拦截器，获取文件大小
        LargeImageManager.getInstance().addOkHttpInterceptor(LargeImageInterceptor())

        Glide.with(this)
            .load(R.drawable.legal_bitmap)
            .into(findViewById(R.id.iv_image))

        val textView = findViewById<TextView>(R.id.tv_info)

        findViewById<Button>(R.id.btn_large_image).setOnClickListener {
            val builder = StringBuilder()
            LargeImageManager.LARGE_IMAGE_INFO_MAP.forEach {
                if (it.value.fileSize > LargeImageManager.getInstance().fileSizeThreshold
                    || it.value.memorySize > LargeImageManager.getInstance().memorySizeThreshold) {
                    builder.append(it.value.toString())
                }
                textView.text = builder.toString()
            }

        }
    }
}