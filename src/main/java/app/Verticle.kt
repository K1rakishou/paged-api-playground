package app

import handler.IndexPageHandler
import handler.MainPageHandler
import handler.PhotosHandler
import handler.PhotosHandler.Companion.LAST_PHOTO_ID_PARAM
import handler.PhotosHandler.Companion.PHOTOS_PER_PAGE_PARAM
import handler.UsersHandler
import handler.UsersHandler.Companion.LAST_USER_ID_PARAM
import handler.UsersHandler.Companion.USERS_PER_PAGE_PARAM
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
    val router = Router.router(vertx).also { baseRouter ->
      baseRouter.get("/").handler { context ->
        println("get /")
        mainPageHandler.rerouteToIndexPage(context)
      }
      baseRouter.get("/index.html").handler { context ->
        println("get /index.html")
        indexPageHandler.showIndexPage(context)
      }

      // /api/v1
      baseRouter.mountSubRouter("/api/v1", Router.router(vertx).also { routerV1 ->

        // /api/v1/photos
        routerV1.mountSubRouter("/photos", Router.router(vertx).also { photosRouter ->

          photosRouter.get("/").handler { context ->
            println("get /photos")
            photosHandler.handleGetAllPhotos(context)
          }
          photosRouter.get("/:$LAST_PHOTO_ID_PARAM").handler { context ->
            println("get /photos/:$LAST_PHOTO_ID_PARAM")
            photosHandler.handleGetPageOfPhotos(context)
          }
          photosRouter.get("/:$LAST_PHOTO_ID_PARAM/:$PHOTOS_PER_PAGE_PARAM").handler { context ->
            println("get /photos/:$LAST_PHOTO_ID_PARAM/:$PHOTOS_PER_PAGE_PARAM")
            photosHandler.handleGetPageOfPhotos(context)
          }

        })

        // /api/v1/users
        routerV1.mountSubRouter("/users", Router.router(vertx).also { usersRouter ->

          usersRouter.get("/").handler { context ->
            println("get /users")
            usersHandler.handleGetAllUsers(context)
          }
          usersRouter.get("/:$LAST_USER_ID_PARAM").handler { context ->
            println("get /users/:$LAST_USER_ID_PARAM")
            usersHandler.handleGetPageOfUsers(context)
          }
          usersRouter.get("/:$LAST_USER_ID_PARAM/:$USERS_PER_PAGE_PARAM").handler { context ->
            println("get /users/:$LAST_USER_ID_PARAM/:$USERS_PER_PAGE_PARAM")
            usersHandler.handleGetPageOfUsers(context)
          }

        })
      })
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