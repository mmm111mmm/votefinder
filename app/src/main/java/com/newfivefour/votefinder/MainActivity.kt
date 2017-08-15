package com.newfivefour.votefinder

import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.newfivefour.votefinder.databinding.ActivityMainBinding
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import org.joda.time.DateTime

class MainActivity : AppCompatActivity() {

    val model = Model()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        .model = model

        // vote finder

        val mps = EndPoints.mpsObservable
            .flatMap {
                Log.d("TAG", "mps found")
                model.constituencies = it
                var ck = JsonObject()
                it.forEach {
                    ck.add(it.asJsonObject.get("mp_id").toString(), it)
                }
                model.ck = ck
                Observable.just(it)
            }

        val divisionDetails = EndPoints.divisionsDetailsObservable("CD:2017-07-17:275")
            .flatMap {
                Log.d("TAG", "divisions")
                val vote = it.getAsJsonObject("result").getAsJsonArray("items").get(0).asJsonObject

                val division_date = DateTime.parse(vote.getAsJsonObject("date").get("_value").toString().replace("\"", ""))
                val division_dateLong = division_date.toDate().time
                val current_constituencies = model.constituencies.filter {
                    it.asJsonObject.getAsJsonArray("mp_statuses").filter {
                        val startLong = DateTime.parse((it.asJsonArray[0].toString().replace("\"", ""))).toDate().time
                        val endLong =
                                (if(!it.asJsonArray[1].isJsonNull) DateTime.parse((it.asJsonArray[1].toString().replace("\"", "")))
                                else DateTime.now()).toDate().time
                        division_dateLong in startLong..endLong
                    }.isNotEmpty()
                }

                var ayes = arrayListOf<String>()
                var noes = arrayListOf<String>()
                current_constituencies.forEach {
                    var c = it.asJsonObject
                    var member = vote.getAsJsonArray("vote").filter {
                        c.get("mp_id") == it.asJsonObject.get("id")
                    }
                    if(member.size != 0)
                        member.forEach {
                            if(it.asJsonObject.get("type").asString.indexOf("Aye")!=-1)
                                ayes.add(c.get("mp_id").asString)
                            if(it.asJsonObject.get("type").asString.indexOf("No")!=-1)
                                noes.add(c.get("mp_id").asString)
                        }
                }

                val not_in_house = model.constituencies.filter {
                    current_constituencies.indexOf(it)==-1
                }.map { it.asJsonObject.get("mp_id").asString }

                var absent = model.constituencies.filter {
                    not_in_house.indexOf(it.asJsonObject.get("mp_id").asString)==-1
                    && ayes.indexOf(it.asJsonObject.get("mp_id").asString)==-1
                    && noes.indexOf(it.asJsonObject.get("mp_id").asString)==-1
                }.map { it.asJsonObject.get("mp_id") }

                model.division = vote
                model.allvotes.addAll(Gson().fromJson<JsonArray>(Gson().toJson(
                        listOf(ayes, noes, absent, not_in_house)
                ), JsonArray::class.java))

                Observable.just(it)
            }

        Observable.zip(mps,divisionDetails, BiFunction { _:JsonArray, _:JsonObject -> Unit }).subscribe()

        EndPoints.divisionsList {
            model.divisions = it
            Log.d("TAG", model.divisions.get(0).asJsonObject.get("name").toString())
        }

        EndPoints.mpDetails {
            Log.d("TAG", it.toString())
            model.constituency = it
        }

        // gb postcode finder

        EndPoints.constituencySearch {
            Log.d("TAG", it.toString())
        }

        EndPoints.constitucyDetails {
            Log.d("TAG", it.toString())
        }

        EndPoints.postcodeToLatLon {
            Log.d("TAG", it.toString())
        }

    }
}
