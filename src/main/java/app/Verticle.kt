package app

import handler.IndexPageHandler
import handler.MainPageHandler
import handler.PhotosHandler
import handler.UsersHandler
import io.vertx.ext.web.Router
import io.vertx.kotlin.coroutines.CoroutineVerticle
import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.launch
import kotlin.coroutines.experimental.CoroutineContext

class Verticle(
  private val mainPageHandler: MainPageHandler,
  private val indexPageHandler: IndexPageHandler,
  private val photosHandler: PhotosHandler,
  private val usersHandler: UsersHandler
) : CoroutineVerticle(), CoroutineScope {

  override val coroutineContext: CoroutineContext
    get() = Dispatchers.Default

  override suspend fun start() {
    super.start()

    val server = vertx.createHttpServer()
    val router = Router.router(vertx).apply {
      get("/").handler { context ->
        println("get /")
        mainPageHandler.rerouteToIndexPage(context)
      }
      get("/index.html").handler { context ->
        println("get /index.html")
        indexPageHandler.showIndexPage(context)
      }

      //photo handlers
      get("/photos").handler { context ->
        println("get /photos")
        photosHandler.handleGetAllPhotos(context)
      }
      get("/photos/:last_photo_id").handler { context ->
        println("get /photos/:last_photo_id")
        photosHandler.handleGetPageOfPhotos(context)
      }
      get("/photos/:last_photo_id/:photos_per_page").handler { context ->
        println("get /photos/:last_photo_id/:photos_per_page")
        photosHandler.handleGetPageOfPhotos(context)
      }

      //user handlers
      get("/users").handler { context ->
        println("get /users")
        usersHandler.handleGetAllUsers(context)
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