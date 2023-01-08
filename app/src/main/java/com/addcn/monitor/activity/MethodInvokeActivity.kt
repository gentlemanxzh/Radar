package com.addcn.monitor.activity

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.addcn.monitor.activity.adapter.MethodRecordAdapter
import com.addcn.monitor.record.MethodRecordBean
import com.addcn.monitor.record.MethodRecordManager
import com.example.asmdemo.R

class MethodInvokeActivity : AppCompatActivity(), MethodRecordAdapter.OnItemClickListener {

    private val mRecycler: RecyclerView by lazy { findViewById(R.id.rv_method) }
    private val mAdapter = MethodRecordAdapter(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_method_record)
        mRecycler.layoutManager = LinearLayoutManager(this)
        mRecycler.adapter = mAdapter
        findViewById<TextView>(R.id.tv_method_title).text = "方法调用栈"
        findViewById<TextView>(R.id.tv_sort_list).visibility = View.INVISIBLE
        findViewById<ImageView>(R.id.iv_back).setOnClickListener {
            onBackPressed()
        }
        initData()
    }

    private fun initData() {
        val extras = intent.extras
        val methodKey = extras?.getString("METHOD_ID_KEY")
        if (!TextUtils.isEmpty(methodKey)) {
            val recorderMap = MethodRecordManager.getRecorderMap()
            val threadName = extras?.getString("THREAD_NAME_KEY") ?: ""
            val iMethodRecorder = recorderMap[threadName] ?: return
            val methodRecordBean = iMethodRecorder.methodRecordMap[methodKey]
            val data = mutableListOf<MethodRecordBean>()
            if (methodRecordBean != null) {
                data.add(methodRecordBean)
                var parentId = methodRecordBean.getParentId()
                while (parentId != null) {
                    if (parentId.isEmpty()) {
                        break
                    }
                    val bean = iMethodRecorder.methodRecordMap[parentId]
                    bean?.let {
                        data.add(it)
                    }
                    parentId = bean?.getParentId()
                }
            }
            data.reverse()
            mAdapter.setList( data, null, )

        } else {
            finish()
        }
    }

    override fun onItemClick(adapter: MethodRecordAdapter, index: Int) {
        if (adapter.itemCount == index + 1) {
            this@MethodInvokeActivity.finish()
            return
        }
        val curMethod = adapter.curMethod
        val threadName = curMethod?.threadName ?: return
        val methodKey = curMethod.id ?: return
        MethodRecordActivity.start(this@MethodInvokeActivity, threadName, methodKey)
    }

}