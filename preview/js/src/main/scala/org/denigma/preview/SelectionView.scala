package org.denigma.preview

import org.denigma.binding.binders.{Events, GeneralBinder}
import org.denigma.binding.macroses._
import org.denigma.binding.views.{ItemsSetView, ItemsSeqView}
import org.denigma.controls.selection.{SelectOptionsView, OptionView, TextSelection, TextSelectionView}
import org.scalajs.dom.raw.HTMLElement
import rx.Rx
import rx.core.Var
import rx.ops._
import org.denigma.binding.extensions._

import scala.collection.immutable.{Seq, SortedSet}
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

case object TestOptions{
  val states:Vector[(String,String)] = {
    val mp = CSV.toDataFrame("preview/data/state_table.csv")
    mp.name.zip(mp.abbreviation)
  }

  lazy val data = states.zipWithIndex.map{case ((label,value),index)=>TextSelection(value,label)(index)}

  lazy val options: SortedSet[TextSelection] = SortedSet(data:_*)

  val items = options.take(3)

}


class SelectionView(val elem:HTMLElement,val params:Map[String,Any]) extends TextSelectionView
{


  private def my(str:String) = str+"_of_"+this.id //to name Vars for debugging purposes

  val unique = this.resolveKeyOption("unique"){case u:Boolean=>u}.getOrElse(true)

  val input: Var[String] = Var("","input_of_"+this.id)

  val suggester =  TypedSuggester(input,Var(TestOptions.options))

  lazy val options = suggester.suggestions.map(_.map(Var(_)))

  val items:Var[SortedSet[Item]] = Var(TestOptions.items.map(Var(_)))

  val positionShift = Var(0,my("positionShift"))

  val position: Rx[Int] = Rx{
    items().size+positionShift()
  }

  val order: Rx[String] = position.map(_.toString)

  protected def moveLeft() = if(position.now > -1) positionShift.set(positionShift.now-1)
  protected def moveRight() = if(position.now<items.now.size) positionShift.set(positionShift.now+1)
  //val items: Var[Seq[Var[TextSelection]]] = Var(testData.take(3).zipWithIndex.map{ case (o,i)=> Var(o.copy(position=i)) }) //just for the sake of tests

  val onkeydown = Var(Events.createKeyboardEvent(),my("onkeydown"))
    val keyDownHandler = onkeydown.onChange(my("keydown_handler"))(event=>{
      if(input.now=="")
        event.keyCode match  {
          case LeftKey(_)=>
            //println(s"input position is ${position.now} and shift is ${positionShift.now}")
            moveLeft()
          case RightKey(_)=> moveRight()
          case BackspaceKey(_) => items.set(items.now.filterNot(i=>i.now.position==position.now)) //note it can be buggy
          case DeleteKey(_) => items.set(items.now.filterNot(i=>i.now.position==position.now)) //note it can be buggy
          case EnterKey(_) => //items() = items.now.ins
          case other=>
      }
    })

  protected def typed2Item(str:String):Item = Var(TextSelection(str,str)(position.now))

    override lazy val injector = defaultInjector
    //.register("suggestions"){  case (el,args)=> new SelectOptionsView(el,options,args) }
    .register("options"){  case (el,args)=> new SelectOptionsView(el,options,args) }


  override def newItem(item: Item): OptionView = constructItemView(item){
    case (el,mp)=>new OptionView(el,item, mp).withBinder{new GeneralBinder(_)}
  }


}
