package com.nthportal.concurrent

import java.util.concurrent.FutureTask

import scala.concurrent._
import scala.util.Try

/**
  * An asynchronously executed task which can be cancelled
  * before or during its execution.
  *
  * This class is adapted from
  * [[https://stackoverflow.com/a/39986418/5101123 this Stack Overflow answer]].
  *
  * @param body the task to execute
  * @param ec   the context in which to execute the task
  * @tparam T the return type of `body`
  */
final class CancellableTask[T] private(body: => T, ec: ExecutionContext) {
  private val promise = Promise[T]()

  private val task = new FutureTask[T](() => body) {
    override def done() = promise complete {
      Try(get) recover {
        case t: ExecutionException => t.getCause match {
          case e: CancellationException => throw new OtherCancellationException(e)
          case e: OtherCancellationException => throw new OtherCancellationException(e)
          case e => throw e
        }
      }
    }
  }
  ec.execute(task)

  /**
    * Returns a [[Future]] which will contain the result of this task
    * when it is completed.
    *
    * @return a Future which will contain the result of this task
    *         when it is completed
    */
  def future: Future[T] = promise.future

  /**
    * Attempts to cancel this task.
    *
    * This method behaves the same as
    * [[java.util.concurrent.FutureTask `java.util.concurrent.FutureTask.cancel()`]].
    * See its documentation for more details.
    *
    * @param mayInterruptIfRunning whether or not to attempt to cancel the task
    *                              if it has already been started
    * @return `true` if the task was cancelled by this invocation; `false` otherwise
    */
  def cancel(mayInterruptIfRunning: Boolean): Boolean = task.cancel(mayInterruptIfRunning)
}

object CancellableTask {
  /**
    * Creates a cancellable task which will be executed asynchronously.
    *
    * @param body the task to execute
    * @param ec the context in which to execute the task
    * @tparam T the return type of `body`
    * @return a cancellable task with the specified body
    */
  def apply[T](body: => T)(implicit ec: ExecutionContext): CancellableTask[T] = new CancellableTask(body, ec)
}
