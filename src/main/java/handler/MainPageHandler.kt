package handler

import io.vertx.core.http.HttpMethod
import io.vertx.ext.web.RoutingContext

class MainPageHandler : BaseHandler() {

  fun rerouteToIndexPage(routingContext: RoutingContext) {
    handlerAsync(routingContext) { context ->
      context
        .reroute(HttpMethod.GET, "/index.html")
    }
  }
}