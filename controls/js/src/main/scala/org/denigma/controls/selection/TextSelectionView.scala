package org.denigma.controls.selection

import org.denigma.binding.binders.{Events, GeneralBinder}
import org.denigma.binding.extensions._
import org.denigma.binding.views.ViewEvent
import org.scalajs.dom.KeyboardEvent
import org.scalajs.dom.ext.KeyCode
import rx.core._
import rx.ops._
import scala.collection.immutable
import scala.collection.immutable._

case class TypedSuggester(input:Rx[String],options:Var[immutable.Seq[TextSelection]],minLen:Double=3)
{
  self=>
  //note: it is very buggy
  def position(value:String,opt:TextSelection):Int = {
    val i = opt.label.indexOf(value)
    opt.label.length - value.length - Math.round(i/2)
  }

  lazy val suggestions:Rx[scala.collection.immutable.Seq[TextSelection]] = input.map {
    case small if small.length < minLen => scala.collection.immutable.Seq[TextSelection]()
    case str =>  options.now.collect{ case op if op.label.contains(str) => op.copy()(position = self.position(str, op),op.preselected) }
  }
}

trait TextSelectionView extends SelectionView
{
  val suggester:TypedSuggester
  type Item = Var[TextSelection]
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
      items.set(items.now+selection.item)
      input() = ""

    case event:ViewEvent=> this.propagate(event)
  }

  val unique = true//this.resolveKeyOption("unique"){case u:Boolean=>u}.getOrElse(true)

  lazy val options: rx.Rx[collection.immutable.Seq[Var[TextSelection]]] = suggester.suggestions.map(_.map(Var(_)))

  lazy val items:Var[SortedSet[Item]] = Var(SortedSet.empty)

  protected def typed2Item(str:String):Item = Var(TextSelection(str,str)(position.now))

  override lazy val injector = defaultInjector
    .register("options"){  case (el,args)=>
      val optsView = new TextOptionsView(el,options,onkeydown).withBinder{new GeneralBinder(_)}
      optsView
    }

  override def newItem(item: Item): OptionView = constructItemView(item){
    case (el,mp)=> new OptionView(el,item).withBinder{new GeneralBinder(_)}
  }
}