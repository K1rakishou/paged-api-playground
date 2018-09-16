package handler

import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.core.http.HttpMethod
import io.vertx.ext.web.RoutingContext
import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.launch
import java.lang.IllegalArgumentException
import java.lang.RuntimeException
import kotlin.coroutines.experimental.CoroutineContext

abstract class BaseHandler(
  private vararg val httpMethods: HttpMethod
) : CoroutineScope {

  override val coroutineContext: CoroutineContext
    get() = Dispatchers.Default

  init {
    if (httpMethods.isEmpty()) {
      throw RuntimeException("Handler must handle at least one http method!")
    }
  }

  private fun sendErrorResponse(error: Throwable, context: RoutingContext) {
    val errorMessage = error.message ?: "No error message"

    context
      .response()
      .setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code())
      .setStatusMessage("Unknown error has happened: $errorMessage")
      .end()
  }

  private fun sendHttpMethodNotSupportedResponse(context: RoutingContext) {
    context
      .response()
      .setStatusCode(HttpResponseStatus.BAD_REQUEST.code())
      .setStatusMessage("Http method not supported")
      .end()
  }

  protected fun sendJson(context: RoutingContext, json: String) {
    context
      .response()
      .setChunked(true)
      .putHeader("content-type", "application/json;charset=utf-8")
      .setStatusCode(HttpResponseStatus.OK.code())
      .write(json)
      .end()
  }

  protected fun sendBadRequest(context: RoutingContext, message: String) {
    context
      .response()
      .setStatusCode(HttpResponseStatus.BAD_REQUEST.code())
      .setStatusMessage(message)
      .end()
  }

  protected fun send200Ok(context: RoutingContext) {
    context
      .response()
      .setStatusCode(HttpResponseStatus.OK.code())
      .end()
  }

  suspend fun dummyHandler(routingContext: RoutingContext) {
    handleExceptions(routingContext) { context ->
      send200Ok(context)
    }
  }

  protected suspend fun handleExceptions(context: RoutingContext, block: suspend (context: RoutingContext) -> Unit) {
    try {
      block(context)
    } catch (error: Throwable) {
      error.printStackTrace()

      sendErrorResponse(error, context)
    }
  }
}