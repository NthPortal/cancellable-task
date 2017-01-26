package com.nthportal.concurrent

import scala.concurrent.CancellationException

/**
  * Thrown when a task fails because something else was cancelled,
  * but the task itself was not cancelled.
  *
  * @param cause the exception thrown by the other cancellation
  */
class OtherCancellationException private(message: String, cause: Throwable) extends Exception(message, cause) {
  /**
    * Creates an `OtherCancellationException` caused by the cancellation of
    * something else.
    *
    * @param cause the exception thrown by the other cancellation
    * @return a new OtherCancellationException
    */
  def this(cause: CancellationException) = this("exceptional cancellation", cause)

  /**
    * Creates an `OtherCancellationException` caused by a different
    * `OtherCancellationException`.
    *
    * @param cause the other OtherCancellationException
    * @return a new OtherCancellationException
    */
  def this(cause: OtherCancellationException) = this("exceptional other cancellation", cause)
}
