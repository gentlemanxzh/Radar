package com.addcn.monitor.activity.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.addcn.monitor.activity.MethodRecordLineView
import com.addcn.monitor.annotation.NoMethodRecord
import com.addcn.monitor.record.MethodRecordBean
import com.example.asmdemo.R

@NoMethodRecord
class MethodRecordAdapter(private var listener: OnItemClickListener? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val ITEM_TYPE_RECORD_METHOD_LEADER = 1
        const val ITEM_TYPE_RECORD_METHOD = 0

        //随机颜色
        val COLOR_LIST = listOf(
            Color.parseColor("#ffa502"),
            Color.parseColor("#ff7f50"),
            Color.parseColor("#ff6b81"),
            Color.parseColor("#747d8c"),
            Color.parseColor("#2f3542"),
            Color.parseColor("#2ed573"),
        )
    }

    //当前是否是顺序排序
    private var isOrder = true
    private var maxDuration: Long = 0
    private val data: MutableList<MethodRecordBean> = mutableListOf()
    var curMethod: MethodRecordBean? = null



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ITEM_TYPE_RECORD_METHOD_LEADER) {
            val inflate = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_method_leader, parent, false)
            MethodRecordLeaderViewHolder(inflate)
        } else {
            val inflate = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_method_recored, parent, false)
            MethodRecordViewHolder(inflate)
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = data[position]
        if (holder is MethodRecordLeaderViewHolder) {
            holder.bindData(data, position)
        } else if (holder is MethodRecordViewHolder) {
            holder.bindData(data, this.maxDuration, position)
        }
        holder.itemView.setOnClickListener {
            listener?.onItemClick(this, position)
        }
    }


    override fun getItemCount(): Int {
        return data.size
    }


    fun setList(list: List<MethodRecordBean>, bean: MethodRecordBean?) {
        curMethod = bean
        this.maxDuration = 0L
        this.data.clear()
        list.forEach {
            this.maxDuration = it.getDuration().coerceAtLeast(this.maxDuration)
            this.data.add(it)
        }
        notifyDataSetChanged()
    }


    fun sort(order: Boolean) {
        this.data.sortWith { data1, data2 ->
            if (order) {
                (data1.startTime - data2.startTime).toInt()
            } else {
                (data2.duration - data1.duration).toInt()
            }
        }
        notifyDataSetChanged()
    }

    fun clearList() {
        this.data.clear()
    }

    fun get(i: Int): MethodRecordBean {
        return data[i]
    }

    override fun getItemViewType(position: Int): Int {
        return if (data[position] == curMethod) {
            ITEM_TYPE_RECORD_METHOD_LEADER
        } else {
            ITEM_TYPE_RECORD_METHOD
        }
    }

    fun getOrder(): Boolean {
        return isOrder
    }

    fun setOrder(order: Boolean) {
        isOrder = order
    }


    class MethodRecordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val methodLineView: MethodRecordLineView = itemView.findViewById(R.id.mlv_method)
        private val tvMethodInfo: TextView = itemView.findViewById(R.id.tv_method_info)
        private val tvMethodInfoOut: TextView = itemView.findViewById(R.id.tv_method_info_out)
        private val flLineContainer: FrameLayout = itemView.findViewById(R.id.fl_line_container)


        fun bindData(bean: MethodRecordBean, maxTime: Long, index: Int) {
            val builder = StringBuilder()
            val className = bean.className
            builder.append(className.split("/").last())
            builder.append("(")
            builder.append(bean.getDuration())
            builder.append("ms)\n")
            builder.append(bean.methodName)
            builder.append(bean.desc)
            setData(bean.getDuration(), builder.toString(), maxTime, index)
        }

        fun setData(duration: Long, str: String, maxDuration: Long, index: Int) {
            var weight: Float = duration.toFloat()
            if (duration == maxDuration) {
                weight = maxDuration.coerceAtLeast(1L).toFloat()
            }
            val layoutParams = flLineContainer.layoutParams as LinearLayout.LayoutParams?
            layoutParams?.weight = weight
            val tvLayoutParams = tvMethodInfoOut.layoutParams as LinearLayout.LayoutParams?
            tvLayoutParams?.weight = (maxDuration - weight).coerceAtLeast(0f)


            if ((maxDuration / 2).toFloat() > weight) {
                this.tvMethodInfoOut.text = str
                this.tvMethodInfo.text = ""
            } else {
                this.tvMethodInfoOut.text = ""
                this.tvMethodInfo.text = str
            }

            //设置随机颜色
            val color = COLOR_LIST[index % COLOR_LIST.size]
            tvMethodInfoOut.setTextColor(color)
            methodLineView.setColor(color)
        }
    }


    class MethodRecordLeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val methodLineView: MethodRecordLineView = itemView.findViewById(R.id.mlv_method)
        private val tvMethodInfo: TextView = itemView.findViewById(R.id.tv_method_info)

        fun bindData(bean: MethodRecordBean, index: Int) {
            val text =
                "className:" + bean.className + "\nmethodName:" + bean.methodName + "\nmethodDesc:" + bean.desc + "\ndepth:" + bean.getDepth()
            tvMethodInfo.text = text
            val color = COLOR_LIST[index % COLOR_LIST.size]
            methodLineView.setColor(color)
        }
    }


    interface OnItemClickListener {
        fun onItemClick(adapter: MethodRecordAdapter, index: Int)
    }
}