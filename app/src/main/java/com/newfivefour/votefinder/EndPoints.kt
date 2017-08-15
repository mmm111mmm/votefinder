package com.newfivefour.votefinder

import android.support.annotation.NonNull
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
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
    //fun divisionsDetails(uin:String, f: (JsonObject) -> Unit) = divisionsDetailsObservable().subscribe(f)

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

