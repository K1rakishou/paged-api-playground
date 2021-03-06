package data.repository

import data.model.Comment
import data.model.Photo
import data.model.User
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Deferred
import service.HikariService

class Repository(
  hikariService: HikariService,
  coroutineDispatcher: CoroutineDispatcher
) : BaseRepository(hikariService, coroutineDispatcher) {

  /**
   * Photos
   * */

  suspend fun getAllPhotos(): Deferred<List<Photo>> {
    return repoAsync { connection ->
      connection.prepareStatement("SELECT * FROM photos").use { statement ->
        val photos = mutableListOf<Photo>()

        statement.executeQuery().use { rs ->
          while (rs.next()) {
            photos += Photo(
              rs.getLong("photo_id"),
              rs.getLong("user_id"),
              rs.getString("photo_name")
            )
          }
        }

        return@use photos
      }
    }
  }

  suspend fun getPageOfPhotos(lastPhotoId: Long, photosPerPage: Int): Deferred<List<Photo>> {
    return repoAsync { connection ->
      connection.prepareStatement("SELECT * FROM photos WHERE photo_id > ? LIMIT ?").use { statement ->
        val photos = mutableListOf<Photo>()
        statement.setLong(1, lastPhotoId)
        statement.setInt(2, photosPerPage)

        statement.executeQuery().use { rs ->
          while (rs.next()) {
            photos += Photo(
              rs.getLong("photo_id"),
              rs.getLong("user_id"),
              rs.getString("photo_name")
            )
          }
        }

        return@use photos
      }
    }
  }

  suspend fun getAllPhotosByUserId(userId: Long): Deferred<List<Photo>> {
    return repoAsync { connection ->
      connection.prepareStatement("SELECT * FROM photos WHERE user_id = ?").use { statement ->
        val photos = mutableListOf<Photo>()
        statement.setLong(1, userId)

        statement.executeQuery().use { rs ->
          while (rs.next()) {
            photos += Photo(
              rs.getLong("photo_id"),
              rs.getLong("user_id"),
              rs.getString("photo_name")
            )
          }
        }

        return@use photos
      }
    }
  }

  suspend fun getPageOfPhotosByUserId(lastPhotoId: Long, userId: Long, photosPerPage: Int): Deferred<List<Photo>> {
    return repoAsync { connection ->
      connection.prepareStatement("SELECT * FROM photos WHERE photo_id > ? AND user_id = ? LIMIT ?").use { statement ->
        val photos = mutableListOf<Photo>()
        statement.setLong(1, lastPhotoId)
        statement.setLong(2, userId)
        statement.setInt(3, photosPerPage)

        statement.executeQuery().use { rs ->
          while (rs.next()) {
            photos += Photo(
              rs.getLong("photo_id"),
              rs.getLong("user_id"),
              rs.getString("photo_name")
            )
          }
        }

        return@use photos
      }
    }
  }

  suspend fun getPhotoByPhotoName(photoName: String): Deferred<Photo?> {
    return repoAsync { connection ->
      connection.prepareStatement("SELECT * FROM photos WHERE photo_name = ? LIMIT 1").use { statement ->
        var photo: Photo? = null
        statement.setString(1, photoName)

        statement.executeQuery().use { rs ->
          if (rs.first()) {
            photo = Photo(
              rs.getLong("photo_id"),
              rs.getLong("user_id"),
              rs.getString("photo_name")
            )
          }
        }

        return@use photo
      }
    }
  }

  /**
   * Users
   * */

  suspend fun getAllUsers(): Deferred<List<User>> {
    return repoAsync { connection ->
      connection.prepareStatement("SELECT * FROM users").use { statement ->
        val users = mutableListOf<User>()

        statement.executeQuery().use { rs ->
          while (rs.next()) {
            users += User(
              rs.getLong("user_id")
            )
          }
        }

        return@use users
      }
    }
  }

  suspend fun getPageOfUsers(lastUserId: Long, usersPerPage: Int): Deferred<List<User>> {
    return repoAsync { connection ->
      connection.prepareStatement("SELECT * FROM users WHERE user_id > ? LIMIT ?").use { statement ->
        val users = mutableListOf<User>()
        statement.setLong(1, lastUserId)
        statement.setInt(2, usersPerPage)

        statement.executeQuery().use { rs ->
          while (rs.next()) {
            users += User(
              rs.getLong("user_id")
            )
          }
        }

        return@use users
      }
    }
  }

  /**
   * Comments
   * */

  suspend fun getAllComments(): Deferred<List<Comment>> {
    return repoAsync { connection ->
      connection.prepareStatement("SELECT * FROM comments").use { statement ->
        val comments = mutableListOf<Comment>()

        statement.executeQuery().use { rs ->
          while (rs.next()) {
            comments += Comment(
              rs.getLong("comment_id"),
              rs.getLong("user_id"),
              rs.getLong("photo_id"),
              rs.getString("message")
            )
          }
        }

        return@use comments
      }
    }
  }

  suspend fun getAllCommentsByUserId(userId: Long): Deferred<List<Comment>> {
    return repoAsync { connection ->
      connection.prepareStatement("SELECT * FROM comments WHERE user_id = ?").use { statement ->
        val comments = mutableListOf<Comment>()
        statement.setLong(1, userId)

        statement.executeQuery().use { rs ->
          while (rs.next()) {
            comments += Comment(
              rs.getLong("comment_id"),
              rs.getLong("user_id"),
              rs.getLong("photo_id"),
              rs.getString("message")
            )
          }
        }

        return@use comments
      }
    }
  }

  suspend fun getPageOfCommentsByUserId(lastCommentId: Long, userId: Long, commentsPerPage: Int): Deferred<List<Comment>> {
    return repoAsync { connection ->
      connection.prepareStatement("SELECT * FROM comments WHERE comment_id > ? AND user_id = ? LIMIT ?").use { statement ->
        val comments = mutableListOf<Comment>()
        statement.setLong(1, lastCommentId)
        statement.setLong(2, userId)
        statement.setInt(3, commentsPerPage)

        statement.executeQuery().use { rs ->
          while (rs.next()) {
            comments += Comment(
              rs.getLong("comment_id"),
              rs.getLong("user_id"),
              rs.getLong("photo_id"),
              rs.getString("message")
            )
          }
        }

        return@use comments
      }
    }
  }

  suspend fun getPageOfComments(lastCommentId: Long, commentsPerPage: Int): Deferred<List<Comment>> {
    return repoAsync { connection ->
      connection.prepareStatement("SELECT * FROM comments WHERE comment_id > ? LIMIT ?").use { statement ->
        val comments = mutableListOf<Comment>()
        statement.setLong(1, lastCommentId)
        statement.setInt(2, commentsPerPage)

        statement.executeQuery().use { rs ->
          while (rs.next()) {
            comments += Comment(
              rs.getLong("comment_id"),
              rs.getLong("user_id"),
              rs.getLong("photo_id"),
              rs.getString("message")
            )
          }
        }

        return@use comments
      }
    }
  }
}