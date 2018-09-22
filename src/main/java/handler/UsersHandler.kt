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
    handlerAsync(routingContext) { context ->

    }
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

  companion object {
    const val LAST_USER_ID_PARAM = "last_user_id"
    const val USERS_PER_PAGE_PARAM = "users_per_page"
  }
}