package org.denigma.semantic.binders.shex

import org.denigma.binding.views.BindableView
import org.scalajs.dom.HTMLElement
import org.scalax.semweb.shex.ArcRule
import rx.core.Var

import scala.collection.immutable.Map


class OccursBinder(view:BindableView, arc:Var[ArcRule]) extends ArcBinder(view,arc){
  var occurs = Map.empty[HTMLElement,OccursSelector]


  override protected def rdfPartial(el: HTMLElement, key: String, value: String, ats:Map[String,String]): PartialFunction[String, Unit] = {
    this.vocabPartial(value).orElse(this.arcPartial(el:HTMLElement,value.toLowerCase))
  }


  override protected def arcPartial(el: HTMLElement,value:String): PartialFunction[String,Unit] =
  {
    case "data" if value=="occurs" =>

      this.bindVar("occurs", el: HTMLElement, this.arc) { (e,arc)=>
        val sel = this.occurs.getOrElse(el, {
          val s = new OccursSelector(el,arc)
          occurs = occurs + (el -> s)
          s
        })
        sel.fillValues(arc)

      }
  }



}


import org.scalajs.dom
import org.scalajs.dom.HTMLElement
import org.scalax.semweb.rdf.IRI
import org.scalax.semweb.shex.{ArcRule, _}
import rx.core.Var
import rx.ops._

import scala.scalajs.js



class OccursSelector(val el:HTMLElement,arc:Var[ArcRule]) extends ArcSelector(arc) {

  //val sel: js.Dynamic = jQuery(el).dyn.selectize(selectParams(el))

  protected val values = List(Star.obj,Plus.obj,ExactlyOne.obj)

  type Element = Cardinality

  type Value = String

  def valueIntoElement(value:String):Element =  value match {
    case ExactlyOne.obj.stringValue=>ExactlyOne
    case Plus.obj.stringValue=>Plus
    case Star.obj.stringValue=>Star
    case other => dom.console.error("unknown cardinality value ="+other)
      Star
  }

  def elementIntoValue(element:Element):String = element  match {
    case ExactlyOne=>ExactlyOne.obj.stringValue
    case Plus=>Plus.obj.stringValue
    case Star=>Star.obj.stringValue
    case other => dom.console.error("unknown cardinality value ="+other)
      Star.obj.stringValue
  }



  protected def selectParams(el: HTMLElement):js.Dynamic = {
    js.Dynamic.literal(
      onItemAdd = itemAddHandler _,
      onItemRemove =  itemRemoveHandler _,
      maxItems = 1,
      options = makeOptions(),
      value = elementIntoValue(arc.map(a=>a.occurs).now),
      valueField = "id",
      labelField = "title",
      searchField = "title"
    )
  }

  def makeOptions()=
  {
    val o =    List(Star.obj,Plus.obj,ExactlyOne.obj).map(i=> makeOption(i)).toList
    js.Array( o:_* )
  }





  override protected def itemRemoveHandler(value: String): Unit = {
    //nothing is needed

  }

  override protected def itemAddHandler(value: String, item: js.Dynamic): Unit = {

    val oc = valueIntoElement(value)
    if(oc!=arc.now.occurs){
      arc() = arc.now.copy(occurs = oc)
    }

  }

  def fillValues(arc:Var[ArcRule]):this.type = {
    val ss= this.selectizeFrom(el)
    ss.clear()
    val value = this.elementIntoValue(arc.now.occurs)
    //dom.console.info("value = "+value)
    ss.addItem(value)
    this

  }

}