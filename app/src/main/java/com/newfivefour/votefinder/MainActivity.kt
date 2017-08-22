package com.newfivefour.votefinder

import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.newfivefour.votefinder.databinding.ActivityMainBinding
import io.reactivex.Observable
import io.reactivex.functions.BiFunction

class MainActivity : AppCompatActivity() {

    companion object {
        val model = Model()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        binding.model = model
        binding.utils = Updater
        setSupportActionBar(binding.toolbar)

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

        MainActivity.model.loading = true
        Observable
                .zip(mps,EndPoints.divisionDetails("CD:2017-07-17:275"),
                BiFunction {
                    _:JsonArray, _:JsonElement ->
                    MainActivity.model.error = false
                    MainActivity.model.loading = false
                })
                .onErrorReturn {
                    e ->
                    MainActivity.model.loading = false
                    MainActivity.model.error = true
                    Log.d("HI z", ""+e)
                }
                .subscribe()

        EndPoints.divisionsList {
            MainActivity.model.error = false
            MainActivity.model.loading = false
            model.divisions = it
            if(model.divisions.size()>0)
            Log.d("TAG", model.divisions.get(0).asJsonObject.get("name").toString())
        }

        /*
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
        */
    }

    override fun onCreateOptionsMenu(menu: Menu):Boolean {
        val inflater = getMenuInflater()
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        Log.d("HI", "here")
        Updater.showAbout(true)
        if(item?.itemId == R.id.action_refresh) {
           Updater.showAbout(true)
        }
        return super.onOptionsItemSelected(item)
    }
}
