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


class PromoSelectionView(val elem:HTMLElement,val params:Map[String,Any]) extends TextSelectionView
{

  val unique = this.resolveKeyOption("unique"){case u:Boolean=>u}.getOrElse(true)

  val suggester =  TypedSuggester(input,Var(TestOptions.options))

  lazy val options = suggester.suggestions.map(_.map(Var(_)))

  lazy val items:Var[SortedSet[Item]] = Var(TestOptions.items.map(Var(_)))

  protected def typed2Item(str:String):Item = Var(TextSelection(str,str)(position.now))

  override lazy val injector = defaultInjector
    .register("options"){  case (el,args)=> new SelectOptionsView(el,options,args) }


  override def newItem(item: Item): OptionView = constructItemView(item){
    case (el,mp)=>new OptionView(el,item, mp).withBinder{new GeneralBinder(_)}
  }


}
