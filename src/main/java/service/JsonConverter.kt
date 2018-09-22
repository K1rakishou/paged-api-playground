package service

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import core.AsyncResult
import kotlinx.coroutines.experimental.CoroutineDispatcher
import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import kotlin.coroutines.experimental.CoroutineContext

class JsonConverter(
  val gson: Gson,
  private val coroutineDispatcher: CoroutineDispatcher
) : CoroutineScope {

  override val coroutineContext: CoroutineContext
    get() = coroutineDispatcher

  fun <T> toJson(data: T): Deferred<AsyncResult<String, Throwable>> {
    return async {
      return@async try {
        AsyncResult.Ok(gson.toJson(data))
      } catch (error: Throwable) {
        AsyncResult.Error(error)
      }
    }
  }

}