package com.newfivefour.votefinder

import android.databinding.BaseObservable
import android.databinding.Bindable
import com.google.gson.JsonArray
import com.google.gson.JsonObject

class Model : BaseObservable() {

    var error: Boolean = false
        @Bindable get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.error)
        }
    var loading: Int = 0
        @Bindable get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.loading)
        }
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
    var allvotes = listOf<List<String>>()
        @Bindable get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.allvotes)
        }
    var show_profile = false
        @Bindable get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.show_profile)
        }
    var show_about = false
        @Bindable get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.show_about)
        }
    var uin = "CD:2017-07-17:275"
    fun date():String { return uin.replace("CD:","").replace(":...$".toRegex(), "") }
    var profile = ""
    var constituency = JsonObject()
        @Bindable get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.constituency)
        }
    var changeBill: Boolean = false
        @Bindable get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.changeBill)
        }
    var billChanged: Int = 0
        @Bindable get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.billChanged)
        }
    var ck = hashMapOf<String, JsonObject>()
    var party_nums = hashMapOf<String, Int>()
    var division = JsonObject()
}
