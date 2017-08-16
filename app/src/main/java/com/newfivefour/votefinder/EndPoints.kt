package com.newfivefour.votefinder

import android.support.annotation.NonNull
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.joda.time.DateTime
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

object EndPoints {

    val repo = Retrofit.Builder().baseUrl("https://newfivefour.com:3000")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()!!

    interface DivisionsList { @GET("/divisions") fun divisions(): Observable<JsonArray> }
    val divisionListObservable: Observable<JsonArray> = repo.create(DivisionsList::class.java)
        .divisions()
        .subscribeOn(Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread())
    fun divisionsList(@NonNull f: (JsonArray) -> Unit) = divisionListObservable.subscribe(f)

    interface DivisionDetails { @GET("/vote") fun division(@Query("uin") uin: String): Observable<JsonObject> }
    fun divisionsDetailsObservable(uin:String):Observable<JsonObject> = repo.create(DivisionDetails::class.java)
        .division(uin)
        .subscribeOn(Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread())
    fun divisionDetails(uin:String):Observable<JsonElement> {
        return divisionsDetailsObservable(uin)
            .flatMap {
                Log.d("TAG", "divisions")
                val vote = it.getAsJsonObject("result").getAsJsonArray("items").get(0).asJsonObject

                val division_date = DateTime.parse(vote.getAsJsonObject("date").get("_value").toString().replace("\"", ""))
                val division_dateLong = division_date.toDate().time
                val current_constituencies = MainActivity.model.constituencies.filter {
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

                val not_in_house = MainActivity.model.constituencies.filter {
                    current_constituencies.indexOf(it)==-1
                }.map { it.asJsonObject.get("mp_id").asString }

                var absent = MainActivity.model.constituencies.filter {
                    not_in_house.indexOf(it.asJsonObject.get("mp_id").asString)==-1
                            && ayes.indexOf(it.asJsonObject.get("mp_id").asString)==-1
                            && noes.indexOf(it.asJsonObject.get("mp_id").asString)==-1
                }.map { it.asJsonObject.get("mp_id") }

                MainActivity.model.division = vote
                MainActivity.model.allvotes = Gson().fromJson<JsonArray>(Gson().toJson(
                        listOf(ayes, noes, absent, not_in_house)
                ), JsonArray::class.java)

                Observable.just(it)
            }
    }

    interface MPsList { @GET("/mps") fun mps(): Observable<JsonArray> }
    val mpsObservable: Observable<JsonArray> = repo.create(MPsList::class.java)
        .mps()
        .subscribeOn(Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread())
    //fun mps(f: (JsonArray) -> Unit) = mpsObservable.subscribe(f)

    interface MPDetails { @GET("/mp_details") fun details(@Query("id") id:String): Observable<JsonObject> }
    fun mpDetails(f: (JsonObject) -> Unit) = repo.create(MPDetails::class.java)
        .details("523")
        .subscribeOn(Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(f)

    interface ConstituencySearch { @GET("/name") fun details(@Query("name") name:String): Observable<JsonArray> }
    fun constituencySearch(f: (JsonArray) -> Unit) =
        repo.create(ConstituencySearch::class.java)
                .details("Oxford")
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(f)

    interface ConstituencyDetail { @GET("/id") fun details(@Query("id") name:String): Observable<JsonObject> }
    fun constitucyDetails(f: (JsonObject) -> Unit) =
        repo.create(ConstituencyDetail::class.java)
                .details("E14000807")
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(f)

    interface PostcodeToLatLon{ @GET("/pc_lat_lon") fun details(@Query("postcode") name:String): Observable<JsonObject> }
    fun postcodeToLatLon(f: (JsonObject) -> Unit) =
        repo.create(PostcodeToLatLon::class.java)
                .details("M401DB")
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(f)


}

