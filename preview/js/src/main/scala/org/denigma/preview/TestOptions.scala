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
case object TestOptions{
  val states:Vector[(String,String)] = {
    val mp = CSV.toDataFrame("preview/data/state_table.csv")
    mp.name.zip(mp.abbreviation)
  }

  lazy val data = states.zipWithIndex.map{case ((label,value),index)=>TextSelection(value,label)(index)}

  lazy val options: SortedSet[TextSelection] = SortedSet(data:_*)

  val items = options.take(3)

}

