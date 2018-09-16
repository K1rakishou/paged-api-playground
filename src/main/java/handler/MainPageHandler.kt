package handler

import io.vertx.core.http.HttpMethod
import io.vertx.ext.web.RoutingContext

class MainPageHandler : BaseHandler(HttpMethod.GET) {

  override suspend fun handleGet(context: RoutingContext) {
    super.handleGet(context)
  }
}