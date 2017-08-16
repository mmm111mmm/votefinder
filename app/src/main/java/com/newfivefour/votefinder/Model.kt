package com.newfivefour.votefinder

import android.databinding.BaseObservable
import android.databinding.Bindable
import com.google.gson.JsonArray
import com.google.gson.JsonObject

class Model : BaseObservable() {
    var constituencies: JsonArray = JsonArray()
        @Bindable get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.constituencies)
        }
    var divisions: JsonArray = JsonArray()
        @Bindable get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.divisions)
        }
    var division_select_number = 0
        @Bindable get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.division_select_number)
        }
    var allvotes: JsonArray = JsonArray()
        @Bindable get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.allvotes)
        }
    var show_profile = false
    var uin = ""
    var profile = ""
    var constituency = JsonObject()
    var ck = JsonObject()
    var division = JsonObject()
}
