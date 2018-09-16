package handler

import io.vertx.core.http.HttpMethod
import io.vertx.ext.web.RoutingContext

class IndexPageHandler : BaseHandler(HttpMethod.GET) {

  private val indexHtmlPath = Thread.currentThread().contextClassLoader.getResource("index.html").file

  suspend fun showIndexPage(routingContext: RoutingContext) {
    handleExceptions(routingContext) { context ->
      context.response()
        .putHeader("content-type", "text/html;charset=utf-8")
        .setStatusCode(200)
        .sendFile(indexHtmlPath)
        .end()
    }
  }
}