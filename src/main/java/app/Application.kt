package app

import com.google.gson.GsonBuilder
import data.repository.Repository
import handler.*
import io.vertx.core.Vertx
import kotlinx.coroutines.experimental.asCoroutineDispatcher
import service.DataGenerator
import service.HikariService
import service.JsonConverter
import java.util.*
import java.util.concurrent.Executors
import kotlin.system.measureTimeMillis

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
  val commentsHandler = CommentsHandler(repository, jsonConverter)

  val time = measureTimeMillis {
    dataGenerator.generate()
  }

  println("Data generation done. Time spent: ${time / 1000L} seconds")

  val verticle = Verticle(
    mainPageHandler,
    indexPageHandler,
    photosHandler,
    usersHandler,
    commentsHandler
  )

  Vertx.vertx().deployVerticle(verticle) { result ->
    if (result.succeeded()) {
      println("Server has started!\n\n")
      println("List of Commands: ")

      println("- Photo commands")
      println("Get All Photos - http://127.0.0.1:8080/api/v1/photos")
      println("Get page of photos starting from a photo with id = n + 1 - http://127.0.0.1:8080/api/v1/photos/10")
      println("Get page of photos (n count when n max is PhotosHandler.defaultPhotosPerPage) starting from a photo with id = m + 1 - http://127.0.0.1:8080/api/v1/photos/10/5")

      println("- User commands")
      println("Get All Users - http://127.0.0.1:8080/api/v1/users")
      println("Get page of users starting from a user with id = n + 1 - http://127.0.0.1:8080/api/v1/users/1")
      println("Get page of users (n count when n max is UsersHandler.defaultUsersPerPage) starting from a user with id = m + 1 - http://127.0.0.1:8080/api/v1/users/1/5")

      println("- Comment commands")
      println("Get All Comments - http://127.0.0.1:8080/api/v1/comments")
      println("Get page of comments starting from a comment with id = n + 1 - http://127.0.0.1:8080/api/v1/comments/100")
      println("Get page of comments (n count when n max is CommentsHandler.defaultCommentsPerPage) starting from a comment with id = m + 1 - http://127.0.0.1:8080/api/v1/comments/100/50")

      println("\n\n")
    } else {
      println("Could not start server")
      result.cause().printStackTrace()
    }
  }
}