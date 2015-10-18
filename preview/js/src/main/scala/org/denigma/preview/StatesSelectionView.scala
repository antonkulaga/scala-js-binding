package org.denigma.preview

import org.denigma.binding.binders.Events
import org.denigma.controls.selection._
import org.denigma.controls.sockets.WebSocketSubscriber
import org.denigma.preview.data.TestOptions
import org.scalajs.dom.Element
import rx.core.Var

class StatesSelectionView(val elem:Element,channel:String,username:String="guest") extends TextSelectionView{

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

