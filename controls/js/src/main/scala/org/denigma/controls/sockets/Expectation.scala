package org.denigma.controls.sockets

import rx._
import scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.concurrent.Promise
import scala.concurrent.duration.FiniteDuration
import rx.Ctx.Owner.Unsafe.Unsafe

case class TimeoutException(message: String, timeout: FiniteDuration) extends Throwable

case class RetryExpectation[Input, Output](input: Rx[Input], resend:()=>Unit, timeout: FiniteDuration, retries: Int = 2)
                                            (val partialFunction: PartialFunction[Input, Output])
  extends Expectation[Input, Output]{

  //private var counter = retries

  import scala.scalajs.js.timers._

  protected def wait(counter: Int): Unit =  {
    if(counter < 0 && !promise.isCompleted)
      promise.failure(
        TimeoutException("Expectation timeout has passed", timeout)
      )
    else setTimeout(timeout)(wait(counter - 1))
  }

  wait(retries)

}

case class TimeoutExpectation[Input, Output](input: Rx[Input], timeout: FiniteDuration)
                                          (val partialFunction: PartialFunction[Input, Output])
  extends Expectation[Input, Output]{

  import scala.scalajs.js.timers._

  setTimeout(timeout){
    if(!promise.isCompleted){
      println("timeout passed")
      promise.failure(
        TimeoutException("Expectation timeout has passed", timeout)
      )
    }

  }

}

trait Expectation[Input, Output] {

  //type Input
  //type Output

  def input: Rx[Input]

  def partialFunction: PartialFunction[Input, Output]

  protected val promise = Promise[Output]
  val inputObservable = input.triggerLater {
    val mes = input.now
    if (partialFunction.isDefinedAt(mes)) {
      println("it works!")
      promise.success(partialFunction(mes))
    }
  }

  lazy val future = promise.future
  future.onComplete{
    case _ => inputObservable.kill()
  }
}
