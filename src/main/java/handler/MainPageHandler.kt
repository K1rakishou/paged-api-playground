package handler

import io.vertx.core.http.HttpMethod
import io.vertx.ext.web.RoutingContext

class MainPageHandler : BaseHandler(HttpMethod.GET) {

  suspend fun rerouteToIndexPage(routingContext: RoutingContext) {
    handleExceptions(routingContext) { context ->
      context
        .reroute(HttpMethod.GET, "/index.html")
    }
  }
}