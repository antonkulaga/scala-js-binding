package org.denigma.controls.selection

import java.nio.ByteBuffer

import boopickle.Default._
import org.denigma.binding.binders.{Events, GeneralBinder}
import org.denigma.binding.extensions._
import org.denigma.binding.views.ViewEvent
import org.denigma.controls.models._
import org.denigma.controls.sockets.WebSocketSubscriber
import org.scalajs.dom
import org.scalajs.dom.ext.KeyCode
import org.scalajs.dom.raw.{MessageEvent, FileReader}
import org.scalajs.dom.{ProgressEvent, Blob, KeyboardEvent}
import rx.core._
import rx.ops._

import scala.collection.immutable
import scala.collection.immutable._
import scala.scalajs.js.typedarray.TypedArrayBufferOps._
import scala.scalajs.js.typedarray.{Int8Array, ArrayBuffer, TypedArrayBuffer}




case class TextOptionsSuggester(input:Rx[String],subscriber:WebSocketSubscriber)
  extends Suggester with WebPicklers with BinaryWebSocket
{

  import scala.concurrent.duration._

  lazy val inputDelay = 0.4 seconds

  lazy val minSuggestLength = 1

  val dalayedInput = input.afterLastChange(inputDelay)
  dalayedInput.onChange("on_input"){case inp=>
    if(inp.length>=minSuggestLength){
      import boopickle.Default._
      val bytes: ByteBuffer = Pickle.intoBytes[WebMessage](Suggest(inp,subscriber.channel))
      subscriber.send(bytes2message(bytes))
    }
  }

  protected def updateFromMessage(bytes:ByteBuffer) = {
    Unpickle[WebMessage].fromBytes(bytes) match {
      case Suggestion(inp,channel,sug)=>
        suggestions() = sug.toList

      case other=>  println("something other was found! "+other)
    }
  }

  this.subscriber.onMessage.onChange("onMessage")(onMessage)

  lazy val suggestions:Var[scala.collection.immutable.Seq[TextOption]] = Var(Seq.empty)

  def clear() = suggestions() = immutable.Seq.empty
}

trait Suggester {
  def suggestions:Rx[scala.collection.immutable.Seq[TextOption]]

  def clear():Unit
}

trait TextSelectionView extends SelectionView
{
  val suggester:Suggester
  type Item = Var[TextOption]
  type ItemView = OptionView

  val onkeydown: Var[KeyboardEvent] = Var(Events.createKeyboardEvent(),my("onkeydown"))
  val keyDownHandler = onkeydown.onChange(my("keydown_handler"))(event=>{
    val clean = input.now==""
    event.keyCode match  {
      case KeyCode.Left if clean=>  moveLeft()
      case KeyCode.Right if clean=> moveRight()
      case KeyCode.Backspace if clean => items.set(items.now.filterNot(i=>i.now.position==position.now-1))
      case KeyCode.Delete if clean => items.set(items.now.filterNot(i=>i.now.position==position.now))
      case KeyCode.Enter =>
        val str = input.now
        val item = typed2Item(str)
        //println(s"item $item is created from $str")
        items() = items.now + item
        input() = ""
      case KeyCode.Down =>
        //val opts = this.subviews.values.collectFirst{   case v:SelectOptionsView=> v   } //KOSTYL TODO:fix it!
        //opts
      case other=>
    }
  })

  override def receive:PartialFunction[ViewEvent,Unit] = {
    case selection:SelectTextOptionEvent=>
      //println("selection received = "+selection)
      val i = selection.item
      i.set(i.now.copy(preselected = false))
      println("ITEMS BEFORE = "+items.now.toList.map(_.now.value).mkString(" | "))
      items.set(items.now+i)
      println(i.now.label+" | "+i.now.value)
      println("ITEMS AFTER = "+items.now.toList.map(_.now.value).mkString(" | "))

      input() = ""
      suggester.clear()

    case event:ViewEvent=> this.propagate(event)
  }

  val unique = true//this.resolveKeyOption("unique"){case u:Boolean=>u}.getOrElse(true)

  lazy val options: rx.Rx[collection.immutable.Seq[Var[TextOption]]] = suggester.suggestions.map(sgs=>sgs.collect{
    case opt if items.now.forall(i=>i.now!=opt)=>Var(opt) //TODO: check how equality for vars is done
  })

  lazy val items:Var[SortedSet[Item]] = Var(SortedSet.empty)

  protected def onPositionChange(pos:Int):Unit = if(pos<items.now.size){
    for{
      item <- items.now
      i = item.now
      if i.position ==pos
    }{
      item() = i.copy(position = pos+1) //TODO: buggy
    }
  }

  position.onChange("positionChange")(onPositionChange)


  protected def typed2Item(str:String):Item = Var(TextOption(str,str,position.now))

  override lazy val injector = defaultInjector
    .register("options"){  case (el,args)=>
      val optsView = new TextOptionsView(el,options,onkeydown).withBinder{new GeneralBinder(_)}
      optsView
    }

  override def newItem(item: Item): OptionView = constructItemView(item){
    case (el,mp)=> new OptionView(el,item).withBinder{new GeneralBinder(_)}
  }
}