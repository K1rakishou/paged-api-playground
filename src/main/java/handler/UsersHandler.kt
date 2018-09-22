package handler

import data.repository.Repository
import io.vertx.ext.web.RoutingContext
import service.JsonConverter

class UsersHandler(
  private val repository: Repository,
  private val jsonConverter: JsonConverter
) : BaseHandler() {

  private val defaultUsersPerPage = 20

  fun handleGetPageOfUsers(routingContext: RoutingContext) {

  }

  fun handleGetAllUsers(routingContext: RoutingContext) {
    handlerAsync(routingContext) {
      val users = repository.getAllUsers().await()
      val jsonResult = jsonConverter.toJson(users).await()

      jsonResult
        .doWhenOk { json -> sendJsonResponse(routingContext, json) }
        .doWhenError { error ->  sendErrorResponse(routingContext, error) }
    }
  }
}