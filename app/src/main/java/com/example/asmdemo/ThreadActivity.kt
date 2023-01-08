package com.example.asmdemo

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

class ThreadActivity : AppCompatActivity() {

    private val newScheduledThreadPool: ScheduledExecutorService =
        Executors.newScheduledThreadPool(1)

    private val thread = Thread {
        while (true) {
            println(Thread.currentThread().name)
            try {
                Thread.sleep(500)
            } catch (e: InterruptedException) {
                e.printStackTrace()
                break
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_thread)

        findViewById<Button>(R.id.btn_executors).setOnClickListener {
            newScheduledThreadPool.execute {
                while (true) {
                    println(Thread.currentThread().name)
                    try {
                        Thread.sleep(500)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                        break
                    }
                }
            }
        }

        findViewById<Button>(R.id.btn_thread).setOnClickListener {
            thread.start()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        newScheduledThreadPool.shutdownNow()
        thread.interrupt()
    }
}