package com.nthportal.concurrent

import org.scalatest.{FlatSpec, Matchers}

import scala.concurrent.{Await, CancellationException}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

class CancellableTaskTest extends FlatSpec with Matchers {

  behavior of "CancellableTask"

  it should "contain the result of the task" in {
    val t1 = CancellableTask {true}
    Await.result(t1.future, Duration.Inf) should be (true)

    val ex = new Exception
    val t2 = CancellableTask {throw ex}
    Await.result(t2.future.failed, Duration.Inf) should be theSameInstanceAs ex
  }

  it should "cancel" in {
    val task = CancellableTask {Thread.sleep(5000)}
    task.cancel(true) should be (true)
    task.isCancelled should be(true)
    Await.result(task.future.failed, Duration.Zero) shouldBe a [CancellationException]
  }

  it should "wrap CancellationExceptions" in {
    val ex = new CancellationException("Not actually cancelled")

    val t1 = CancellableTask {throw ex}
    val res1 = Await.result(t1.future.failed, Duration.Inf)
    res1 shouldBe an [OtherCancellationException]
    res1.getCause should be theSameInstanceAs ex

    val t2 = CancellableTask {throw new OtherCancellationException(ex)}
    val res2 = Await.result(t2.future.failed, Duration.Inf)
    res2 shouldBe an [OtherCancellationException]
    res2.getCause shouldBe an [OtherCancellationException]
    res2.getCause.getCause should be theSameInstanceAs ex
  }
}
