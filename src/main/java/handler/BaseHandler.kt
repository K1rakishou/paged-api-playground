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

  fun handleRequest(httpMethod: HttpMethod, context: RoutingContext) {
    launch {
      try {
        when (httpMethod) {
          HttpMethod.GET,
          HttpMethod.POST,
          HttpMethod.PUT,
          HttpMethod.DELETE -> {
            if (!httpMethods.contains(httpMethod)) {
              sendHttpMethodNotSupportedResponse(context)
              return@launch
            }

            when (httpMethod) {
              HttpMethod.GET -> handleGet(context)
              HttpMethod.POST -> handlePost(context)
              HttpMethod.PUT -> handlePut(context)
              HttpMethod.DELETE -> handleDelete(context)
              else -> throw IllegalArgumentException("Unknown httpMethod $httpMethod")
            }
          }

          HttpMethod.OPTIONS,
          HttpMethod.HEAD,
          HttpMethod.TRACE,
          HttpMethod.CONNECT,
          HttpMethod.PATCH,
          HttpMethod.OTHER -> {
            sendHttpMethodNotSupportedResponse(context)
          }
        }
      } catch (error: Throwable) {
        error.printStackTrace()

        sendErrorResponse(error, context)
      }
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

  protected open suspend fun handleGet(context: RoutingContext) {

  }

  protected open suspend fun handlePost(context: RoutingContext) {

  }

  protected open suspend fun handlePut(context: RoutingContext) {

  }

  protected open suspend fun handleDelete(context: RoutingContext) {

  }
}