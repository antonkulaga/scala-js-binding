package org.denigma.preview

import org.denigma.binding.binders.{Events, Binder, GeneralBinder}
import org.denigma.binding.macroses._
import org.denigma.binding.views.BindableView
import org.denigma.binding.views.collections.CollectionView
import org.scalajs.dom
import org.scalajs.dom.raw.HTMLElement
import rx.Rx
import rx.core.Var

import scala.collection.immutable.Seq
import scala.util.Try

case class TextSelection(value:String,label:String) extends Selection[String]

trait Selection[T]{
  def value:T
  def label:T
}

class SelectionView(val elem:HTMLElement,val params:Map[String,Any]) extends CollectionView{
  import org.denigma.binding.extensions._

  type Item = Var[TextSelection]
  type ItemView = OptionView

  val states:Vector[(String,String)] = {
    val mp = CSV.toDataFrame("/state_table.csv")
    mp.name.zip(mp.abbreviation)
  }

  protected def typeHandler(typed:String):Unit= {
    println("TYPED IS: "+typed)
  }
  val input = Var("")
  input.onChange(typeHandler _)



  val options:Var[Seq[Var[TextSelection]]] = Var(states.map{case (label,value)=>Var(TextSelection(value,label))})

  val items: Var[Seq[Var[TextSelection]]] = Var(options.now.take(3))


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
