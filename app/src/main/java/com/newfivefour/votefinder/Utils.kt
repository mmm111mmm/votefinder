package com.newfivefour.votefinder

import android.util.Log

object Utils {
    fun changeBill(i: Int) {
        MainActivity.model.division_select_number += i
        updateBill()
    }
    fun profileClicked(id: String) {
    }

    fun updateBill() {
        val uin = MainActivity.model.divisions.get(MainActivity.model.division_select_number)
                .asJsonObject
                .get("uin")
                .asString
        Log.d("TAG", "bill change " + MainActivity.model.division_select_number)
        EndPoints.divisionDetails(uin).subscribe()
    }
}
