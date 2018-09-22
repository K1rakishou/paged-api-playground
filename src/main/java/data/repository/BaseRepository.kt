package data.repository

import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.sync.Mutex
import kotlinx.coroutines.experimental.sync.withLock
import service.HikariService
import java.sql.Connection
import kotlin.coroutines.experimental.CoroutineContext

abstract class BaseRepository(
  protected val hikariService: HikariService
) : CoroutineScope {

  override val coroutineContext: CoroutineContext
    get() = Dispatchers.IO

  protected suspend fun <T> repoAsync(block: (Connection) -> T): Deferred<T> {
    return async(coroutineContext) {
      return@async hikariService.getConnection().use { connection ->
        return@use block(connection)
      }
    }
  }
}