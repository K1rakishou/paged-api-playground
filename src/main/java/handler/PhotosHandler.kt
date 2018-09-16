package handler

import data.repository.Repository
import io.vertx.core.http.HttpMethod
import io.vertx.ext.web.RoutingContext
import service.JsonConverter
import java.lang.NumberFormatException

class PhotosHandler(
  private val repository: Repository,
  private val jsonConverter: JsonConverter
) : BaseHandler(HttpMethod.GET) {

  private val defaultItemsPerPage = 20

  suspend fun handleGetPageOfPhotos(routingContext: RoutingContext) {
    handleExceptions(routingContext) { context ->
      val lastPhotoIdParam: String? = context.request().getParam("last_photo_id")
      if (lastPhotoIdParam == null) {
        sendBadRequest(context, "No page number in the request")
        return@handleExceptions
      }

      val lastPhotoId = try {
        lastPhotoIdParam.toLong()
      } catch (error: NumberFormatException) {
        -1L
      }

      if (lastPhotoId == -1L) {
        sendBadRequest(context, "Could not parse parameter page")
        return@handleExceptions
      }

      val photosPerPageParam: String? = context.request().getParam("photos_per_page")
      val photosPerPage = try {
        photosPerPageParam?.toInt() ?: defaultItemsPerPage
      } catch (error: NumberFormatException) {
        defaultItemsPerPage
      }

      val photos = repository.getPageOfPhotos(lastPhotoId, photosPerPage).await()
      val json = jsonConverter.toJson(photos)

      sendJson(context, json)
    }
  }

  suspend fun handleGetAllPhotos(routingContext: RoutingContext) {
    handleExceptions(routingContext) { context ->
      val photos = repository.getAllPhotos().await()
      val json = jsonConverter.toJson(photos)

      sendJson(context, json)
    }
  }
}