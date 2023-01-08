@file:Suppress("DEPRECATION")

package com.addcn.monitor.activity

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.addcn.monitor.IntentKey
import com.addcn.monitor.activity.adapter.MethodRecordAdapter
import com.addcn.monitor.record.MethodRecordBean
import com.addcn.monitor.record.MethodRecordManager
import com.example.asmdemo.R
import java.util.*


class MethodRecordActivity : AppCompatActivity(), MethodRecordAdapter.OnItemClickListener {

    private val mAdapter by lazy { MethodRecordAdapter(this) }
    private val mRecycler by lazy { findViewById<RecyclerView>(R.id.rv_method) }
    private val mTvSort by lazy { findViewById<TextView>(R.id.tv_sort_list) }

    private val mStack = Stack<Pair<Int, Boolean>>()
    private var mThreadName = ""
    private var mMethodRecordBean: MethodRecordBean? = null

    companion object {
        fun start(context: Context, threadName: String, methodId: String?) {
            val intent = Intent(context, MethodRecordActivity::class.java)
            intent.putExtra(IntentKey.THREAD_NAME_KEY, threadName)
            intent.putExtra(IntentKey.METHOD_ID_KEY, methodId)
            context.startActivity(intent)
        }
    }

    private fun getMethodRecord(methodId: String?): List<MethodRecordBean> {
        val iMethodRecorder =
            MethodRecordManager.getRecorderMap()[mThreadName] ?: return emptyList()
        val records = mutableListOf<MethodRecordBean>()
        if (!TextUtils.isEmpty(methodId)) {
            val methodRecordBean = iMethodRecorder.methodRecordMap[methodId] ?: return emptyList()
            this.mMethodRecordBean = methodRecordBean
            records.add(methodRecordBean)
            methodRecordBean.children?.forEach {
                val record = iMethodRecorder.methodRecordMap[it]
                if (record != null) {
                    records.add(record)
                }
            }
            return records
        }

        this.mMethodRecordBean = null
        val methodRecordMap = iMethodRecorder.methodRecordMap

        methodRecordMap?.forEach { entry ->
            entry.value?.let {
                if (it.parentId != null) return@forEach
                records.add(it)
            }
        }
        return records
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_method_record)
        findViewById<ImageView>(R.id.iv_back).setOnClickListener {
            onBackPressed()
        }
        val closeView = findViewById<ImageView>(R.id.iv_close)
        closeView.visibility = View.VISIBLE
        closeView.setOnClickListener {
            finish()
        }

        mRecycler.layoutManager = LinearLayoutManager(this)
        mRecycler.adapter = mAdapter
        val dialog = ProgressDialog.show(this, "Loading", "加载数据中")
        dialog.show()
        mThreadName = intent.getStringExtra("THREAD_NAME_KEY") ?: ""
        val methodId = intent.getStringExtra("METHOD_ID_KEY") ?: ""
        loadData(methodId, true) {
            dialog.dismiss()
        }
        mTvSort.setOnClickListener {
            val text = mTvSort.text
            sortData("顺序" != text)
        }
    }


    private fun loadData(methodId: String, order: Boolean, function: () -> Unit) {
        Thread {
            val recordList = getMethodRecord(methodId)
            Handler(Looper.getMainLooper()).post {
                val textView = findViewById<TextView>(R.id.tv_method_title)
                val title: String
                val recordId: String

                if (mMethodRecordBean == null) {
                    var time: Long = 0
                    recordList.forEach {
                        time += it.getDuration()
                    }
                    title = mThreadName + "(" + time + "ms)"
                } else {
                    val builder = StringBuilder()
                    val record = recordList.firstOrNull()
                    recordId = record?.id ?: ""
                    builder.append(recordId)
                    builder.append("(")
                    record?.let {
                        builder.append(it.getDuration())
                    }
                    builder.append("ms)")
                    title = builder.toString()
                }
                textView.text = title
               mAdapter.setList(recordList, mMethodRecordBean)
                //排序
                sortData(order)
                function.invoke()
            }
        }.start()
    }

    private fun sortData(order: Boolean) {
        val textView = findViewById<TextView>(R.id.tv_sort_list)
        textView.text = if (order) "顺序" else "耗时"
        mAdapter.sort(order)
        mAdapter.setOrder(order)
    }


    override fun onBackPressed() {
        if (mStack.isEmpty()) {
            mAdapter.clearList()
            super.onBackPressed()
            return
        } else {
            val pop = this.mStack.pop()
            Thread {
                val parentId = mMethodRecordBean?.getParentId() ?: ""
                val second = pop.second
                Handler(Looper.getMainLooper()).post {
                    loadData(parentId, second) {
                        mRecycler.scrollBy(0, pop.first)
                    }
                }
            }.start()
        }
    }

    override fun onItemClick(adapter: MethodRecordAdapter, index: Int) {
        val record = adapter.get(index)
        if (record.id == mMethodRecordBean?.id) {
            val intent = Intent(this@MethodRecordActivity, MethodInvokeActivity::class.java)
            intent.putExtra(IntentKey.METHOD_ID_KEY, record.id)
            intent.putExtra(IntentKey.THREAD_NAME_KEY, mThreadName)
            this@MethodRecordActivity.startActivity(intent)
            return
        }
        mStack.push(Pair(mRecycler.computeVerticalScrollOffset(), adapter.getOrder()))
        loadData(record.id, true) {}
    }
}