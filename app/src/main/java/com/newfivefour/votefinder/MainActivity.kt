package com.newfivefour.votefinder

import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.newfivefour.votefinder.ViewUtils.pravda
import com.newfivefour.votefinder.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    companion object {
        val model = Model()
        val backstack:ArrayList<(m:Model) -> Unit> = arrayListOf()
        fun saveBackstack(f: (m:Model) -> Unit):Int {
            backstack.add(f)
            Log.d("HI", "$backstack")
            return backstack.size
        }
        fun runBackstackFunction(position: Int) {
            if(position>0 && backstack.size > position-1) backstack.removeAt(position-1)(MainActivity.model)
        }
        fun goBackInModel() {
            if(backstack.size > 0) backstack.removeAt(backstack.size-1)(MainActivity.model)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        setSupportActionBar(binding.toolbar)
        binding.model = model
        binding.utils = ViewUtils
        binding.updater = Updater

        if(MainActivity.model.constituencies.size()==0) {
        MainActivity.model.loading++
        EndPoints.divisionListObservable()
            .flatMap {
                model.divisions = it
                EndPoints.mpsObservable()
            }.flatMap {
                Log.d("TAG", "mps found")
                model.constituencies = it
                model.ck.clear()
                it.forEach {
                    model.ck.put(it.asJsonObject.get("mp_id").toString().removeSurrounding("\""),
                            it.asJsonObject)
                }
                it.forEach {
                    model.party_nums.put(it.asJsonObject.get("mp_id").toString().removeSurrounding("\""),
                            it.asJsonObject.get("mp_party_no").toString().removeSurrounding("\"").toInt())
                }
                EndPoints.divisionsDetailsObservable(model.uin)
            }.map {
                Updater.changeBillSquares(it.asJsonObject)
            }.subscribe({
            MainActivity.model.loading--
        },{
            MainActivity.model.loading--
        })
        }
    }

    override fun onBackPressed() {
        Log.d("HI", "Back pressed")
        if(backstack.size>0) goBackInModel()
        else super.onBackPressed()
    }

    override fun onCreateOptionsMenu(menu: Menu):Boolean = pravda {
        menuInflater.inflate(R.menu.menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean = when(item?.itemId) {
        R.id.action_refresh -> pravda { Updater.showAbout(true) }
        R.id.action_forward ->
            pravda { if(MainActivity.model.division_select_number<MainActivity.model.divisions.size()-1) Updater.changeBill(1) }
        R.id.action_back ->
            pravda { if(MainActivity.model.division_select_number>0) Updater.changeBill(-1) }
        else -> super.onOptionsItemSelected(item)
    }

}
