package app

import com.google.gson.GsonBuilder
import data.repository.Repository
import handler.*
import io.vertx.core.Vertx
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
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
  val IOCoroutineDispatcher = Dispatchers.IO

  val photosDirectory = args[0]

  val gson = GsonBuilder()
    .create()

  val hikariService = HikariService()
  val repository = Repository(hikariService, IOCoroutineDispatcher)

  val dataGenerator = DataGenerator(photosDirectory, hikariService, Random())
  val jsonConverter = JsonConverter(gson, jsonCoroutineDispatcher)

  val mainPageHandler = MainPageHandler()
  val indexPageHandler = IndexPageHandler()
  val photosHandler = PhotosHandler(repository, jsonConverter, photosDirectory)
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
      println("Get photo file = http://127.0.0.1:8080/api/v1/photos/file/photoname.png")
      println("Get All Photos - http://127.0.0.1:8080/api/v1/photos")
      println("Get page of photos starting from a photo with id = n + 1 - http://127.0.0.1:8080/api/v1/photos/10")
      println("Get page of photos starting from a photo with id = lastId + 1 - http://127.0.0.1:8080/api/v1/photos/10/5")
      println("Get page of photos by certain user starting from a photo with id = lastId + 1 - http://127.0.0.1:8080/api/v1/photos?user_id=2&last_photo_id=0&photos_per_page=15")

      println("- User commands")
      println("Get All Users - http://127.0.0.1:8080/api/v1/users")
      println("Get page of users starting from a user with id = n + 1 - http://127.0.0.1:8080/api/v1/users/1")
      println("Get page of users starting from a user with id = lastId + 1 - http://127.0.0.1:8080/api/v1/users/1/5")

      println("- Comment commands")
      println("Get All Comments - http://127.0.0.1:8080/api/v1/comments")
      println("Get page of comments starting from a comment with id = n + 1 - http://127.0.0.1:8080/api/v1/comments/100")
      println("Get page of comments starting from a comment with id = lastId + 1 - http://127.0.0.1:8080/api/v1/comments/100/50")
      println("Get page of comments by certain user starting from a comment with id = lastId + 1 - http://127.0.0.1:8080/api/v1/comments?user_id=3&last_comment_id=0&comments_per_page=546")

      println("\n\n")
    } else {
      println("Could not start the server")
      result.cause().printStackTrace()
    }
  }
}