package org.denigma.semantic.binders.selectors

import org.scalajs.dom
import org.scalajs.dom.HTMLElement
import org.scalax.semweb.rdf.IRI
import org.scalax.semweb.shex.{ArcRule, _}
import rx.core.Var
import rx.ops._

import scala.scalajs.js



class OccursSelector(val el:HTMLElement,arc:Var[ArcRule]) extends ArcSelector(arc) {

  //val sel: js.Dynamic = jQuery(el).dyn.selectize(selectParams(el))
  override def key: IRI = rs / "occurs"

  protected val values = List(Star.obj,Plus.obj,ExactlyOne.obj)

  type Element = Cardinality

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

  def makeOptions():js.Array[js.Dynamic] =
  {
    val o: List[js.Dynamic] =    List(Star.obj,Plus.obj,ExactlyOne.obj).map(i=> makeOption(i)).toList
    js.Array( o:_* )
  }





  override protected def itemRemoveHandler(value: String): Unit = {
    //nothing is needed

  }

  override protected def itemAddHandler(value: String, item: js.Any): Unit = {

    val oc = valueIntoElement(value)
    if(oc!=arc.now.occurs){
      arc() = arc.now.copy(occurs = oc)
    }

  }

  def fillValues(arc:Var[ArcRule]):this.type = {
    val ss= this.selectizeFrom(el)
    ss.clear()
    val value = this.elementIntoValue(arc.now.occurs)
    dom.console.info("value = "+value)
    ss.addItem(value)
    this

  }

}