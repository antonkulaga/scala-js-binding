package org.denigma.semantic.binders.shaped.selectors

import org.scalajs.dom.HTMLElement
import org.scalax.semweb.rdf.IRI
import org.scalax.semweb.shex._
import rx._
import rx.ops._

import scala.scalajs.js

/**
 * NameTerm selector for the property
 * @param el
 * @param arc
 * @param typeHandler
 */
class NameTermSelector(val el:HTMLElement,arc:Var[ArcRule], typeHandler:(String)=>Unit) extends ArcSelector(arc) {

   override def key: IRI = rs / "property"

   type Value = IRI

   type Element = NameClass



   def valueIntoElement(value:String):Element = this.value2NameClass(value)

   //TODO: rewrite
   def elementIntoValue(element:Element):IRI = element  match {
     case NameTerm(iri) => iri
     case NameStem(stem) => stem ;???
     case NameAny(other) => ???
   }

   protected def selectParams(el: HTMLElement):js.Dynamic = {
     js.Dynamic.literal(
       onItemAdd = itemAddHandler _,
       onItemRemove =  itemRemoveHandler _,
       onType = typeHandler  ,
       maxItems = 1,
       value = this.elementIntoValue(arc.map(a=>a.name).now).stringValue,
       valueField = "id",
       labelField = "title",
       searchField = "title"
     )
   }

   protected def nameClass2Value(name:NameClass) = name match {
     case NameTerm(iri)=>iri.stringValue
     case NameStem(stem)=> this.error("name stem is not yet implemented"); ???
     case NameAny(other)=> this.error("name any is not yet implemented"); ???
   }

   protected def value2NameClass(value:String) = value match {
     case value if value.contains(":") => NameTerm(IRI(value))
     case _ => this.error("strange value for the name term") ; ???
   }





   override protected def itemRemoveHandler(value: String): Unit = {
     //nothing is needed

   }

   override protected def itemAddHandler(value: String, item: js.Any): Unit = {
     val nt = this.value2NameClass(value)
     debug(s"VALUE = $value TERM + ${nt.toString}")
     if(arc.now.name!=nt) {
       arc() = arc.now.copy(name = nt)
     }



   }

   def fillValues(arc:Var[ArcRule]):this.type = {
     val ss= this.selectizeFrom(el)
     ss.clear()
     ss.clearOptions()
     val name = arc.now.name
     val value= this.elementIntoValue(name)
     //debug(name + " | "+value)
     //dom.console.info("value = "+value)
     ss.addOption(this.makeOption(value))
     ss.addItem(value.stringValue)
     this

   }


 }
