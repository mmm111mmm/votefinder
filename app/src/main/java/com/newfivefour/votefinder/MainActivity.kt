package com.newfivefour.votefinder

import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.newfivefour.votefinder.databinding.ActivityMainBinding
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import org.joda.time.DateTime

class MainActivity : AppCompatActivity() {

    companion object {
        val model = Model()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        binding.model = model
        binding.utils = Utils


        // vote finder

        val mps = EndPoints.mpsObservable
            .flatMap {
                Log.d("TAG", "mps found")
                model.constituencies = it
                var ck = JsonObject()
                it.forEach {
                    ck.add(it.asJsonObject.get("mp_id").toString().replace("\"", ""), it)
                }
                model.ck = ck
                Observable.just(it)
            }


        Observable.zip(mps,EndPoints.divisionDetails("CD:2017-07-17:275"),
                BiFunction { _:JsonArray, _:JsonElement -> Unit }).subscribe()

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
