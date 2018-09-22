package handler

import data.repository.Repository
import io.vertx.ext.web.RoutingContext
import service.JsonConverter
import java.lang.NumberFormatException

class PhotosHandler(
  private val repository: Repository,
  private val jsonConverter: JsonConverter
) : BaseHandler() {

  private val defaultPhotosPerPage = 20
  private val maxPhotosPerPage = 100

  fun handleGetPageOfPhotos(routingContext: RoutingContext) {
    handlerAsync(routingContext) { context ->
      val lastPhotoId = tryParseLongRequestParamOrNull(context, LAST_PHOTO_ID_PARAM)
      if (lastPhotoId == null) {
        sendBadRequest(context, "Bad parameter $LAST_PHOTO_ID_PARAM: $lastPhotoId")
        return@handlerAsync
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
    handlerAsync(routingContext) { context ->
      val photos = repository.getAllPhotos().await()
      val jsonResult = jsonConverter.toJson(photos).await()

      jsonResult
        .doWhenOk { json -> sendJsonResponse(context, json) }
        .doWhenError { error -> sendErrorResponse(context, error) }
    }
  }

  companion object {
    const val LAST_PHOTO_ID_PARAM = "last_photo_id"
    const val PHOTOS_PER_PAGE_PARAM = "photos_per_page"
  }
}