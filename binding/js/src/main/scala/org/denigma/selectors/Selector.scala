package org.denigma.selectors

import org.denigma.binding.commons.ILogged
import org.denigma.binding.extensions._
import org.denigma.binding.views.BindableView
import org.denigma.selectize.Selectize
import org.scalajs.dom
import org.scalajs.dom.raw.HTMLElement
import rx.core.Var

import scala.collection.immutable.Map
import scala.scalajs.js
import scala.scalajs.js.annotation.JSExportAll


object SelectOption
{
  implicit def convert(op:SelectOption): js.Dynamic =op.asInstanceOf[js.Dynamic]
}


@JSExportAll
case class SelectOption(id:String,title:String)


@JSExportAll
trait SelectRenderer{
  val item: js.Function1[SelectOption,String]
  val option: js.Function1[SelectOption,String]
  //val option_create: js.Function1[InputHolder,  String]

}

class InputHolder extends js.Object{
  val input:String = ???
}



trait Escaper {
  protected val replacements = ("\"", "&#34;") ::("<", "&lt;") ::(">", "&gt;") ::("'", "&#39;") :: Nil

  def escape(str: String) = replacements.foldLeft(str) {case (acc, (from,to))=>acc.replace(from,to) }

  def unescape(str: String) = replacements.foldLeft(str) {case (acc, (from,to))=>acc.replace(to,from) }

}


/**
 * Basic class for all selectors, contains some basic handlers and helper methods
 */
trait Selector extends ILogged {

  protected def makeOption(vid:String,title:String): SelectOption

  protected def itemAddHandler(value:String, item:js.Dynamic): Unit
  protected def itemRemoveHandler(value:String): Unit


  protected def selectParams(el: HTMLElement):js.Dynamic

  protected def selectizeFrom(el:HTMLElement): Selectize = {
    val s = el.dyn.selectize
    s.asInstanceOf[Selectize]
  }

  def selectizeOption(el:HTMLElement):Option[Selectize] =   el.dyn.selectize match {
    case s if s==null | js.isUndefined(s)=>None
    case s=>Some(s.asInstanceOf[Selectize])
  }

}

trait GeneralSelectBinder
{
  type Element
  type View<:BindableView
  type Selector

  val view:View
  val model:Var[Element]
  var selectors = Map.empty[HTMLElement,Selector]
}