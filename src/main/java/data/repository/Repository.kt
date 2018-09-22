package data.repository

import data.model.Photo
import kotlinx.coroutines.experimental.Deferred
import service.HikariService

class Repository(
  hikariService: HikariService
) : BaseRepository(hikariService) {

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

}