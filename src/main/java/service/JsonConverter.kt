package service

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException

class JsonConverter(
  val gson: Gson
) {

  @Suppress("UNCHECKED_CAST")
  inline fun <reified T> fromJson(json: String): T? {
    try {
      return gson.fromJson(json, T::class.java) as T
    } catch (error: JsonSyntaxException) {
      return null
    }
  }

  fun <T> toJson(data: T): String {
    return gson.toJson(data)
  }

}