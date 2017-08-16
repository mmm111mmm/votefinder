package com.newfivefour.votefinder.views

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.os.Bundle
import android.os.Parcelable
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.google.gson.JsonArray
import com.newfivefour.votefinder.R
import android.view.ViewGroup
import android.util.DisplayMetrics
import android.util.Log
import android.widget.TextView
import com.newfivefour.votefinder.MainActivity
import com.newfivefour.votefinder.Model


class DivisionDisplayCount : FrameLayout {
    val model = Model()

    constructor(context: Context) : super(context) { init(null, 0) }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) { init(attrs, 0) }
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) { init(attrs, defStyle) }

    var layout:View? = null
    var title:String? = ""
        set(value) {
            val tv:TextView? = layout?.findViewById<TextView>(R.id.division_display_count_title_textview)
            tv?.text = value
        }
    var votes:JsonArray? = JsonArray()
        set(value) {
            field = value

            Log.d("TAG in votes", value?.size().toString())

            val act = layout?.context as Activity
            val display = act.windowManager.defaultDisplay
            val outMetrics = DisplayMetrics()
            display.getMetrics(outMetrics)

            val density = resources.displayMetrics.density
            val dpWidth = outMetrics.widthPixels / density
            val grid:Int = (dpWidth / 28).toInt()
            val padding = dpWidth - (26*grid)
            layout?.setPadding((padding/2).toInt(), 0, (padding/2).toInt(), 0)

            val rc:RecyclerView? = layout?.findViewById<RecyclerView>(R.id.division_display_count_recyclerview)
            rc?.adapter = MyRecycler()
            rc?.layoutManager = GridLayoutManager(context, grid, GridLayoutManager.VERTICAL, false)

            Log.d("TAG", rc?.width.toString() + " " + dpWidth.toString() + " " + "" + (28 * grid) + " " + padding)
        }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        val a = context.obtainStyledAttributes(
                attrs, R.styleable.DivisionDisplayCount, defStyle, 0)
        a.recycle()

        val layoutInflator = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        this.layout = layoutInflator.inflate(R.layout.division_display_count, this)
    }

    public override fun onRestoreInstanceState(state: Parcelable) {
        if (state is Bundle) {
            super.onRestoreInstanceState(state.getParcelable<Parcelable>("instanceState"))
            return
        }
        super.onRestoreInstanceState(state)
    }

    public override fun onSaveInstanceState(): Parcelable? {
        val bundle = Bundle()
        bundle.putParcelable("instanceState", super.onSaveInstanceState())
        return bundle
    }

    inner class MyRecycler : RecyclerView.Adapter<MyRecycler.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.division_mp_block, parent, false)
            val vh = ViewHolder(v)
            return vh
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.block.setBackgroundColor(Color.RED)
            var id = MainActivity.model.ck[votes?.get(position)!!.asString].asJsonObject.get("mp_party_no").toString().replace("\"", "")
            holder.block.setBackgroundColor(if(id=="15") Color.RED
            else if(id=="29") resources.getColor(R.color.snp) // SNP
            else if(id=="17") resources.getColor(R.color.lib) // Lib
            else if(id=="30") resources.getColor(R.color.sinn) // Sinn Fein
            else if(id=="7") resources.getColor(R.color.dup) // DUP
            else if(id=="4") resources.getColor(R.color.cons) // Cons
            else if(id=="44") resources.getColor(R.color.green) // Green
            else if(id=="22") resources.getColor(R.color.plaid) // Plaid
            else resources.getColor(R.color.unknown_party))

            //holder.textView.text = "Stuff, innit. " + position
        }

        override fun getItemCount():Int {
            return if(votes!=null) votes!!.size() else 0
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            public val block: View

            init {
                block = itemView.findViewById<View>(R.id.division_mp_block_view)
            }
        }
    }
}
