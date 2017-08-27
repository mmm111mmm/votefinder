package com.newfivefour.votefinder.views

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.view.animation.Animation
import com.newfivefour.votefinder.R
import com.newfivefour.votefinder.MainActivity
import android.widget.RelativeLayout

class DialogueBox: RelativeLayout {

    constructor(context: Context) : super(context) { init(null, 0) }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) { init(attrs, 0) }
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) { init(attrs, defStyle) }

    var backstackPosition: Int = -1
    var backAction: View.OnClickListener = View.OnClickListener { }
    var show: Boolean = false
    set(value) {
        field = value
        if(value) {
            visibility = View.VISIBLE
            ObjectAnimator.ofFloat(this, "alpha", 0f, 1.0f).start()
            this.backstackPosition = MainActivity.saveBackstack { this.backAction.onClick(this) }
        } else {
            val a = ObjectAnimator.ofFloat(this, "alpha", 1.0f, 0.0f)
            a.addListener(object: Animation.AnimationListener, Animator.AnimatorListener {
                override fun onAnimationRepeat(p0: Animation?) { }
                override fun onAnimationRepeat(p0: Animator?) { }
                override fun onAnimationEnd(p0: Animation?) { }
                override fun onAnimationEnd(p0: Animator?) {
                    visibility = View.GONE
                }
                override fun onAnimationStart(p0: Animation?) { }
                override fun onAnimationStart(p0: Animator?) { }
                override fun onAnimationCancel(p0: Animator?) { }
            })
            if(alpha!=0.0f) a.start()
        }
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        val a = context.obtainStyledAttributes(
                attrs, R.styleable.DivisionDisplayCount, defStyle, 0)
        a.recycle()
        setOnClickListener {
            MainActivity.runBackstackFunction(this.backstackPosition)
        }
        visibility = View.GONE
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
