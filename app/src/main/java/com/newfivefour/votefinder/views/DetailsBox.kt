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
import com.newfivefour.votefinder.R
import com.newfivefour.votefinder.databinding.*
import com.newfivefour.votefinder.MainActivity
import com.newfivefour.votefinder.Updater
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import com.newfivefour.votefinder.ViewUtils

class DetailsBox: FrameLayout {
    constructor(context: Context) : super(context) { init(null, 0) }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) { init(attrs, 0) }
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) { init(attrs, defStyle) }

    var backstackPosition: Int = -1
    var binding: MpDetailsBinding? = null
    var show:Boolean = false
        set(value) {
            field = value
            val offscreenX = binding!!.root.height.toFloat() * -1
            Log.d("HI", ""+binding!!.root.x+" "+value+" "+offscreenX)
            if(value && binding!!.root.y < 0) {
                ObjectAnimator.ofFloat(binding!!.root, "y", offscreenX, 0f).start()
                if(backstackPosition==-1) backstackPosition = MainActivity.saveBackstack { m -> m.show_profile = false }
            } else if(!value && binding!!.root.y == 0f && offscreenX<0){
                Log.d("HI", "animating off")
                ObjectAnimator.ofFloat(binding!!.root, "y", 0f, offscreenX).start()
            }
        }


    private fun init(attrs: AttributeSet?, defStyle: Int) {
        val layoutInflator = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val binding = DataBindingUtil.inflate<MpDetailsBinding>(layoutInflator, R.layout.mp_details, this, true)
        binding.utils = ViewUtils
        binding.updater = Updater
        binding.model = MainActivity.model
        this.binding = binding
        viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                viewTreeObserver.removeOnGlobalLayoutListener(this)
                binding.root.y = (binding.root.height * -1).toFloat()
                show = show
                binding.root.invalidate()
            }
        })
        binding.mpDetailsLinearlayout.setOnClickListener {
            MainActivity.runBackstackFunction(this.backstackPosition)
        }
    }

    override fun onRestoreInstanceState(state: Parcelable) = when(state) {
        is Bundle -> {
            super.onRestoreInstanceState(state.getParcelable<Parcelable>("instanceState"))
            backstackPosition = state.getInt("backstackPosition")
        }
        else -> super.onRestoreInstanceState(state)
    }

    override fun onSaveInstanceState(): Parcelable? {
        val bundle = Bundle()
        bundle.putParcelable("instanceState", super.onSaveInstanceState())
        bundle.putInt("backstackPosition", backstackPosition)
        return bundle
    }
}
