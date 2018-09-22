package service

import data.model.Comment
import data.model.Photo
import util.StringUtils
import java.awt.image.BufferedImage
import java.io.File
import java.sql.Connection
import java.sql.Statement
import java.util.*
import javax.imageio.ImageIO


class DataGenerator(
  photosDirectory: String,
  val hikariService: HikariService,
  val random: Random
) {

  private val photosInnerDirectory: File

  init {
    if (!File(photosDirectory).isDirectory) {
      throw RuntimeException("Path $photosDirectory is not a directory!")
    }

    photosInnerDirectory = File("$photosDirectory\\photos")

    if (!photosInnerDirectory.isDirectory) {
      throw RuntimeException("Path $photosDirectory is not a directory!")
    }
  }

  fun generate() {
    try {
      if (photosInnerDirectory.exists()) {
        photosInnerDirectory.deleteRecursively()
      }

      photosInnerDirectory.mkdirs()

      createDatabase()

      generate(
        usersCount = 10,
        photosPerUserMax = 20,
        commentsPerUserMax = 20
      )

    } catch (error: Throwable) {
      throw RuntimeException(error)
    }
  }

  private fun createDatabase() {
    hikariService.getConnection().use { connection ->
      connection.createStatement().use { statement ->
        statement.execute(
          "CREATE TABLE IF NOT EXISTS users " +
            "(" +
            "   user_id BIGINT NOT NULL AUTO_INCREMENT, " +
            "   PRIMARY KEY (user_id)" +
            ");")

        statement.execute(
          "CREATE TABLE IF NOT EXISTS photos " +
            "(" +
            "   photo_id BIGINT NOT NULL AUTO_INCREMENT, " +
            "   user_id BIGINT NOT NULL, " +
            "   photo_name VARCHAR(32) NOT NULL, " +
            "   FOREIGN KEY (user_id) REFERENCES users(user_id) on delete cascade, " +
            "   PRIMARY KEY (photo_id) " +
            "); " +
            "CREATE INDEX user_id ON photos(user_id);")

        statement.execute(
          "CREATE TABLE IF NOT EXISTS comments " +
            "(" +
            "   comment_id BIGINT NOT NULL AUTO_INCREMENT, " +
            "   user_id BIGINT NOT NULL, " +
            "   photo_id BIGINT NOT NULL, " +
            "   message VARCHAR(255) NOT NULL, " +
            "   FOREIGN KEY (user_id) REFERENCES users(user_id) on delete cascade, " +
            "   FOREIGN KEY (photo_id) REFERENCES photos(photo_id) on delete cascade, " +
            "   PRIMARY KEY (comment_id)" +
            "); " +
            "CREATE INDEX photo_id ON comments(photo_id);")
      }
    }
  }

  private fun createUser(connection: Connection): Long {
    val sql = "INSERT INTO users () VALUES ()"

    connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS).use { statement ->
      if (statement.executeUpdate() != 1) {
        return -1
      }

      val rs = statement.generatedKeys
      if (rs.first()) {
        return rs.getLong(1)
      }

      return -1
    }
  }

  private fun createPhoto(connection: Connection, photo: Photo): Long {
    val sql = "INSERT INTO photos (user_id, photo_name) VALUES (?, ?)"

    connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS).use { statement ->
      statement.setLong(1, photo.userId)
      statement.setString(2, photo.photoName)

      if (statement.executeUpdate() != 1) {
        return -1
      }

      val rs = statement.generatedKeys
      if (rs.first()) {
        return rs.getLong(1)
      }

      return -1
    }
  }

  private fun createComment(connection: Connection, comment: Comment): Long {
    val sql = "INSERT INTO comments (user_id, photo_id, message) VALUES (?, ?, ?)"

    connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS).use { statement ->
      statement.setLong(1, comment.userId)
      statement.setLong(2, comment.photoId)
      statement.setString(3, comment.message)

      if (statement.executeUpdate() != 1) {
        return -1
      }

      val rs = statement.generatedKeys
      if (rs.first()) {
        return rs.getLong(1)
      }

      return -1
    }
  }

  private fun generate(usersCount: Int, photosPerUserMax: Int, commentsPerUserMax: Int) {
    hikariService.getConnection().use { connection ->
      for (userIndex in 0L until usersCount) {
        println("Creating user $userIndex out of $usersCount")

        val userId = createUser(connection)
        if (userId == -1L) {
          throw RuntimeException("Could not create user!")
        }

        val photosToGenerate = Math.abs(random.nextInt(photosPerUserMax))

        for (photoIndex in 0L until photosToGenerate) {
          val photoNameLen = Math.abs(random.nextInt(15)) + 5
          val photoName = StringUtils.generateRandomString(photoNameLen) + ".png"

          val photoId = createPhoto(connection, Photo(0, userId, photoName))
          if (photoId == -1L) {
            throw RuntimeException("Could not create photo!")
          }

          generatePhotoBitmap(photoName, 400)

          for (commentIndex in 0L until commentsPerUserMax) {
            val commentId = createComment(connection, Comment(0, userId, photoId, StringUtils.randomLoremIpsumLine()))
            if (commentId == -1L) {
              throw RuntimeException("Could not create comment!")
            }
          }
        }
      }
    }
  }

  private fun generatePhotoBitmap(photoName: String, width: Int) {
    val bufferedImage = BufferedImage(width, width, BufferedImage.TYPE_INT_ARGB)

    val a = 255
    val r = (Math.random() * 256).toInt()
    val g = (Math.random() * 256).toInt()
    val b = (Math.random() * 256).toInt()
    val p = a shl 24 or (r shl 16) or (g shl 8) or b

    for (x in 0 until width) {
      for (y in 0 until width) {
        bufferedImage.setRGB(x, y, p)
      }
    }

    val outFile = File("${photosInnerDirectory.absolutePath}\\$photoName.png")
    ImageIO.write(bufferedImage, "png", outFile)
  }
}