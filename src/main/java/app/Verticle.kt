package app

import handler.*
import handler.CommentsHandler.Companion.COMMENTS_PER_PAGE_PARAM
import handler.CommentsHandler.Companion.LAST_COMMENT_ID_PARAM
import handler.PhotosHandler.Companion.LAST_PHOTO_ID_PARAM
import handler.PhotosHandler.Companion.PHOTOS_PER_PAGE_PARAM
import handler.PhotosHandler.Companion.PHOTO_NAME_PARAM
import handler.UsersHandler.Companion.LAST_USER_ID_PARAM
import handler.UsersHandler.Companion.USERS_PER_PAGE_PARAM
import io.vertx.ext.web.Router
import io.vertx.kotlin.coroutines.CoroutineVerticle
import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.Dispatchers
import kotlin.coroutines.experimental.CoroutineContext

class Verticle(
  private val mainPageHandler: MainPageHandler,
  private val indexPageHandler: IndexPageHandler,
  private val photosHandler: PhotosHandler,
  private val usersHandler: UsersHandler,
  private val commentsHandler: CommentsHandler
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
          photosRouter.get("/file/:$PHOTO_NAME_PARAM").handler { context ->
            println("get /photos/file/:$PHOTO_NAME_PARAM")
            photosHandler.handleGetPhotoFile(context)
          }
          photosRouter.get("/").handler { context ->
            println("get /photos")
            photosHandler.handleGetAllPhotos(context)
          }
          photosRouter.get("/").handler { context ->
            println("get /photos?user_id")
            photosHandler.handleGetAllPhotosByUserId(context)
          }
          photosRouter.get("/").handler { context ->
            println("get /photos?user_id&last_photo_id&photos_per_page")
            photosHandler.handleGetPageOfPhotosByUserId(context)
          }
          photosRouter.get("/:$LAST_PHOTO_ID_PARAM").handler { context ->
            println("get /photos/:$LAST_PHOTO_ID_PARAM")
            photosHandler.handleGetPageOfPhotos(context)
          }
          photosRouter.get("/:$LAST_PHOTO_ID_PARAM/:$PHOTOS_PER_PAGE_PARAM").handler { context ->
            println("get /photos/:$LAST_PHOTO_ID_PARAM/:$PHOTOS_PER_PAGE_PARAM")
            photosHandler.handleGetPageOfPhotos(context)
          }
        }) // /api/v1/photos

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
        }) // /api/v1/users

        // /api/v1/comments
        routerV1.mountSubRouter("/comments", Router.router(vertx).also { commentsRouter ->
          commentsRouter.get("/").handler { context ->
            println("get /comments")
            commentsHandler.handleGetAllComments(context)
          }
          commentsRouter.get("/").handler { context ->
            println("get /comments?user_id")
            commentsHandler.handleGetAllCommentsByUserId(context)
          }
          commentsRouter.get("/").handler { context ->
            println("get /comments?user_id&last_comment_id&comments_per_page")
            commentsHandler.handleGetPageOfCommentsByUserId(context)
          }
          commentsRouter.get("/:$LAST_COMMENT_ID_PARAM").handler { context ->
            println("get /comments/:$LAST_COMMENT_ID_PARAM")
            commentsHandler.handleGetPageOfComments(context)
          }
          commentsRouter.get("/:$LAST_COMMENT_ID_PARAM/:$COMMENTS_PER_PAGE_PARAM").handler { context ->
            println("get /comments/:$LAST_COMMENT_ID_PARAM/:COMMENTS_PER_PAGE_PARAM")
            commentsHandler.handleGetPageOfComments(context)
          }
        }) // /api/v1/comments

      }) // /api/v1
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