package com.newfivefour.votefinder.views

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView

class QuickRecyclerView : RecyclerView {
    constructor(context: Context) : super(context) { init(null, 0) }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) { init(attrs, 0) }
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) { init(attrs, defStyle) }

    var items = listOf<String>()
    set(value) {
        field = value
        adapter = MyRecycler(value)
    }

    fun init(attr: AttributeSet?, style: Int?) {
        layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    }

    inner class MyRecycler(val items: List<String>) : RecyclerView.Adapter<MyRecycler.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false)
            val vh = ViewHolder(TextView(parent.context))
            return vh
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.v.text = items[position]
        }

        override fun getItemCount():Int {
            return items.size
        }

        inner class ViewHolder(val v:TextView) : RecyclerView.ViewHolder(v) {
            //val text = v.findViewById<TextView>(android.R.id.text1)
        }
    }

}