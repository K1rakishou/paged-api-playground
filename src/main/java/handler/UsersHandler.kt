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
    handlerAsync(routingContext) { context ->
      val lastUserIdParam: String? = context.request().getParam(LAST_USER_ID_PARAM)
      if (lastUserIdParam == null) {
        sendBadRequest(context, "No page number in the request")
        return@handlerAsync
      }

      val lastUserId = try {
        lastUserIdParam.toLong()
      } catch (error: NumberFormatException) {
        -1L
      }

      if (lastUserId < 0L) {
        sendBadRequest(context, "Could not parse parameter page")
        return@handlerAsync
      }

      val usersPerPageParam: String? = context.request().getParam(USERS_PER_PAGE_PARAM)
      val usersPerPage = try {
        usersPerPageParam
          ?.toInt()
          ?.coerceIn(0, maxUsersPerPage) ?: defaultUsersPerPage
      } catch (error: NumberFormatException) {
        defaultUsersPerPage
      }

      val users = repository.getPageOfUsers(lastUserId, usersPerPage).await()
      val jsonResult = jsonConverter.toJson(users).await()

      jsonResult
        .doWhenOk { json -> sendJsonResponse(context, json) }
        .doWhenError { error -> sendErrorResponse(context, error) }
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