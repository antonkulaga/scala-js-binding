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


trait WebSocketSuggester {
  self:Suggester=>

  protected def updateSuggestions(bytes:ByteBuffer):Unit

  implicit def bytes2message(data: ByteBuffer): ArrayBuffer = {
    if (data.hasTypedArray()) {
      // get relevant part of the underlying typed array
      data.typedArray().subarray(data.position, data.limit).buffer
    } else {
      // fall back to copying the data
      val tempBuffer = ByteBuffer.allocateDirect(data.remaining)
      val origPosition = data.position
      tempBuffer.put(data)
      data.position(origPosition)
      tempBuffer.typedArray().buffer
    }
  }

  protected def toByteBuffer(data: Any) = TypedArrayBuffer.wrap(data.asInstanceOf[ArrayBuffer])


  protected def onMessage(mess:MessageEvent) = {
    mess.data match{
      case str:String=>  dom.console.log(s"message from websocket: "+str)

      case blob:Blob=>
        //println("blob received:"+blob)
        val reader = new FileReader()
        def onLoadEnd(ev:ProgressEvent):Any = {
          val buff = reader.result
          updateSuggestions(toByteBuffer(buff))
        }
        reader.onloadend = onLoadEnd _
        reader.readAsArrayBuffer(blob)

      case buff:ArrayBuffer=>
        val bytes = TypedArrayBuffer.wrap(buff)
        updateSuggestions(bytes)
    }
  }

}

case class TextOptionsSuggester(input:Rx[String],subscriber:WebSocketSubscriber)
  extends Suggester with WebPicklers with WebSocketSuggester
{

  import scala.concurrent.duration._

  lazy val inputDelay = 1.5 second

  val dalayedInput = input.afterLastChange(inputDelay)
  dalayedInput.onChange("on_input"){case inp=>
      import boopickle.Default._
      val bytes: ByteBuffer = Pickle.intoBytes[WebMessage](Suggest(inp,subscriber.channel))
      subscriber.send(bytes2message(bytes))
  }

  /*subscriber.onOpen.onChange("onOpen") {
    case op=>
      dom.alert("OPENED")
  }

  subscriber.onClose.onChange("closed"){
    case cl=> dom.alert("closed")
  }*/


  protected def updateSuggestions(bytes:ByteBuffer) = {
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

/*case class TypedSuggester(input:Rx[String],options:Var[immutable.Seq[TextOption]],minLen:Double=2)
extends Suggester
{
  self=>
  //note: it is very buggy
  def position(value:String,opt:TextOption):Int = {
    val i = opt.label.indexOf(value)
    opt.label.length - value.length - Math.round(i/2)
  }

  lazy val suggestions:Rx[scala.collection.immutable.Seq[TextOption]] = input.map {
    case small if small.length < minLen => scala.collection.immutable.Seq[TextOption]()
    case str =>  options.now.collect{ case op if op.label.contains(str) =>
      op.copy(position = self.position(str, op) )
    }
  }

}*/

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
        input() = ""
        items() = items.now + item
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
      items.set(items.now+selection.item)
      input() = ""
      suggester.clear()

    case event:ViewEvent=> this.propagate(event)
  }

  val unique = true//this.resolveKeyOption("unique"){case u:Boolean=>u}.getOrElse(true)

  lazy val options: rx.Rx[collection.immutable.Seq[Var[TextOption]]] = suggester.suggestions.map(_.map(Var(_)))

  lazy val items:Var[SortedSet[Item]] = Var(SortedSet.empty)

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