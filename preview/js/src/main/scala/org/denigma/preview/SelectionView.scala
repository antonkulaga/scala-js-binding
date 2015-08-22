package org.denigma.preview

import org.denigma.binding.binders.{Events, Binder, GeneralBinder}
import org.denigma.binding.macroses._
import org.denigma.binding.views.{CollectionView, BindableView}
import org.scalajs.dom
import org.scalajs.dom.raw.HTMLElement
import rx.Rx
import rx.core.Var
import org.denigma.binding.extensions._
import rx.extensions.Moved
import rx.ops._
import scala.collection.immutable.Seq
import scala.util.Try


case class TextSelection(value:String,label:String,position:Int) extends Selection[String]

trait Selection[T]{
  def value:T
  def label:T
}

class SelectionView(val elem:HTMLElement,val params:Map[String,Any]) extends CollectionView
{


  type Item = Var[TextSelection]
  type ItemView = OptionView

  val unique = this.resolveKeyOption("unique"){case u:Boolean=>u}.getOrElse(true)

  val input = Var("")

  val states:Vector[(String,String)] = {
    val mp = CSV.toDataFrame("preview/data/state_table.csv")
    mp.name.zip(mp.abbreviation)
  }

  val testData = states.zipWithIndex.map{case ((label,value),index)=>TextSelection(value,label,index)}


  val positionShift = Var(1)

  val position = Rx{
    items().length+positionShift()
  }

  protected def moveLeft() = if(position.now > -1) positionShift.set(positionShift.now-1)
  protected def moveRight() = if(position.now<items.now.length) positionShift.set(positionShift.now+1)


  val options:Var[Seq[Var[TextSelection]]] = Var(testData.map(Var(_)))

  val items: Var[Seq[Var[TextSelection]]] = Var(testData.take(3).zipWithIndex.map{ case (o,i)=> Var(o.copy(position=i)) }) //just for the sake of tests

  val onkeydown = Var(Events.createKeyboardEvent())
  val keyDownHandler = onkeydown.onChange(event=>{
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

class SelectOptionsView(val elem:HTMLElement,val items: Var[Seq[Var[TextSelection]]],val params:Map[String,Any]) extends CollectionView
{

  type Item = Var[TextSelection]

  type ItemView = OptionView

  val  hasOptions = items.map{case ops=>  ops.nonEmpty}

  override protected def warnIfNoBinders(asError:Boolean) = if(asError) super.warnIfNoBinders(asError)

  override def newItem(item: Item): OptionView = constructItemView(item){
    case (el,mp)=>new OptionView(el,item, mp).withBinder{new GeneralBinder(_)}
    }

}

class OptionView(val elem:HTMLElement,item:Var[TextSelection],val params:Map[String,Any]) extends BindableView
{
  import rx.ops._
  val label = item.map(_.label)
  val value = item.map(_.value)
  val position = item.map(_.position)
}

/*
class OneItemView(val elem:HTMLElement,val params:Map[String,Any]) extends BindableView{

  override protected def attachBinders(): Unit = this.withBinders(new GeneralBinder(this))



}

class ItemsView(val elem:HTMLElement, val params:Map[String,Any]) extends CollectionView with BindableView
{


  //injector = defaultInjector.register("")

  override protected def attachBinders(): Unit = this.withBinders(new GeneralBinder(this))



  override type Item = this.type
  override type ItemView = BindableView

  override def newItem(item: Item): ItemView= {
    ???
  }


  override val items: Rx[Seq[Item]] = Var(Seq.empty)
}
*/
