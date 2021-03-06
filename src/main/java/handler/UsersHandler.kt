package handler

import data.repository.Repository
import io.vertx.ext.web.RoutingContext
import service.JsonConverter

class UsersHandler(
  private val repository: Repository,
  private val jsonConverter: JsonConverter
) : BaseHandler() {

  private val defaultUsersPerPage = 10
  private val maxUsersPerPage = 100

  fun handleGetPageOfUsers(routingContext: RoutingContext) {
    handleAsync(routingContext) { context ->
      val lastUserId = tryParseLongRequestParamOrNull(context, LAST_USER_ID_PARAM)
      if (lastUserId == null) {
        sendBadRequest(context, "Bad parameter $LAST_USER_ID_PARAM: $lastUserId")
        return@handleAsync
      }

      val usersPerPage = tryParseIntRequestParamOrNull(context, USERS_PER_PAGE_PARAM)
        ?.coerceIn(0, maxUsersPerPage)
        ?: defaultUsersPerPage

      val users = repository.getPageOfUsers(lastUserId, usersPerPage).await()
      val jsonResult = jsonConverter.toJson(users).await()

      jsonResult
        .doWhenOk { json -> sendJsonResponse(context, json) }
        .doWhenError { error -> sendErrorResponse(context, error) }
    }
  }

  fun handleGetAllUsers(routingContext: RoutingContext) {
    handleAsync(routingContext) {
      val users = repository.getAllUsers().await()
      val jsonResult = jsonConverter.toJson(users).await()

      jsonResult
        .doWhenOk { json -> sendJsonResponse(routingContext, json) }
        .doWhenError { error -> sendErrorResponse(routingContext, error) }
    }
  }

  companion object {
    const val LAST_USER_ID_PARAM = "last_user_id"
    const val USERS_PER_PAGE_PARAM = "users_per_page"
  }
}