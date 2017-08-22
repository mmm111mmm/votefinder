package com.newfivefour.votefinder.views

import android.animation.ObjectAnimator
import android.databinding.DataBindingUtil
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.View
import com.newfivefour.votefinder.R
import com.newfivefour.votefinder.databinding.*
import com.newfivefour.votefinder.MainActivity
import com.newfivefour.votefinder.Updater


class DetailsBox: FrameLayout {
    constructor(context: Context) : super(context) { init(null, 0) }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) { init(attrs, 0) }
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) { init(attrs, defStyle) }

    var binding: MpDetailsBinding? = null
    var layout:View? = null
    var show:Boolean = false
        set(value) {
            field = value

            val originalX = binding!!.root.x + binding!!.root.paddingLeft
            val paddingLeft = binding!!.root.paddingLeft.toFloat()
            val offscreenX = (binding!!.root.width.toFloat() * -1).toFloat()
            Log.d("HI", ""+originalX + " " + offscreenX)
            binding!!.root.x = (offscreenX*-1).toFloat()
            layout?.visibility = if(value) View.VISIBLE else View.GONE

            ObjectAnimator.ofFloat(binding!!.root, "x", offscreenX, paddingLeft).start()

            //val pvhX = PropertyValuesHolder.ofFloat("x", 50f);
            //val pvhY = PropertyValuesHolder.ofFloat("y", 100f);
            //ObjectAnimator.ofPropertyValuesHolder(layout!!, pvhX, pvhY).start()
        }


    private fun init(attrs: AttributeSet?, defStyle: Int) {

        val layoutInflator = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val binding = DataBindingUtil.inflate<MpDetailsBinding>(layoutInflator, R.layout.mp_details, this, true)
        binding.utils = Updater
        binding.model = MainActivity.model
        this.binding = binding
        this.layout = binding.root
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
