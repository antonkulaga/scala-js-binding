package org.denigma.preview

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

  def openChannel(channel: String, username: String = "guest"): Flow[Message, Message, Unit] = (channel, username) match {
    case (_, _) =>
      Flow[Message].collect {
        case BinaryMessage.Strict(data) =>
          Unpickle[WebMessage].fromBytes(data.toByteBuffer) match {
            case Suggest(inp, ch) =>
              val sug = Suggestion(inp,channel,testOptions.search(inp)) //cases error
              val d = Pickle.intoBytes[WebMessage](sug)
              BinaryMessage(ByteString(d))
          }
      }.via(reportErrorsFlow(channel,username)) // ... then log any processing errors on stdin
  }


  def reportErrorsFlow[T](channel:String,username:String): Flow[T, T, Unit] =
    Flow[T]
      .transform(() ⇒ new PushStage[T, T] {
        def onPush(elem: T, ctx: Context[T]): SyncDirective = ctx.push(elem)

        override def onUpstreamFailure(cause: Throwable, ctx: Context[T]): TerminationDirective = {
          println(s"WS stream for $channel failed for $username with the following cause:\n  $cause")
          super.onUpstreamFailure(cause, ctx)
        }
      })

}
