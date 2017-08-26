package com.newfivefour.votefinder.views

import android.animation.ObjectAnimator
import android.databinding.DataBindingUtil
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.os.Bundle
import android.os.Parcelable
import com.newfivefour.votefinder.R
import com.newfivefour.votefinder.databinding.*
import com.newfivefour.votefinder.MainActivity
import com.newfivefour.votefinder.Updater
import android.view.ViewTreeObserver.OnGlobalLayoutListener

class DetailsBox: FrameLayout {
    constructor(context: Context) : super(context) { init(null, 0) }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) { init(attrs, 0) }
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) { init(attrs, defStyle) }

    var binding: MpDetailsBinding? = null
    var show:Boolean = false
        set(value) {
            field = value
            val paddingLeft = (binding!!.mpDetailsLinearlayout.layoutParams as FrameLayout.LayoutParams).leftMargin.toFloat()
            val offscreenX = binding!!.root.width.toFloat() * -1
            if(value && binding!!.root.x == offscreenX) {
                ObjectAnimator.ofFloat(binding!!.root, "x", offscreenX, paddingLeft).start()
            } else if(!value && binding!!.root.x == paddingLeft){
                ObjectAnimator.ofFloat(binding!!.root, "x", paddingLeft, offscreenX).start()
            }
        }

    private fun init(attrs: AttributeSet?, defStyle: Int) {

        val layoutInflator = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val binding = DataBindingUtil.inflate<MpDetailsBinding>(layoutInflator, R.layout.mp_details, this, true)
        binding.utils = Updater
        binding.model = MainActivity.model
        this.binding = binding
        viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                viewTreeObserver.removeOnGlobalLayoutListener(this)
                binding.root.x = (binding.root.width * -1).toFloat()
                binding.root.invalidate()
            }
        })
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        if (state is Bundle) {
            super.onRestoreInstanceState(state.getParcelable<Parcelable>("instanceState"))
            return
        }
        super.onRestoreInstanceState(state)
    }

    override fun onSaveInstanceState(): Parcelable? {
        val bundle = Bundle()
        bundle.putParcelable("instanceState", super.onSaveInstanceState())
        return bundle
    }
}
