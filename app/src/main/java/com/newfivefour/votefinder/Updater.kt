package com.newfivefour.votefinder

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import org.joda.time.DateTime

object Updater {

    fun showAbout(b: Boolean) {
        if (b) MainActivity.saveBackstack { m -> m.show_about = !b }
        MainActivity.model.show_about = b
    }

    fun updateMPModel(a: JsonObject) {
        Log.d("TAG", a.toString())
        MainActivity.model.loading = false
        MainActivity.model.constituency = a
    }

    fun changeBill(i: Int) = changeBillExactNumber(MainActivity.model.division_select_number + i)

    fun changeBillExactNumber(pos: Int) {
        val allvotes = MainActivity.model.allvotes
        val selected = MainActivity.model.division_select_number
        MainActivity.saveBackstack {
            MainActivity.model.allvotes = allvotes
            MainActivity.model.division_select_number = selected
        }
        MainActivity.model.division_select_number = pos
        updateBill()
    }

    fun mpClicked(id: String) {
        Log.d("TAG", "Profile clicked")
        EndPoints.mpDetails(id)
                .map {
                    updateMPModel(it)
                    MainActivity.saveBackstack { m -> m.show_profile = false }
                    MainActivity.model.show_profile = true
                    MainActivity.model.loading = false
                    MainActivity.model.error = false
                }.subscribe()
    }

    private fun updateBill() {
        Log.d("TAG", "bill change " + MainActivity.model.division_select_number)
        val uin = MainActivity.model.divisions.get(MainActivity.model.division_select_number)
                .asJsonObject
                .get("uin")
                .asString
        EndPoints.divisionDetails(uin)
                .observeOn(Schedulers.newThread())
                .map { changeBillSquares(it) }
                .subscribe()
    }

    fun changeBillSquares(it: JsonObject) {
        if (it.get("result") != null) {
            Log.d("TAG", "divisions")
            val vote = it.getAsJsonObject("result").getAsJsonArray("items").get(0).asJsonObject

            val division_date = DateTime.parse(vote.getAsJsonObject("date").get("_value").toString().replace("\"", ""))
            val division_dateLong = division_date.toDate().time
            val current_constituencies = MainActivity.model.constituencies.filter {
                it.asJsonObject.getAsJsonArray("mp_statuses").filter {
                    val startLong = DateTime.parse((it.asJsonArray[0].toString().replace("\"", ""))).toDate().time
                    val endLong =
                            (if (!it.asJsonArray[1].isJsonNull) DateTime.parse((it.asJsonArray[1].toString().replace("\"", "")))
                            else DateTime.now()).toDate().time
                    division_dateLong in startLong..endLong
                }.isNotEmpty()
            }

            val ayes = arrayListOf<String>()
            val noes = arrayListOf<String>()
            current_constituencies.forEach {
                val c = it.asJsonObject
                val member = vote.getAsJsonArray("vote").filter {
                    c.get("mp_id") == it.asJsonObject.get("id")
                }
                if (member.isNotEmpty())
                    member.forEach {
                        if (it.asJsonObject.get("type").asString.indexOf("Aye") != -1)
                            ayes.add(c.get("mp_id").asString)
                        if (it.asJsonObject.get("type").asString.indexOf("No") != -1)
                            noes.add(c.get("mp_id").asString)
                    }
            }

            val not_in_house:List<String> = MainActivity.model.constituencies.filter {
                current_constituencies.indexOf(it) == -1
            }.map { it.asJsonObject.get("mp_id").asString }

            val absent:List<String> = MainActivity.model.constituencies.filter {
                not_in_house.indexOf(it.asJsonObject.get("mp_id").asString) == -1
                        && ayes.indexOf(it.asJsonObject.get("mp_id").asString) == -1
                        && noes.indexOf(it.asJsonObject.get("mp_id").asString) == -1
            }.map { it.asJsonObject.get("mp_id").asString }

            MainActivity.model.division = vote
            MainActivity.model.allvotes = listOf(ayes, noes, absent, not_in_house)
        }
        MainActivity.model.loading = false
        MainActivity.model.error = false
    }

}
