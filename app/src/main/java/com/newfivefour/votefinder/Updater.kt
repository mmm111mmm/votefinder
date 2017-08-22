package com.newfivefour.votefinder

import android.util.Log
import com.google.gson.JsonObject
import io.reactivex.Observable
import io.reactivex.functions.BiFunction

object Updater {

    fun changeBill(i: Int) {
        MainActivity.model.division_select_number += i
        updateBill()
    }

    fun mpClicked(id: String) {
        Log.d("TAG", "Profile clicked")

        MainActivity.model.loading = true
        Observable.zip(
                EndPoints.mpDetails(id), Observable.just(JsonObject()),
                BiFunction{ details:JsonObject, _:JsonObject ->
                    MainActivity.model.loading = false
                    MainActivity.model.error = false
                    EndPoints.updateMPModel(details)
                    MainActivity.model.show_profile = true
                })
                .onErrorReturn {
                    MainActivity.model.loading = false
                    MainActivity.model.error = true
                }
                .subscribe()
    }

    fun toggleProfile() {
        MainActivity.model.show_profile = !MainActivity.model.show_profile
        Log.d("TAG", "Profile now " + MainActivity.model.show_profile)
    }

    fun updateBill() {
        val uin = MainActivity.model.divisions.get(MainActivity.model.division_select_number)
                .asJsonObject
                .get("uin")
                .asString
        Log.d("TAG", "bill change " + MainActivity.model.division_select_number)
        EndPoints.divisionDetails(uin).subscribe()
    }

    fun  showAbout(b: Boolean) {
        MainActivity.model.show_about = b
    }
}
