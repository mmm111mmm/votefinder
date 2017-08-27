package com.newfivefour.votefinder.views

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.os.Bundle
import android.os.Parcelable
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.newfivefour.votefinder.R
import android.view.ViewGroup
import android.util.DisplayMetrics
import android.widget.TextView
import com.newfivefour.votefinder.MainActivity
import android.databinding.DataBindingUtil
import com.newfivefour.votefinder.Updater
import com.newfivefour.votefinder.databinding.DivisionMpBlockBinding


class DivisionDisplayCount : FrameLayout {

    constructor(context: Context) : super(context) { init(null, 0) }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) { init(attrs, 0) }
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) { init(attrs, defStyle) }

    var dpWidth: Float = 0f
    val lab = resources.getColor(R.color.lab)
    val snp = resources.getColor(R.color.snp)
    val lib = resources.getColor(R.color.lib)
    val sinn = resources.getColor(R.color.sinn)
    val dup = resources.getColor(R.color.dup)
    val cons = resources.getColor(R.color.cons)
    val green = resources.getColor(R.color.green)
    val plaid = resources.getColor(R.color.plaid)
    val unknown = resources.getColor(R.color.unknown_party)
    var size:Int = 0
    var layout:View? = null
    var title:String? = ""
        set(value) {
            val tv:TextView? = layout?.findViewById<TextView>(R.id.division_display_count_title_textview)
            tv?.text = value
        }
    var votes:List<String>? = listOf("")
        set(value) {
            field = value
            this.size = value?.size ?: 0

            val grid:Int = (dpWidth / 28).toInt()
            val padding = dpWidth - (24*grid)
            layout?.setPadding((padding/2).toInt(), 0, (padding/2).toInt(), 0)

            val rc:RecyclerView? = layout?.findViewById<RecyclerView>(R.id.division_display_count_recyclerview)
            rc?.adapter = MyRecycler()
            rc?.layoutManager = GridLayoutManager(context, grid, GridLayoutManager.VERTICAL, false)
        }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.DivisionDisplayCount, defStyle, 0)
        a.recycle()

        val layoutInflator = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        this.layout = layoutInflator.inflate(R.layout.division_display_count, this)
        layout?.findViewById<RecyclerView>(R.id.division_display_count_recyclerview)
                ?.isNestedScrollingEnabled = false

        val outMetrics = DisplayMetrics()
        val act = layout?.context as Activity
        val display = act.windowManager.defaultDisplay
        display.getMetrics(outMetrics)
        val density = resources.displayMetrics.density
        this.dpWidth = outMetrics.widthPixels / density
    }

    inner class MyRecycler : RecyclerView.Adapter<MyRecycler.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val v= DataBindingUtil.inflate<DivisionMpBlockBinding>(LayoutInflater.from(parent.context), R.layout.division_mp_block, parent, false)
            val vh = ViewHolder(v)
            return vh
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val id = MainActivity.model.party_nums[votes!![position]]
            holder.root.setOnClickListener { _ ->
                val id = MainActivity.model.ck[votes!![position]]?.get("mp_id").toString()
                Updater.mpClicked(id.removeSurrounding("\""))
            }
            holder.root.setBackgroundColor(
                when(id) {
                    15 -> lab
                    29 -> snp
                    17 -> lib
                    30 -> sinn
                    7 -> dup
                    4 -> cons
                    44 -> green
                    22 -> plaid
                    else -> unknown })
        }

        override fun getItemCount():Int = size

        inner class ViewHolder(itemView: DivisionMpBlockBinding) : RecyclerView.ViewHolder(itemView.root) {
            val root:View = itemView.root
        }
    }

    public override fun onRestoreInstanceState(state: Parcelable) = when(state) {
        is Bundle ->  super.onRestoreInstanceState(state.getParcelable<Parcelable>("instanceState"))
        else -> super.onRestoreInstanceState(state)
    }

    public override fun onSaveInstanceState(): Parcelable? {
        val bundle = Bundle()
        bundle.putParcelable("instanceState", super.onSaveInstanceState())
        return bundle
    }

}
