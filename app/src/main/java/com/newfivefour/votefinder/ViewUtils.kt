package com.newfivefour.votefinder

import android.view.View
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive

object ViewUtils {

    fun listOf(element: JsonElement?) : List<String> {
        if(element == null) return listOf("")
        else {
            return element.asJsonArray!!.map {
                it.asJsonObject.get("Name").toString().removeSurrounding("\"")
            }
        }
    }

    fun isThere(element: JsonElement?) : Int {
        if(element is JsonArray && element.asJsonArray.size() > 0
                && element.asJsonArray.get(0).toString()!="null") {
            return View.VISIBLE
        } else if(element is JsonPrimitive && element.toString().removeSurrounding("\"").isEmpty()) {
            return View.VISIBLE
        } else {
            return View.GONE
        }
    }

    fun extract(element: JsonElement?) : String {
        if(element is JsonArray && element.asJsonArray.size() > 0) {
            val item = element.asJsonArray?.get(0)
            val value = if(item!!.isJsonPrimitive) item.toString().removeSurrounding("\"")
            else if(item.isJsonArray) item.asJsonArray.map { it.toString().removeSurrounding("\"") }.joinToString(", ")
            else ""
            return if(value == "null") "" else value
        } else if(!element.toString().removeSurrounding("\"").isEmpty()){
            return element.toString().removeSurrounding("\"")
        } else
            return ""
    }

    fun postsExtract(element: JsonElement?) : String {
        if(element == null) return ""
        else {
            return element.asJsonArray!!.map {
                it.asJsonObject.get("Name").toString().removeSurrounding("\"")
            }.joinToString(", ")
        }
    }

}
