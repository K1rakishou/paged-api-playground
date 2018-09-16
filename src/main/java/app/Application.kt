package app

import com.google.gson.GsonBuilder
import data.repository.Repository
import handler.IndexPageHandler
import handler.MainPageHandler
import handler.PhotosHandler
import io.vertx.core.Vertx
import service.DataGenerator
import service.HikariService
import service.JsonConverter
import java.util.*

fun main(args: Array<String>) {
  if (args.size != 1) {
    println("Should contain photos directory!")
    return
  }

  val gson = GsonBuilder()
    .setPrettyPrinting() //TODO: deletme
    .create()

  val hikariService = HikariService()
  val repository = Repository(hikariService)

  val dataGenerator = DataGenerator(args[0], hikariService, Random())
  val jsonConverter = JsonConverter(gson)

  val mainPageHandler = MainPageHandler()
  val indexPageHandler = IndexPageHandler()
  val photosHandler = PhotosHandler(repository, jsonConverter)

  dataGenerator.generate()

  val verticle = Verticle(
    mainPageHandler,
    indexPageHandler,
    photosHandler
  )

  Vertx.vertx().deployVerticle(verticle) { result ->
    if (result.succeeded()) {
      println("Server started")
    } else {
      println("Could not start server")
      result.cause().printStackTrace()
    }
  }
}