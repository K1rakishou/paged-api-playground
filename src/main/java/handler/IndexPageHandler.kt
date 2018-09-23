package handler

import io.vertx.ext.web.RoutingContext

class IndexPageHandler : BaseHandler() {

  private val indexHtmlPath = Thread.currentThread().contextClassLoader.getResource("index.html").file

  fun showIndexPage(routingContext: RoutingContext) {
    handleAsync(routingContext) { context ->
      sendHtmlPage(context, indexHtmlPath)
    }
  }
}