package org.denigma.semantic.binders.shaped

import org.scalajs.dom
import org.scalajs.dom.HTMLElement
import org.scalax.semweb.rdf.{IRI, Res}
import org.scalax.semweb.shex._
import rx.Var
import rx.ops._

import scala.scalajs.js

class ValueSelector(val el:HTMLElement,arc:Var[ArcRule], typeHandler:(String)=>Unit) extends ArcSelector(arc){

  val key = ValueType.property

  type Value = Res

  type Element = ValueClass


  def valueIntoElement(value:String):Element =value match {
    case value if value.contains(":") => ValueType(IRI(value))
    case _ => dom.console.error("strange value for the name term") ; ???
  }

  //TODO: rewrite
  def elementIntoValue(element:Element):Value = element  match {
    case ValueType(res) => res
    case ValueStem(stem) => stem ;???
    case ValueAny(other) => ???
  }

  protected def selectParams(el: HTMLElement):js.Dynamic = {
    js.Dynamic.literal(
      onItemAdd = itemAddHandler _,
      onItemRemove =  itemRemoveHandler _,
      onType = typeHandler  ,
      maxItems = 1,
      value = this.elementIntoValue(arc.map(a=>a.value).now).stringValue,
      valueField = "id",
      labelField = "title",
      searchField = "title"
    )
  }

  protected def nameClass2Value(name:ValueClass) = name match {
    case ValueType(res)=>res.stringValue
    case ValueStem(stem)=> dom.console.error("name stem is not yet implemented"); ???
    case ValueAny(other)=> dom.console.error("name any is not yet implemented"); ???
  }





  override protected def itemRemoveHandler(value: String): Unit = {
    //nothing is needed

  }

  override protected def itemAddHandler(value: String, item: js.Dynamic): Unit = {
    val vt = valueIntoElement(value)
    //debug(s"VALUE = $value TERM + ${vt.toString}")
    if(arc.now.name!=vt) {
      arc() = arc.now.copy(value = vt)
    }



  }

  def fillValues(arc:Var[ArcRule]):this.type = {
    val ss= this.selectizeFrom(el)
    ss.clear()
    ss.clearOptions()
    val vc = arc.now.value
    val value= this.elementIntoValue(vc)
    //debug(name + " | "+value)
    //dom.console.info("value = "+value)
    ss.addOption(this.makeOption(value))
    ss.addItem(value.stringValue)
    this

  }

}
