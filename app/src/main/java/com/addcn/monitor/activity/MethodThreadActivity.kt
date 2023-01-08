package com.addcn.monitor.activity

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.addcn.monitor.activity.adapter.MethodRecordAdapter
import com.addcn.monitor.record.IMethodRecorder
import com.addcn.monitor.record.MethodRecordManager
import com.example.asmdemo.R

/**
 * 线程耗时界面
 */
class MethodThreadActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_method_thread)
        findViewById<ImageView>(R.id.iv_back).setOnClickListener {
            finish()
        }
        val dialog = ProgressDialog.show(this, "Loading", "加载数据中")
        dialog.show()
        initData(dialog)
    }

    private fun initData(dialog: ProgressDialog) {
        Thread {
            val recorderMap = MethodRecordManager.getRecorderMap()
            if (recorderMap.size > 1) {
                initViewWithData(recorderMap.values.toList())
            } else if (recorderMap.size == 1) {
                initViewWithData(recorderMap.values.toList())
            }
            runOnUiThread {
                dialog.dismiss()
            }

        }.start()
    }

    private fun initViewWithData(list: List<IMethodRecorder>) {
        val arrayList = ArrayList<Pair<Long, IMethodRecorder>>()
        var maxDuration = 0L
        list.forEach { recorder ->
            var duration = 0L
            val methodRecordMap = recorder.methodRecordMap ?: return@forEach
            methodRecordMap.forEach { entry ->
                val value = entry.value
                val parentId = value.parentId
                if (parentId == null) {
                    duration += value.duration
                }
            }
            arrayList.add(Pair(duration, recorder))
            maxDuration = duration.coerceAtLeast(maxDuration)
        }

        arrayList.sortWith { p0, p1 -> (p1.first - p0.first).toInt() }
        runOnUiThread {
            arrayList.forEachIndexed { index, pair ->
                addView(pair.second, pair.first, maxDuration, index)
            }
        }
    }


    private fun addView(
        iMethodRecorder: IMethodRecorder,
        duration: Long,
        maxTime: Long,
        index: Int
    ) {
        val linearLayout = findViewById<LinearLayout>(R.id.ll_content_container);
        val inflate = LayoutInflater.from(linearLayout.context)
            .inflate(R.layout.item_method_recored, linearLayout, false)
        val holder = MethodRecordAdapter.MethodRecordViewHolder(inflate)
        holder.setData(
            duration,
            iMethodRecorder.recorderName + "(" + duration.toInt() + "ms)",
            maxTime,
            index
        )
        holder.itemView.setOnClickListener {
            val recorderName = iMethodRecorder.recorderName
            MethodRecordActivity.start(this, recorderName, null)
            finish()
        }
        linearLayout.addView(holder.itemView)
    }


}