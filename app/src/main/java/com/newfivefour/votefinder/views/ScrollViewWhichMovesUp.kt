package com.newfivefour.votefinder.views

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ScrollView
import android.widget.TextView

class ScrollViewWhichMovesUp: ScrollView {
    constructor(context: Context) : super(context) { /*init(null, 0)*/ }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) { /*init(attrs, 0)*/ }
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) { /*init(attrs, defStyle)*/ }

    var change: Int = 0
    set(value) {
        field = value
        smoothScrollTo(0, 0)
    }

}