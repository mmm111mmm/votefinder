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
import com.newfivefour.votefinder.MainActivity
import android.databinding.DataBindingUtil
import android.util.Log
import com.newfivefour.votefinder.Updater
import com.newfivefour.votefinder.databinding.DivisionMpBlockBinding
import com.newfivefour.votefinder.databinding.DivisionMpHeaderBinding


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
    var layout: View? = null
    var flatVotes: ArrayList<String> = arrayListOf()
    var votes: List<List<String>> = listOf(listOf())
        set(value) {
            field = value
            flatVotes.clear()
            value.flatten().toCollection(flatVotes)
            if(value.size>0) flatVotes.add(0, "s1")
            if(value.size>0) flatVotes.add(value[0].size+1, "s2")
            if(value.size>0) flatVotes.add(value[0].size + value[1].size +2, "s3")
            if(value.size==4 && value[3].size>0) flatVotes.add(value[0].size + value[1].size + value[2].size + 3, "s4")

            val grid: Int = (dpWidth / 28).toInt()
            val padding = dpWidth - (24 * grid)
            layout?.setPadding((padding / 2).toInt(), 0, (padding / 2).toInt(), 0)

            val rc: RecyclerView? = layout?.findViewById<RecyclerView>(R.id.division_display_count_recyclerview)
            rc?.adapter = MyRecycler()
            val glm = GridLayoutManager(context, grid, GridLayoutManager.VERTICAL, false)
            glm.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return if(flatVotes[position].startsWith("s")) grid else 1
                }
            }
            rc?.layoutManager = glm
        }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.DivisionDisplayCount, defStyle, 0)
        a.recycle()

        val layoutInflator = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        this.layout = layoutInflator.inflate(R.layout.division_display_count, this)
        layout?.findViewById<RecyclerView>(R.id.division_display_count_recyclerview)

        val outMetrics = DisplayMetrics()
        val act = layout?.context as Activity
        val display = act.windowManager.defaultDisplay
        display.getMetrics(outMetrics)
        val density = resources.displayMetrics.density
        this.dpWidth = outMetrics.widthPixels / density
    }

    inner class MyRecycler : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        override fun getItemViewType(position: Int): Int {
            return if(flatVotes[position].startsWith("s"))
                flatVotes[position].subSequence(1..flatVotes[position].length-1).toString().toInt()
            else 0
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = when(viewType) {
            0 -> {
                val v = DataBindingUtil.inflate<DivisionMpBlockBinding>(LayoutInflater.from(parent.context), R.layout.division_mp_block, parent, false)
                ViewHolder(v.root)
            } else -> {
                val v = DataBindingUtil.inflate<DivisionMpHeaderBinding>(LayoutInflater.from(parent.context), R.layout.division_mp_header, parent, false)
                SectionViewHolder(v, when(viewType) {
                    1 -> { "Ayes (${votes[0].size})"}
                    2 -> { "Noes (${votes[1].size})"}
                    3 -> { "Absent (${votes[2].size})"}
                    4 -> { "Hadn't entered parliament (${votes[3].size})"}
                    else -> { ""}
                })
            }
        }

        val party_ids = MainActivity.model.party_nums
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int): Unit = when (holder) {
            is ViewHolder -> {
                holder.root.setOnClickListener {
                    val mpid = MainActivity.model.ck[flatVotes[position]]?.get("mp_id").toString()
                    Updater.mpClicked(mpid.removeSurrounding("\""))
                }
                holder.root.setBackgroundColor( when (party_ids[flatVotes[position]]) {
                        15 -> { lab } 29 -> { snp } 17 -> { lib } 30 -> { sinn }
                        7 -> { dup } 4 -> { cons } 44 -> { green } 22 -> { plaid }
                        else -> unknown
                    })
            }
            is SectionViewHolder -> {}
            else -> {}
        }

        override fun getItemCount(): Int {
            return flatVotes.size
        }

        inner class ViewHolder(val root: View) : RecyclerView.ViewHolder(root)
        inner class SectionViewHolder(binding: DivisionMpHeaderBinding, title: String) : RecyclerView.ViewHolder(binding.root) {
            init { binding.divisionMpBlockHeaderTextview.text = title }
        }
    }

    public override fun onRestoreInstanceState(state: Parcelable) = when (state) {
        is Bundle -> super.onRestoreInstanceState(state.getParcelable<Parcelable>("instanceState"))
        else -> super.onRestoreInstanceState(state)
    }

    public override fun onSaveInstanceState(): Parcelable? {
        val bundle = Bundle()
        bundle.putParcelable("instanceState", super.onSaveInstanceState())
        return bundle
    }

}
