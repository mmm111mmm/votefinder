package com.newfivefour.votefinder

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

    fun <T> repoWithLoadingAndErrorHandler(o:Observable<T>, loading: Boolean = true):Observable<T> {
       return Observable.just("").flatMap {
           if(loading) MainActivity.model.loading++
           MainActivity.model.error = false
           o.doOnError {
               MainActivity.model.error = true
           }
           o.doAfterTerminate{
               if(loading) MainActivity.model.loading--;
           }
           .subscribeOn(Schedulers.newThread())
                   .observeOn(Schedulers.newThread())
        }
       .subscribeOn(Schedulers.newThread())
               .observeOn(Schedulers.newThread())
    }

    interface DivisionsList { @GET("/divisions") fun divisions(): Observable<JsonArray> }
    fun divisionListObservable(): Observable<JsonArray> =
            repoWithLoadingAndErrorHandler(repo.create(DivisionsList::class.java).divisions())

    interface DivisionDetails { @GET("/vote") fun division(@Query("uin") uin: String): Observable<JsonObject> }
    fun divisionsDetailsObservable(uin:String):Observable<JsonObject> =
            repoWithLoadingAndErrorHandler(repo.create(DivisionDetails::class.java).division(uin))

    interface MPsList { @GET("/mps") fun mps(): Observable<JsonArray> }
    fun mpsObservable(): Observable<JsonArray> =
            repoWithLoadingAndErrorHandler(repo.create(MPsList::class.java).mps())

    interface MPDetails { @GET("/mp_details") fun details(@Query("id") id:String): Observable<JsonObject> }
    fun mpDetails(mpid:String):Observable<JsonObject> =
            repoWithLoadingAndErrorHandler(repo.create(MPDetails::class.java) .details(mpid))


    interface ConstituencySearch { @GET("/name") fun details(@Query("name") name:String): Observable<JsonArray> }
    fun constituencySearch() =
        repoWithLoadingAndErrorHandler(repo.create(ConstituencySearch::class.java).details("Oxford"))

    interface ConstituencyDetail { @GET("/id") fun details(@Query("id") name:String): Observable<JsonObject> }
    fun constitucyDetails() =
        repoWithLoadingAndErrorHandler(repo.create(ConstituencyDetail::class.java).details("E14000807"))

    interface PostcodeToLatLon{ @GET("/pc_lat_lon") fun details(@Query("postcode") name:String): Observable<JsonObject> }
    fun postcodeToLatLon() =
        repoWithLoadingAndErrorHandler(repo.create(PostcodeToLatLon::class.java).details("M401DB"))


}

