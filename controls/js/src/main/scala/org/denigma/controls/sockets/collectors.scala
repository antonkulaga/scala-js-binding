package org.denigma.controls.sockets

import rx.Ctx.Owner.Unsafe.Unsafe
import rx.{Rx, _}

import scala.concurrent.Promise
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

class Collector[Input, Output]
  (input: Rx[Input])
  (collect: PartialFunction[Input, Output])
  (until: PartialFunction[Input, Boolean])
  {
    protected val promise = Promise[List[Output]]

    val collection: Rx[List[Output]] = input.fold(List.empty[Output]){
      case (acc, el)=> if (collect.isDefinedAt(el)) collect(el)::acc else acc
    }

    val inputObservable = input.triggerLater {
      val mes = input.now
      if (until.isDefinedAt(mes) && until(mes)) {
        val result = collection.now.reverse
        collection.kill()
        promise.success(result)
      }
    }

    lazy val future = promise.future
    future.onComplete{
      case _ =>
        inputObservable.kill()
    }
  }
