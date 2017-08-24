package com.newfivefour.votefinder

import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.newfivefour.votefinder.databinding.ActivityMainBinding
import io.reactivex.schedulers.Schedulers

class MainActivity : AppCompatActivity() {

    companion object {
        val model = Model()
        val backstack:ArrayList<(m:Model) -> Unit> = arrayListOf()
        fun saveBackstack(f: (m:Model) -> Unit) = backstack.add(f)
    }

    override fun onBackPressed() {
        if(backstack.size>0) backstack.removeAt(backstack.size-1)(MainActivity.model)
        else super.onBackPressed()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        setSupportActionBar(binding.toolbar)
        binding.model = model
        binding.utils = Updater

        MainActivity.model.loading = true
        EndPoints.mpsObservable
            .map {
                Log.d("TAG", "mps found")
                model.constituencies = it
                model.ck.clear()
                it.forEach {
                    model.ck.put(it.asJsonObject.get("mp_id").toString().removeSurrounding("\""), it.asJsonObject)
                }
                it.forEach {
                    model.party_nums.put(it.asJsonObject.get("mp_id").toString().removeSurrounding("\""),
                            it.asJsonObject.get("mp_party_no").toString().removeSurrounding("\"").toInt())
                }
            }
            .observeOn(Schedulers.newThread())
            .flatMap { EndPoints.divisionDetails("CD:2017-07-17:275") }
            .observeOn(Schedulers.newThread())
            .map { Updater.changeBillSquares(it.asJsonObject) }
            .subscribe {
                MainActivity.model.loading = false
                MainActivity.model.error = true
            }

        EndPoints.divisionsList { model.divisions = it }

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
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item?.itemId == R.id.action_refresh) Updater.showAbout(true)
        return super.onOptionsItemSelected(item)
    }
}
