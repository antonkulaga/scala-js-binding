package org.denigma.preview

import org.denigma.binding.binders.Events
import org.denigma.controls.selection._
import org.denigma.controls.sockets.{WebSocketSubscriber, WebSocketStorage}
import org.denigma.preview.data.TestOptions
import org.scalajs.dom
import org.scalajs.dom.raw.{WebSocket, HTMLElement}
import rx.core.Var

/*case class StatesSubscriber(channel:String,username:String) extends WebSocketSubscriber{
  override def getWebSocketUri(username: String): String = {
    val wsProtocol = if (dom.document.location.protocol == "https:") "wss" else "ws"
    s"$wsProtocol://${dom.document.location.host}/connect?channel=$channel&username=$username"
  }

  override def initWebSocket(url: String) = WebSocketStorage(url)
}*/

class StatesSelection(val elem:HTMLElement,channel:String,username:String="guest") extends TextSelectionView{
  //override val suggester = new TypedSuggester(input,Var(TestOptions.options))
  override val suggester = new TextOptionsSuggester(input,WebSocketSubscriber(channel,username))

  override lazy val items:Var[collection.immutable.SortedSet[Item]] = Var(TestOptions.items.map(i=>Var(i)))

  val test = Var(Events.createMouseEvent())
  import org.denigma.binding.extensions._
  test.handler{
/*

    val l = items.now.toList
    println("before reorder = "+l.map(_.now).mkString("\n"))
    l.reverse.zipWithIndex.foreach{case (s,i)=>s() =s.now.copy()(i,s.now.preselected)}
    items() = items.now
    println("after reorder = "+items.now.toList.map(_.now).mkString("\n"))
*/

  }
}

