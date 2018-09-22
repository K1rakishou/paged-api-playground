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

  fun handleGetPageOfPhotos(routingContext: RoutingContext) {
    handlerAsync(routingContext) { context ->
      val lastPhotoIdParam: String? = context.request().getParam("last_photo_id")
      if (lastPhotoIdParam == null) {
        sendBadRequest(context, "No page number in the request")
        return@handlerAsync
      }

      val lastPhotoId = try {
        lastPhotoIdParam.toLong()
      } catch (error: NumberFormatException) {
        -1L
      }

      if (lastPhotoId == -1L) {
        sendBadRequest(context, "Could not parse parameter page")
        return@handlerAsync
      }

      val photosPerPageParam: String? = context.request().getParam("photos_per_page")
      val photosPerPage = try {
        photosPerPageParam?.toInt() ?: defaultPhotosPerPage
      } catch (error: NumberFormatException) {
        defaultPhotosPerPage
      }

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
}