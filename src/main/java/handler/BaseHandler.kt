package handler

import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.ext.web.RoutingContext
import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.launch
import java.lang.RuntimeException
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

  protected fun sendNotFound(routingContext: RoutingContext) {
    routingContext
      .response()
      .setStatusCode(HttpResponseStatus.NOT_FOUND.code())
      .end()
  }

  protected fun handleAsync(routingContext: RoutingContext, block: suspend (context: RoutingContext) -> Unit) {
    launch {
      try {
        block(routingContext)
      } catch (error: Throwable) {
        error.printStackTrace()
        sendErrorResponse(routingContext, error)
      }
    }
  }

  protected fun sendHtmlPage(routingContext: RoutingContext, fileName: String) {
    routingContext.response()
      .setChunked(true)
      .putHeader("content-type", "text/html;charset=utf-8")
      .setStatusCode(200)
      .sendFile(fileName)
  }

  protected fun sendPhoto(routingContext: RoutingContext, fileName: String) {
    routingContext.response()
      .setChunked(true)
      .putHeader("content-type", "image/png")
      .setStatusCode(200)
      .sendFile(fileName)
  }

  protected fun containsQueryParams(routingContext: RoutingContext, vararg queryParamNames: String): Boolean {
    val queryParams = routingContext.queryParams()
    if (queryParams.size() != queryParamNames.size) {
      return false
    }

    for (queryParam in queryParamNames) {
      if (!queryParams.contains(queryParam)) {
        return false
      }
    }

    return true
  }

  protected fun tryParseLongQueryParamOrNull(routingContext: RoutingContext, paramName: String): Long? {
    if (routingContext.queryParam(paramName)?.size ?: 0 > 1) {
      throw RuntimeException("More than one parameter in the query with the same name $paramName")
    }

    val paramString: String? = routingContext.queryParam(paramName)?.firstOrNull()
    if (paramString == null) {
      return null
    }

    val paramLong = try {
      paramString.toLong()
    } catch (error: NumberFormatException) {
      null
    }

    return paramLong
  }

  protected fun tryParseLongRequestParamOrNull(routingContext: RoutingContext, paramName: String): Long? {
    val paramString: String? = routingContext.request().getParam(paramName)
    if (paramString == null) {
      return null
    }

    val paramLong = try {
      paramString.toLong()
    } catch (error: NumberFormatException) {
      null
    }

    return paramLong
  }

  protected fun tryParseIntQueryParamOrNull(routingContext: RoutingContext, paramName: String): Int? {
    if (routingContext.queryParam(paramName)?.size ?: 0 > 1) {
      throw RuntimeException("More than one parameter in the query with the same name $paramName")
    }

    val paramString: String? = routingContext.queryParam(paramName)?.firstOrNull()
    if (paramString == null) {
      return null
    }

    val paramInt = try {
      paramString.toInt()
    } catch (error: NumberFormatException) {
      null
    }

    return paramInt
  }

  protected fun tryParseIntRequestParamOrNull(routingContext: RoutingContext, paramName: String): Int? {
    val paramString: String? = routingContext.request().getParam(paramName)
    if (paramString == null) {
      return null
    }

    val paramInt = try {
      paramString.toInt()
    } catch (error: NumberFormatException) {
      null
    }

    return paramInt
  }
}