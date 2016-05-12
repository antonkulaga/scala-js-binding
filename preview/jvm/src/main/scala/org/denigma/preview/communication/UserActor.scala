package org.denigma.preview.communication
import java.io.{InputStream, File => JFile}
import java.nio.ByteBuffer
import java.time._

import akka.actor.Actor
import akka.http.scaladsl.model.ws.{BinaryMessage, TextMessage}
import akka.stream.actor.{ActorPublisher, ActorPublisherMessage}
import akka.util.ByteString
import boopickle.DefaultBasic._
import org.denigma.controls.models.{Suggest, Suggestion}
import org.denigma.preview.FileManager
import org.denigma.preview.communication.SocketMessages.OutgoingMessage
import org.denigma.preview.data.TestOptions
import org.denigma.preview.messages.WebMessages
import org.denigma.preview.messages.WebMessages.{Connected, Disconnected}

import scala.annotation.tailrec
trait SomeMessage

class UserActor(username: String, fileManager: FileManager) extends Actor
  with akka.actor.ActorLogging
  with ActorPublisher[SocketMessages.OutgoingMessage]
{

  implicit def ctx = context.dispatcher

  val MaxBufferSize = 100
  var buf = Vector.empty[OutgoingMessage]

  val testOptions = TestOptions

  @tailrec final def deliverBuf(): Unit =
    if (totalDemand > 0) {
      /*
      * totalDemand is a Long and could be larger than
      * what buf.splitAt can accept
      */
      if (totalDemand <= Int.MaxValue) {
        val (use, keep) = buf.splitAt(totalDemand.toInt)
        buf = keep
        use foreach onNext
      } else {
        val (use, keep) = buf.splitAt(Int.MaxValue)
        buf = keep
        use foreach onNext
        deliverBuf()
      }
    }

  //val servers: Map[String, WebSimClient] = Map("default", new WebSimClient()(this.context.system, ActorMaterializer())) //note: will be totally rewritten


  def readResource(path: String): Iterator[String] = {
    val stream: InputStream = getClass.getResourceAsStream(path)
    scala.io.Source.fromInputStream( stream ).getLines
  }


  protected def onTextMessage: Receive = {
    case SocketMessages.IncomingMessage(channel, uname, TextMessage.Strict(text), time) =>
      log.error("there is not handler for text message right now!")
  }

  protected def onBinaryMessage: Receive = {
    case SocketMessages.IncomingMessage(channel, uname, message: BinaryMessage.Strict, time) =>
      Unpickle[WebMessages.Message].fromBytes(message.data.toByteBuffer) match
      {
        case WebMessages.Data(Suggest(inp, ch)) =>
          val sug = Suggestion(inp,channel, testOptions.search(inp)) //cases error
          val d = Pickle.intoBytes[WebMessages.Message](WebMessages.Data(sug))
          send(d)

        case mess @ WebMessages.Load(path) =>
          fileManager.readBytes(path) match {
            case Some(bytes)=>
              println("bytes received "+bytes.length)
              val m = WebMessages.DataMessage(mess, bytes)
              val d = Pickle.intoBytes[WebMessages.Message](m)
              send(d)
            case None =>
          }

        case other => log.error(s"unexpected $other")
      }
    //log.error(s"something binary received on $channel by $username")
  }

  protected def onServerMessage: Receive = {

    case result: Connected =>
      val d = Pickle.intoBytes[WebMessages.Message](result)
      send(d)

    case d @ Disconnected(user, channel, participants) =>
      log.info(s"User $user disconnected from channel $channel")

  }


  protected def onOtherMessage: Receive = {

    case ActorPublisherMessage.Request(n) => deliverBuf()

    case other => log.error(s"Unknown other message: $other")
  }


  override def receive: Receive =  onTextMessage.orElse(onBinaryMessage).orElse(onServerMessage).orElse(onOtherMessage)

  def deliver(mess: OutgoingMessage) = {
    if (buf.isEmpty && totalDemand > 0)
      onNext(mess)
    else {
      buf :+= mess
      deliverBuf()
    }
  }

  def send(textMessage: TextMessage, channel: String): Unit = {
    val message = SocketMessages.OutgoingMessage(channel, username, textMessage, LocalDateTime.now)
    deliver(message)
  }

  def sendBinary(binaryMessage: BinaryMessage, channel: String = "all") = {
    val message = SocketMessages.OutgoingMessage(channel, username, binaryMessage, LocalDateTime.now)
    deliver(message)
  }

  def send(d: ByteBuffer, channel: String = "all"): Unit = {
    sendBinary(BinaryMessage(ByteString(d)), channel)
  }

}
