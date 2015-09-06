package org.denigma.controls.selection

import org.denigma.binding.binders.{Events, GeneralBinder}
import org.denigma.binding.extensions._
import org.scalajs.dom.ext.KeyCode
import org.scalajs.dom.raw.HTMLElement
import rx.core._
import rx.ops._
import scala.collection.immutable.SortedSet

case class TypedSuggester(input:Rx[String],options:Var[SortedSet[TextSelection]],minLen:Double=3){
  self=>

  //note: it is very buggy
  def position(value:String,opt:TextSelection):Int = {
    val i = opt.label.indexOf(value)
    opt.label.length - value.length - Math.round(i/2)
  }

  lazy val suggestions:Rx[SortedSet[TextSelection]] = input.map {
    case small if small.length < minLen => SortedSet[TextSelection]()
    case str =>
      options.now.collect{ case op if op.label.contains(str) => op.copy()(position = self.position(str, op)) }
  }

}

trait TextSelectionView extends SelectionView
{

  val suggester:TypedSuggester
  
  type Item = Var[TextSelection]
  type ItemView = OptionView

  val onkeydown = Var(Events.createKeyboardEvent(),my("onkeydown"))
  val keyDownHandler = onkeydown.onChange(my("keydown_handler"))(event=>{
    if(input.now=="")
      event.keyCode match  {
        case KeyCode.Left=>  moveLeft()
        case KeyCode.Right=> moveRight()
        case KeyCode.Backspace => items.set(items.now.filterNot(i=>i.now.position==position.now-1))
        case KeyCode.Delete => items.set(items.now.filterNot(i=>i.now.position==position.now))
        case KeyCode.Enter => //items() = items.now.ins
        case other=>
      }
  })


  val unique = this.resolveKeyOption("unique"){case u:Boolean=>u}.getOrElse(true)

  lazy val options = suggester.suggestions.map(_.map(Var(_)))

  lazy val items:Var[SortedSet[Item]] = Var(SortedSet.empty)

  protected def typed2Item(str:String):Item = Var(TextSelection(str,str)(position.now))

  override lazy val injector = defaultInjector
    .register("options"){  case (el,args)=>
      new SelectOptionsView(el,options,args)
    }

  override def newItem(item: Item): OptionView = constructItemView(item){
    case (el,mp)=>new OptionView(el,item, mp).withBinder{new GeneralBinder(_)}
  }
}