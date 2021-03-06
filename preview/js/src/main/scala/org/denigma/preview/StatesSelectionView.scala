package org.denigma.preview

import java.nio.ByteBuffer

import boopickle.DefaultBasic._
import org.denigma.binding.binders.Events
import org.denigma.controls.models.{Suggest, WebMessage}
import org.denigma.controls.selection._
import org.denigma.controls.sockets.WebSocketSubscriber
import org.denigma.preview.data.TestOptions
import org.denigma.preview.messages.WebMessages
import org.denigma.preview.messages.WebMessages.Data
import org.scalajs.dom.Element
import rx.Ctx.Owner.Unsafe.Unsafe
import rx._

import scala.util.{Failure, Success, Try}

class WebSocketSuggester(val input: Rx[String], val subscriber: WebMessagesTransport) extends TextOptionsSuggester {

  protected def onDelayedInput(inp: String): Unit = {
    if(inp.length >= minSuggestLength){
      val bytes: ByteBuffer = Pickle.intoBytes[WebMessages.Message](Data(Suggest(inp, subscriber.channel)))
      subscriber.send(subscriber.bytes2message(bytes))
    }
  }

  protected def unpickle(bytes: ByteBuffer): Try[WebMessage] =
    Unpickle[WebMessages.Message].fromBytes(bytes) match {
      case Data(mess)=> Success(mess)
      case other=> new Failure(new Exception("unexpected message "+other))
    }

  subscriber.open()
}

class StatesSelectionView(val elem: Element, channel: String, username: String="guest") extends TextSelectionView{

  override val suggester = new WebSocketSuggester(input, new WebMessagesTransport(channel, username))

  override lazy val items: Var[collection.immutable.SortedSet[Item]] = Var(TestOptions.items.map(i=>Var(i)))

  suggester.subscriber.input.foreach(mess => println("WEBSOCKET INPUT ="+mess))

  suggester.subscriber.output.foreach(mess => println("WEBSOCKET OUTPUT ="+mess))


}

