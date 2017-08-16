package com.newfivefour.votefinder

import android.databinding.BaseObservable
import android.databinding.Bindable
import android.util.Log
import com.google.gson.JsonArray
import com.google.gson.JsonObject

class Model : BaseObservable() {
    var uin = ""
    var profile = ""
    var constituency = JsonObject()
    var constituencies: JsonArray = JsonArray()
        @Bindable get() = field
        set(value) {
            field = value
            notifyChange()
        }
    var ck = JsonObject()
    var divisions: JsonArray = JsonArray()
        @Bindable get() = field
        set(value) {
            field = value
            notifyChange()
            //notifyPropertyChanged(BR.divisions)
        }
    var division = JsonObject()
    var division_select_number = 0
        @Bindable get() = field
        set(value) {
            field = value
            notifyChange()
        }
    var allvotes: JsonArray = JsonArray()
        @Bindable get() = field
        set(value) {
            field = value
            notifyChange()
        }
    var show_profile = false
}
