package core

sealed class AsyncResult<out V, out E> {
  class Ok<out V>(val value: V) : AsyncResult<V, Nothing>()
  class Error<out E>(val error: E) : AsyncResult<Nothing, E>()

  inline fun doWhenOk(block: (V) -> Unit): AsyncResult<V, E> {
    if (this is Ok) {
      block(this.value)
    }

    return this
  }

  inline fun doWhenError(block: (E) -> Unit): AsyncResult<V, E> {
    if (this is Error) {
      block(this.error)
    }

    return this
  }
}
