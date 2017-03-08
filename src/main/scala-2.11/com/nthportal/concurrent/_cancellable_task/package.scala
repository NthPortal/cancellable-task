package com.nthportal.concurrent

import java.util.concurrent.Callable

package object _cancellable_task {
  @inline
  private[concurrent] def callable[A](body: => A): Callable[A] = new Callable[A] {override def call() = body}
}
