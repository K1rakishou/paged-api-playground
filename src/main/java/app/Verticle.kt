package app

import handler.IndexPageHandler
import handler.MainPageHandler
import handler.PhotosHandler
import io.vertx.ext.web.Router
import io.vertx.kotlin.coroutines.CoroutineVerticle
import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.launch
import kotlin.coroutines.experimental.CoroutineContext

class Verticle(
  private val mainPageHandler: MainPageHandler,
  private val indexPageHandler: IndexPageHandler,
  private val photosHandler: PhotosHandler
) : CoroutineVerticle(), CoroutineScope {

  override val coroutineContext: CoroutineContext
    get() = Dispatchers.Default

  override suspend fun start() {
    super.start()

    val server = vertx.createHttpServer()
    val router = Router.router(vertx).apply {
      get("/").handler { context ->
        println("get /")
        launch { mainPageHandler.rerouteToIndexPage(context) }
      }
      get("/index.html").handler { context ->
        println("get /index.html")
        launch { indexPageHandler.showIndexPage(context) }
      }
      get("/photos/:last_photo_id").handler { context ->
        println("get /photos/:last_photo_id")
        launch { photosHandler.handleGetPageOfPhotos(context) }
      }
      get("/photos/:last_photo_id/:photos_per_page").handler { context ->
        println("get /photos/:last_photo_id/:photos_per_page")
        launch { photosHandler.handleGetPageOfPhotos(context) }
      }
      get("/photos").handler { context ->
        println("get /photos")
        launch { photosHandler.handleGetAllPhotos(context) }
      }
    }

    server
      .requestHandler { httpRequest ->
        router.accept(httpRequest)
      }
      .exceptionHandler { exception ->
        exception.printStackTrace()
      }
      .listen(8080)
  }
}