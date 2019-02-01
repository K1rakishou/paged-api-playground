package data.repository

import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import service.HikariService
import java.sql.Connection
import kotlin.coroutines.CoroutineContext

abstract class BaseRepository(
  protected val hikariService: HikariService,
  private val coroutineDispatcher: CoroutineDispatcher
) : CoroutineScope {

  override val coroutineContext: CoroutineContext
    get() = coroutineDispatcher

  protected suspend fun <T> repoAsync(block: (Connection) -> T): Deferred<T> {
    return async(coroutineContext) {
      return@async hikariService.getConnection().use { connection ->
        return@use block(connection)
      }
    }
  }
}