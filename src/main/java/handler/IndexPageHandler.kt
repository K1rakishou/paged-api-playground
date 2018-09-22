package handler

import io.vertx.ext.web.RoutingContext

class IndexPageHandler : BaseHandler() {

  private val indexHtmlPath = Thread.currentThread().contextClassLoader.getResource("index.html").file

  fun showIndexPage(routingContext: RoutingContext) {
    handlerAsync(routingContext) { context ->
      context.response()
        .putHeader("content-type", "text/html;charset=utf-8")
        .setStatusCode(200)
        .sendFile(indexHtmlPath)
        .end()
    }
  }
}