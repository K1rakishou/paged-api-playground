package data.repository

import data.model.Photo
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.sync.Mutex
import kotlinx.coroutines.experimental.sync.withLock
import service.HikariService
import kotlin.coroutines.experimental.CoroutineContext

class Repository(
  private val hikariService: HikariService
) : CoroutineScope {

  override val coroutineContext: CoroutineContext
    get() = Dispatchers.IO

  private val itemsPerPage = 20
  private val mutex = Mutex()

  suspend fun getPageOfPhotos(lastPhotoId: Long): Deferred<List<Photo>> {
    return async {
      return@async mutex.withLock {
        return@withLock hikariService.getConnection().use { connection ->
          return@use connection.prepareStatement("SELECT * FROM photos WHERE photo_id > ? LIMIT ?").use { statement ->
            val photos = mutableListOf<Photo>()
            statement.setLong(1, lastPhotoId)
            statement.setInt(2, itemsPerPage)

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
  }

  suspend fun getAllPhotos(): Deferred<List<Photo>> {
    return async {
      return@async mutex.withLock {
        return@withLock hikariService.getConnection().use { connection ->
          return@use connection.prepareStatement("SELECT * FROM photos").use { statement ->
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
  }
}