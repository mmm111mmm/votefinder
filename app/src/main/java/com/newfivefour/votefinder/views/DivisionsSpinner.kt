package com.newfivefour.votefinder.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.widget.*
import com.google.gson.JsonArray
import com.newfivefour.votefinder.R
import android.widget.AdapterView.OnItemSelectedListener
import com.newfivefour.votefinder.MainActivity
import com.newfivefour.votefinder.Updater


class DivisionsSpinner : FrameLayout {
    constructor(context: Context) : super(context) { init(null, 0) }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) { init(attrs, 0) }
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) { init(attrs, defStyle) }

    var layout:View? = null
    var divisions:JsonArray = JsonArray()
        set(value) {
            val adapter = ArrayAdapter<String>(context, R.layout.divisions_spinner_item,
                    android.R.id.text1,
                value.map {
                    val d = it.asJsonObject.get("uin").toString()
                            .removeSurrounding("\"")
                            .replace("CD:", "")
                            .replace(":...$".toRegex(), "")
                    val v = it.asJsonObject.get("name").toString().removeSurrounding("\"")
                    "$d - $v"
                }
            )
            adapter.setDropDownViewResource(R.layout.divisions_spinner_item)
            this.layout?.findViewById<Spinner>(R.id.planets_spinner)?.adapter = adapter
            this.layout?.findViewById<Spinner>(R.id.planets_spinner)?.onItemSelectedListener =
                    object : OnItemSelectedListener {
                        override fun onItemSelected(p: AdapterView<*>, v: View?, pos: Int, id: Long) {
                            if(MainActivity.model.division_select_number != pos) {
                                Updater.changeBillExactNumber(pos)
                            }
                        }
                        override fun onNothingSelected(parent: AdapterView<*>) {}
                    }

        }
    var selected:Int = 0
        set(value) {
            this.layout?.findViewById<Spinner>(R.id.planets_spinner)?.setSelection(value)
        }

    var clicked:Boolean = false
        set(value) {
            field = value
            layout?.findViewById<Spinner>(R.id.planets_spinner)!!.performClick()
        }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.DivisionsSpinner, defStyle, 0)
        a.recycle()

        val layoutInflator = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        this.layout = layoutInflator.inflate(R.layout.divisions_spinner, this)
    }

    public override fun onRestoreInstanceState(state: Parcelable) = when(state) {
        is Bundle -> super.onRestoreInstanceState(state.getParcelable<Parcelable>("instanceState"))
        else -> super.onRestoreInstanceState(state)
    }

    public override fun onSaveInstanceState(): Parcelable? {
        val bundle = Bundle()
        bundle.putParcelable("instanceState", super.onSaveInstanceState())
        return bundle
    }
}
