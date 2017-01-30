/*
 * This file is adapted and modified from Stratio/common-utils
 * (https://github.com/Stratio/common-utils/blob/b9195e3a2b206bb65bf61b412371cf07858d5450/src/main/scala/com/stratio/common/utils/concurrent/Cancellable.scala),
 * as well as from this Stack Overflow answer
 * (https://stackoverflow.com/a/39986418/5101123).
 *
 * The original implementation is licenced under the Apache License, Version 2.0,
 * and the original license is reproduced below. This file is licensed under the
 * Apache License, Version 2.0 as well.
 */

/*
 * ---- ORIGINAL LICENSE AND COPYRIGHT INFORMATION ----
 *
 * Copyright (C) 2015 Stratio (http://stratio.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nthportal.concurrent

import java.util.concurrent.{Callable, FutureTask}

import scala.concurrent._
import scala.util.Try

/**
  * An asynchronously executed task which can be cancelled
  * before or during its execution.
  *
  * @param body the task to execute
  * @param ec   the context in which to execute the task
  * @tparam T the return type of `body`
  */
final class CancellableTask[T] private(body: => T, ec: ExecutionContext) {
  private val promise = Promise[T]()

  private val task = new FutureTask[T](new Callable[T] {override def call() = body}) {
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

  /**
    * Returns `true` if the task was cancelled before completing; `false` otherwise.
    *
    * If this method returns `true`, then [[future]] will fail with a
    * [[CancellationException]].
    *
    * @return `true` if the task was cancelled before completing; `false` otherwise
    */
  def isCancelled: Boolean = task.isCancelled
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
