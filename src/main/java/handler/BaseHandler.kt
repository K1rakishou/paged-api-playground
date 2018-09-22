package handler

import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.ext.web.RoutingContext
import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.launch
import kotlin.coroutines.experimental.CoroutineContext

abstract class BaseHandler : CoroutineScope {

  override val coroutineContext: CoroutineContext
    get() = Dispatchers.Default

  protected fun sendErrorResponse(routingContext: RoutingContext, error: Throwable) {
    val errorMessage = error.message ?: "No error message"

    routingContext
      .response()
      .setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code())
      .setStatusMessage("Unknown error has happened: $errorMessage")
      .end()
  }

  protected fun sendJsonResponse(routingContext: RoutingContext, json: String) {
    routingContext
      .response()
      .setChunked(true)
      .putHeader("content-type", "application/json;charset=utf-8")
      .setStatusCode(HttpResponseStatus.OK.code())
      .write(json)
      .end()
  }

  protected fun sendBadRequest(routingContext: RoutingContext, message: String) {
    routingContext
      .response()
      .setStatusCode(HttpResponseStatus.BAD_REQUEST.code())
      .setStatusMessage(message)
      .end()
  }

  protected fun handlerAsync(routingContext: RoutingContext, block: suspend (context: RoutingContext) -> Unit) {
    launch {
      try {
        block(routingContext)
      } catch (error: Throwable) {
        error.printStackTrace()
        sendErrorResponse(routingContext, error)
      }
    }
  }
}