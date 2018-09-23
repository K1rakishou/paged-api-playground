package handler

import data.repository.Repository
import io.vertx.ext.web.RoutingContext
import service.JsonConverter

class PhotosHandler(
  private val repository: Repository,
  private val jsonConverter: JsonConverter
) : BaseHandler() {

  private val defaultPhotosPerPage = 20
  private val maxPhotosPerPage = 100

  fun handleGetPageOfPhotos(routingContext: RoutingContext) {
    handleAsync(routingContext) { context ->
      val lastPhotoId = tryParseLongRequestParamOrNull(context, LAST_PHOTO_ID_PARAM)
      if (lastPhotoId == null) {
        sendBadRequest(context, "Bad parameter $LAST_PHOTO_ID_PARAM: $lastPhotoId")
        return@handleAsync
      }

      val photosPerPage = tryParseIntRequestParamOrNull(context, PHOTOS_PER_PAGE_PARAM)
        ?.coerceIn(0, maxPhotosPerPage)
        ?: defaultPhotosPerPage

      val photos = repository.getPageOfPhotos(lastPhotoId, photosPerPage).await()
      val jsonResult = jsonConverter.toJson(photos).await()

      jsonResult
        .doWhenOk { json -> sendJsonResponse(context, json) }
        .doWhenError { error -> sendErrorResponse(context, error) }
    }
  }

  fun handleGetAllPhotos(routingContext: RoutingContext) {
    handleAsync(routingContext) { context ->
      if (!context.queryParams().isEmpty) {
        context.next()
        return@handleAsync
      }

      val photos = repository.getAllPhotos().await()
      val jsonResult = jsonConverter.toJson(photos).await()

      jsonResult
        .doWhenOk { json -> sendJsonResponse(context, json) }
        .doWhenError { error -> sendErrorResponse(context, error) }
    }
  }

  fun handleGetAllPhotosByUserId(routingContext: RoutingContext) {
    handleAsync(routingContext) { context ->
      if (!containsQueryParams(context, USER_ID_PARAM)) {
        context.next()
        return@handleAsync
      }

      val userId = tryParseLongRequestParamOrNull(routingContext, USER_ID_PARAM)
      if (userId == null) {
        sendBadRequest(context, "Bad parameter ${USER_ID_PARAM}: $userId")
        return@handleAsync
      }

      val photos = repository.getAllPhotosByUserId(userId).await()
      val jsonResult = jsonConverter.toJson(photos).await()

      jsonResult
        .doWhenOk { json -> sendJsonResponse(context, json) }
        .doWhenError { error -> sendErrorResponse(context, error) }
    }
  }

  fun handleGetPageOfPhotosByUserId(routingContext: RoutingContext) {
    handleAsync(routingContext) { context ->
      if (!containsQueryParams(context, USER_ID_PARAM, LAST_PHOTO_ID_PARAM, PHOTOS_PER_PAGE_PARAM)) {
        context.next()
        return@handleAsync
      }

      val userId = tryParseLongQueryParamOrNull(context, USER_ID_PARAM)
      if (userId == null) {
        sendBadRequest(context, "Bad parameter $USER_ID_PARAM: $userId")
        return@handleAsync
      }

      val lastPhotoId = tryParseLongQueryParamOrNull(context, LAST_PHOTO_ID_PARAM)
      if (lastPhotoId == null) {
        sendBadRequest(context, "Bad parameter $LAST_PHOTO_ID_PARAM: $lastPhotoId")
        return@handleAsync
      }

      val photosPerPage = tryParseIntQueryParamOrNull(context, PHOTOS_PER_PAGE_PARAM)
        ?.coerceIn(0, maxPhotosPerPage)
        ?: defaultPhotosPerPage

      val photos = repository.getPageOfPhotosByUserId(lastPhotoId, userId, photosPerPage).await()
      val jsonResult = jsonConverter.toJson(photos).await()

      jsonResult
        .doWhenOk { json -> sendJsonResponse(context, json) }
        .doWhenError { error -> sendErrorResponse(context, error) }
    }
  }

  companion object {
    const val USER_ID_PARAM = "user_id"
    const val LAST_PHOTO_ID_PARAM = "last_photo_id"
    const val PHOTOS_PER_PAGE_PARAM = "photos_per_page"
  }
}