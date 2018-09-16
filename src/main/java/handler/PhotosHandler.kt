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

  override suspend fun handleGet(context: RoutingContext) {
    super.handleGet(context)

    val lastPhotoIdParam: String? = context.request().getParam("last_photo_id")
    if (lastPhotoIdParam == null) {
      returnEverything(context)
      return
    }

    val lastPhotoId = try {
      lastPhotoIdParam.toLong()
    } catch (error: NumberFormatException) {
      -1L
    }

    if (lastPhotoId == -1L) {
      sendBadRequest(context, "Could not parse parameter page")
      return
    }

    val photos = repository.getPageOfPhotos(lastPhotoId).await()
    val json = jsonConverter.toJson(photos)

    sendJson(context, json)
  }

  private suspend fun returnEverything(context: RoutingContext) {
    val photos = repository.getAllPhotos().await()
    val json = jsonConverter.toJson(photos)

    sendJson(context, json)
  }
}