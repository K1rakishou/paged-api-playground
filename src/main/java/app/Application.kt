package app

import com.google.gson.GsonBuilder
import data.repository.Repository
import handler.IndexPageHandler
import handler.MainPageHandler
import handler.PhotosHandler
import handler.UsersHandler
import io.vertx.core.Vertx
import kotlinx.coroutines.experimental.asCoroutineDispatcher
import service.DataGenerator
import service.HikariService
import service.JsonConverter
import java.util.*
import java.util.concurrent.Executors

fun main(args: Array<String>) {
  if (args.size != 1) {
    println("Should contain photos directory!")
    return
  }

  val jsonCoroutineDispatcher = Executors.newFixedThreadPool(2).asCoroutineDispatcher()

  val gson = GsonBuilder()
    .setPrettyPrinting() //TODO: deletme
    .create()

  val hikariService = HikariService()
  val repository = Repository(hikariService)

  val dataGenerator = DataGenerator(args[0], hikariService, Random())
  val jsonConverter = JsonConverter(gson, jsonCoroutineDispatcher)

  val mainPageHandler = MainPageHandler()
  val indexPageHandler = IndexPageHandler()
  val photosHandler = PhotosHandler(repository, jsonConverter)
  val usersHandler = UsersHandler(repository, jsonConverter)

  dataGenerator.generate()

  val verticle = Verticle(
    mainPageHandler,
    indexPageHandler,
    photosHandler,
    usersHandler
  )

  Vertx.vertx().deployVerticle(verticle) { result ->
    if (result.succeeded()) {
      println("Server has started!\n\n")
      println("List of Commands: ")
      println("Get All Photos - http://127.0.0.1:8080/photos")
      println("Get page of photos starting from a photo with id = n + 1 - http://127.0.0.1:8080/photos/10")
      println("Get page of photos (n count when n max is PhotosHandler.defaultPhotosPerPage) starting from a photo with id = m + 1 - http://127.0.0.1:8080/photos/10/5")
    } else {
      println("Could not start server")
      result.cause().printStackTrace()
    }
  }
}