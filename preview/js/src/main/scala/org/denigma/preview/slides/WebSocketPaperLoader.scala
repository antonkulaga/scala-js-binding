package org.denigma.preview.slides


import org.denigma.controls.papers._
import org.denigma.preview.WebMessagesTransport
import org.denigma.preview.messages.WebMessages
import rx._

import scala.collection.immutable._
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.scalajs.js.typedarray.ArrayBuffer

case class WebSocketPaperLoader(subscriber: WebMessagesTransport,
                                loadedPapers: Var[Map[String, Paper]])
  extends PaperLoader {

  override def getPaper(path: String, timeout: FiniteDuration = 25 seconds): Future[Paper] =
    this.subscriber.ask[Future[ArrayBuffer]](WebMessages.Load(path), timeout){
      case WebMessages.DataMessage(source, bytes) =>
        bytes2Arr(bytes)
    }.flatMap{case arr=>arr}.flatMap{ case arr=>  super.getPaper(path, arr) }

  subscriber.open()

}
