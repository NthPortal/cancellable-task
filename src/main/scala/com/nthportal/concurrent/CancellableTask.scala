package com.nthportal.concurrent

import java.util.concurrent.FutureTask

import scala.concurrent._
import scala.util.Try

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

  def future: Future[T] = promise.future

  def cancel(mayInterruptIfRunning: Boolean): Boolean = task.cancel(mayInterruptIfRunning)
}

object CancellableTask {
  def apply[T](body: => T)(implicit ec: ExecutionContext): CancellableTask[T] = new CancellableTask(body, ec)
}
