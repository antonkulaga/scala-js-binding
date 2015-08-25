package org.denigma.preview

import org.denigma.binding.binders.{Events, GeneralBinder}
import org.denigma.binding.macroses._
import org.denigma.binding.views.{ItemsSetView, ItemsSeqView}
import org.denigma.controls.selection.{SelectOptionsView, OptionView, TextSelection, TextSelectionView}
import org.scalajs.dom.raw.HTMLElement
import rx.Rx
import rx.core.Var
import rx.ops._

import scala.collection.immutable.{Seq, SortedSet}


class SelectionView(val elem:HTMLElement,val params:Map[String,Any]) extends TextSelectionView
{

  import org.denigma.binding.extensions._

  private def my(str:String) = str+"_of_"+this.id //to name Vars for debugging purposes


  val unique = this.resolveKeyOption("unique"){case u:Boolean=>u}.getOrElse(true)

  val input = Var("","input_of"+this.id)

  val states:Vector[(String,String)] = {
    val mp = CSV.toDataFrame("preview/data/state_table.csv")
    mp.name.zip(mp.abbreviation)
  }

  val testData = states.zipWithIndex.map{case ((label,value),index)=>Var(TextSelection(value,label,index))}

  val options:Var[SortedSet[Var[TextSelection]]] = Var(SortedSet(testData:_*)(ordering),my("options"))

  val items:Var[SortedSet[Item]] = Var(options.now.take(5))

  val positionShift = Var(1,my("positionShift"))

  val position = Rx.apply{
    items().size+positionShift()
  }

  protected def moveLeft() = if(position.now > -1) positionShift.set(positionShift.now-1)
  protected def moveRight() = if(position.now<items.now.size) positionShift.set(positionShift.now+1)


  //val items: Var[Seq[Var[TextSelection]]] = Var(testData.take(3).zipWithIndex.map{ case (o,i)=> Var(o.copy(position=i)) }) //just for the sake of tests

  val onkeydown = Var(Events.createKeyboardEvent(),my("onkeydown"))
  val keyDownHandler = onkeydown.onChange(my("keydown_handler"))(event=>{
    if(input.now=="")
      event.keyCode match  {
        case LeftKey(_)=> positionShift.set(positionShift.now-1)
        case RightKey(_)=> positionShift.set(positionShift.now+1)
        case BackspaceKey(_) => items.set(items.now.filterNot(i=>i.now.position==position.now)) //note it can be buggy
        case DeleteKey(_) => items.set(items.now.filterNot(i=>i.now.position==position.now)) //note it can be buggy
        case EnterKey(_) => //items() = items.now.ins
        case other=>
    }
  })

  protected def typed2Item(str:String):Item = Var(TextSelection(str,str,position.now))


  protected def typeHandler(typed:String):Unit= {
    println("TYPED IS: "+typed)
  }
  input.onChange(typeHandler _)



    override lazy val injector = defaultInjector
    //.register("suggestions"){  case (el,args)=> new SelectOptionsView(el,options,args) }
    .register("options"){  case (el,args)=> new SelectOptionsView(el,options,args) }


  override def newItem(item: Item): OptionView = constructItemView(item){
    case (el,mp)=>new OptionView(el,item, mp).withBinder{new GeneralBinder(_)}
  }


}
