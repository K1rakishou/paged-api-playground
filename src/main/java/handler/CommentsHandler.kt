package handler

import data.repository.Repository
import io.vertx.ext.web.RoutingContext
import service.JsonConverter

class CommentsHandler(
  private val repository: Repository,
  private val jsonConverter: JsonConverter
) : BaseHandler() {

  private val defaultCommentsPerPage = 20
  private val maxCommentsPerPage = 100

  fun handleGetPageOfComments(routingContext: RoutingContext) {
    handlerAsync(routingContext) { context ->
      val lastCommentId = tryParseLongRequestParamOrNull(routingContext, LAST_COMMENT_ID_PARAM)
      if (lastCommentId == null) {
        sendBadRequest(context, "Bad parameter $LAST_COMMENT_ID_PARAM: $lastCommentId")
        return@handlerAsync
      }

      val commentsPerPage = tryParseIntRequestParamOrNull(routingContext, COMMENTS_PER_PAGE_PARAM)
        ?.coerceIn(0, maxCommentsPerPage)
        ?: defaultCommentsPerPage

      val comments = repository.getPageOfComments(lastCommentId, commentsPerPage).await()
      val jsonResult = jsonConverter.toJson(comments).await()

      jsonResult
        .doWhenOk { json -> sendJsonResponse(context, json) }
        .doWhenError { error -> sendErrorResponse(context, error) }
    }
  }

  fun handleGetAllComments(routingContext: RoutingContext) {
    handlerAsync(routingContext) { context ->
      if (!context.queryParams().isEmpty) {
        context.next()
        return@handlerAsync
      }

      val comments = repository.getAllComments().await()
      val jsonResult = jsonConverter.toJson(comments).await()

      jsonResult
        .doWhenOk { json -> sendJsonResponse(context, json) }
        .doWhenError { error -> sendErrorResponse(context, error) }
    }
  }

  fun handleGetAllCommentsByUserId(routingContext: RoutingContext) {
    handlerAsync(routingContext) { context ->
      if (!containsQueryParams(context, USER_ID_PARAM)) {
        context.next()
        return@handlerAsync
      }

      val userId = tryParseLongRequestParamOrNull(routingContext, USER_ID_PARAM)
      if (userId == null) {
        sendBadRequest(context, "Bad parameter $USER_ID_PARAM: $userId")
        return@handlerAsync
      }

      val comments = repository.getAllCommentsByUserId(userId).await()
      val jsonResult = jsonConverter.toJson(comments).await()

      jsonResult
        .doWhenOk { json -> sendJsonResponse(context, json) }
        .doWhenError { error -> sendErrorResponse(context, error) }
    }
  }

  fun handleGetPageOfCommentsByUserId(routingContext: RoutingContext) {
    handlerAsync(routingContext) { context ->
      if (!containsQueryParams(context, USER_ID_PARAM, LAST_COMMENT_ID_PARAM, COMMENTS_PER_PAGE_PARAM)) {
        context.next()
        return@handlerAsync
      }

      val userId = tryParseLongQueryParamOrNull(context, USER_ID_PARAM)
      if (userId == null) {
        sendBadRequest(context, "Bad parameter $USER_ID_PARAM: $userId")
        return@handlerAsync
      }

      val lastCommentId = tryParseLongQueryParamOrNull(context, LAST_COMMENT_ID_PARAM)
      if (lastCommentId == null) {
        sendBadRequest(context, "Bad parameter $LAST_COMMENT_ID_PARAM: $lastCommentId")
        return@handlerAsync
      }

      val commentsPerPage = tryParseIntQueryParamOrNull(context, COMMENTS_PER_PAGE_PARAM)
        ?.coerceIn(0, maxCommentsPerPage)
        ?: defaultCommentsPerPage

      val comments = repository.getPageOfCommentsByUserId(lastCommentId, userId, commentsPerPage).await()
      val jsonResult = jsonConverter.toJson(comments).await()

      jsonResult
        .doWhenOk { json -> sendJsonResponse(context, json) }
        .doWhenError { error -> sendErrorResponse(context, error) }
    }
  }

  companion object {
    const val LAST_COMMENT_ID_PARAM = "last_comment_id"
    const val COMMENTS_PER_PAGE_PARAM = "comments_per_page"
    const val USER_ID_PARAM = "user_id"
  }
}