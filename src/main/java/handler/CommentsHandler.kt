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
      val lastCommentIdParam: String? = context.request().getParam(LAST_COMMENT_ID_PARAM)
      if (lastCommentIdParam == null) {
        sendBadRequest(context, "No page number in the request")
        return@handlerAsync
      }

      val lastCommentId = try {
        lastCommentIdParam.toLong()
      } catch (error: NumberFormatException) {
        -1L
      }

      if (lastCommentId < 0L) {
        sendBadRequest(context, "Could not parse parameter page")
        return@handlerAsync
      }

      val commentsPerPage = try {
        context.request().getParam(COMMENTS_PER_PAGE_PARAM)
          ?.toInt()
          ?.coerceIn(0, maxCommentsPerPage) ?: defaultCommentsPerPage
      } catch (error: NumberFormatException) {
        defaultCommentsPerPage
      }

      val comments = repository.getPageOfComments(lastCommentId, commentsPerPage).await()
      val jsonResult = jsonConverter.toJson(comments).await()

      jsonResult
        .doWhenOk { json -> sendJsonResponse(context, json) }
        .doWhenError { error -> sendErrorResponse(context, error) }
    }
  }

  fun handleGetAllComments(routingContext: RoutingContext) {
    handlerAsync(routingContext) { context ->
      val comments = repository.getAllComments().await()
      val jsonResult = jsonConverter.toJson(comments).await()

      jsonResult
        .doWhenOk { json -> sendJsonResponse(context, json) }
        .doWhenError { error -> sendErrorResponse(context, error) }
    }
  }

  companion object {
    const val LAST_COMMENT_ID_PARAM = "last_comment_id"
    const val COMMENTS_PER_PAGE_PARAM = "comments_per_page"
  }
}