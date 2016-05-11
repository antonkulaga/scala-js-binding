package org.denigma.preview.communication

import akka.NotUsed
import akka.actor.{ActorRef, ActorSystem, Props}
import akka.http.scaladsl.model.ws._
import akka.stream._
import akka.stream.scaladsl._
import akka.util.ByteString
import boopickle.DefaultBasic._
import com.typesafe.config.Config
import org.denigma.preview.FileManager
import org.denigma.preview.communication.SocketMessages.{ChannelMessage, IncomingMessage, OutgoingMessage, UserLeft}
import org.denigma.preview.messages.WebMessages
import org.denigma.preview.messages.WebMessages.ServerErrors

class WebSocketManager(system: ActorSystem, fileManager: FileManager){

  val config: Config = system.settings.config

  val allRoom = system.actorOf(Props(classOf[RoomActor], "all"))

  protected def makeIncomingFlow(channel: String, username: String) = Flow[Message].map {  case mes => SocketMessages.IncomingMessage(channel, username, mes) }

  protected val outgoingFlow = Flow[SocketMessages.OutgoingMessage].map{ case SocketMessages.OutgoingMessage(_, _, message, _) => message }


  /**
    * Creates a websocket flow to process
    * @param channel name of a websocket channel to connect to
    * @param username name of a user that connects
    * @return
    */
  def openChannel(channel: String, username: String = "guest"): Flow[Message, Message, Any] = {
    val partial: Graph[FlowShape[Message, Message], ActorRef] = GraphDSL.create(
      Source.actorPublisher[OutgoingMessage](Props(classOf[UserActor],
        username))
    )
    {
      implicit builder => user =>
        import GraphDSL.Implicits._

        val fromWebsocket: FlowShape[Message, IncomingMessage] = builder.add( makeIncomingFlow(channel, username) )
        val backToWebsocket: FlowShape[OutgoingMessage, Message] = builder.add( outgoingFlow )
        val actorAsSource: PortOps[SocketMessages.ChannelMessage] = builder.materializedValue.map{ case actor =>  SocketMessages.UserJoined(username, channel, actor) }

        //send messages to the actor, if send also UserLeft(user) before stream completes.
        val chatActorSink: Sink[ChannelMessage, NotUsed] = Sink.actorRef[SocketMessages.ChannelMessage](allRoom, UserLeft(username, channel))

        val merge: UniformFanInShape[ChannelMessage, ChannelMessage] = builder.add(Merge[SocketMessages.ChannelMessage](2))
        //Message from websocket is converted into IncommingMessage and should be send to each in room
        fromWebsocket ~> merge.in(0)

        //If Source actor is just created should be send as UserJoined and registered as particiant in room
        actorAsSource ~> merge.in(1)

        //Merges both pipes above and forward messages to chatroom Represented by ChatRoomActor
        merge ~> chatActorSink

        user ~> backToWebsocket

        FlowShape( fromWebsocket.in, backToWebsocket.out )

    }.named("socket_flow")

    Flow.fromGraph(partial).recover { case ex =>
      val message = s"WS stream for $channel failed for $username with the following cause:\n  $ex"
      this.system.log.error(message)
      val d = Pickle.intoBytes[WebMessages.Message](ServerErrors(List(message)))
      BinaryMessage(ByteString(d))
      //throw ex

    }
  }//.via(reportErrorsFlow(channel, username)) // ... then log any processing errors on stdin

}

/*
import akka.NotUsed
import akka.http.scaladsl.model.ws.BinaryMessage.Strict
import akka.http.scaladsl.model.ws._
import akka.stream.scaladsl.{Source, Sink, Flow}
import akka.stream.stage.{TerminationDirective, SyncDirective, Context, PushStage}
import akka.util.ByteString
import boopickle.Default._
import org.denigma.controls.models.{Suggestion, WebPicklers, Suggest, WebMessage}
import org.denigma.preview.data.TestOptions

import scala.util.Try

object SuggesterProvider extends WebPicklers {

  val testOptions = TestOptions

  def openChannel(channel: String, username: String = "guest"): Flow[Message, Message, NotUsed] = (channel, username) match {
    case (_, _) =>
      Flow[Message].collect {
        case BinaryMessage.Strict(data) =>
          Unpickle[WebMessage].fromBytes(data.toByteBuffer) match {
            case Suggest(inp, ch) =>
              val sug = Suggestion(inp,channel,testOptions.search(inp)) //cases error
              val d = Pickle.intoBytes[WebMessage](sug)
              val mess: Message = BinaryMessage.Strict(ByteString(d))
              mess
          }
      }.via(reportErrorsFlow(channel, username)) // ... then log any processing errors on stdin
  }


  def reportErrorsFlow[T](channel: String, username: String): Flow[T, T, NotUsed] =
    Flow[T]
      .transform(() ⇒ new PushStage[T, T] {
        def onPush(elem: T, ctx: Context[T]): SyncDirective = ctx.push(elem)

        override def onUpstreamFailure(cause: Throwable, ctx: Context[T]): TerminationDirective = {
          println(s"WS stream for $channel failed for $username with the following cause:\n  $cause")
          super.onUpstreamFailure(cause, ctx)
        }
      })

}
*/