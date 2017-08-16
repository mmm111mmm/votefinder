package com.newfivefour.votefinder

import android.util.Log

class Utils {
    fun changeBill(i: Int) {
        MainActivity.model.division_select_number += i
        EndPoints.divisionsDetailsObservable(
                MainActivity.model.divisions.get(MainActivity.model.division_select_number)
                        .asJsonObject
                        .get("uin").asString)
        Log.d("TAG", "bill change" + MainActivity.model.division_select_number + " " + i)
    }
}
