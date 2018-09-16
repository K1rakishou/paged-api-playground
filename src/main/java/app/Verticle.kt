package app

import data.repository.Repository
import handler.IndexPageHandler
import handler.MainPageHandler
import handler.PhotosHandler
import io.vertx.core.http.HttpMethod
import io.vertx.ext.web.Router
import io.vertx.kotlin.coroutines.CoroutineVerticle
import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.Dispatchers
import service.DataGenerator
import service.HikariService
import service.JsonConverter
import kotlin.coroutines.experimental.CoroutineContext

class Verticle(
  private val hikariService: HikariService,
  private val repository: Repository,
  private val dataGenerator: DataGenerator,
  private val jsonConverter: JsonConverter,
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
      get("/").handler { context -> mainPageHandler.handleRequest(HttpMethod.GET, context) }
      get("/index.html").handler { context -> indexPageHandler.handleRequest(HttpMethod.GET, context) }
      get("/photos/:last_photo_id").handler { context -> photosHandler.handleRequest(HttpMethod.GET, context) }
      get("/photos").handler { context -> photosHandler.handleRequest(HttpMethod.GET, context) }
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