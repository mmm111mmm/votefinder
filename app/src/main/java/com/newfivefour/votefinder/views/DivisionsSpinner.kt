package com.newfivefour.votefinder.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.google.gson.JsonArray
import com.newfivefour.votefinder.R
import android.widget.AdapterView.OnItemSelectedListener
import com.newfivefour.votefinder.MainActivity
import com.newfivefour.votefinder.Utils


class DivisionsSpinner : FrameLayout {
    constructor(context: Context) : super(context) { init(null, 0) }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) { init(attrs, 0) }
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) { init(attrs, defStyle) }

    var layout:View? = null
    var divisions:JsonArray = JsonArray()
        set(value) {
            val adapter = ArrayAdapter<String>(context, android.R.layout.simple_spinner_item,
                value.map { it.asJsonObject.get("name").asString }
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            this.layout?.findViewById<Spinner>(R.id.planets_spinner)?.adapter = adapter
            this.layout?.findViewById<Spinner>(R.id.planets_spinner)?.onItemSelectedListener =
                    object : OnItemSelectedListener {
                        override fun onItemSelected(p: AdapterView<*>, v: View?, pos: Int, id: Long) {
                            if(MainActivity.model.division_select_number != pos) {
                                MainActivity.model.division_select_number = pos
                                Utils.updateBill()
                            }
                        }
                        override fun onNothingSelected(parent: AdapterView<*>) {}
                    }

        }
    var selected:Int = 0
        set(value) {
            this.layout?.findViewById<Spinner>(R.id.planets_spinner)?.setSelection(value)
        }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.DivisionsSpinner, defStyle, 0)
        a.recycle()

        val layoutInflator = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        this.layout = layoutInflator.inflate(R.layout.divisions_spinner, this)
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
}
