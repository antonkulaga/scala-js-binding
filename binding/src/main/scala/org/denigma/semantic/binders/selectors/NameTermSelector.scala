package org.denigma.semantic.binders.selectors

import org.scalajs.dom.HTMLElement
import org.scalax.semweb.rdf.IRI
import org.scalax.semweb.shex._
import rx._
import org.denigma.binding.extensions._
import rx.ops._

import scala.scalajs.js

/**
 * Created by antonkulaga on 9/15/14.
 */
class NameTermSelector(val el:HTMLElement,arc:Var[ArcRule], typeHandler:(String)=>Unit) extends ArcSelector(arc) {

   override def key: IRI = rs / "occurs"

   type Element = NameClass



   def valueIntoElement(value:String):Element =  ???

   //TODO: rewrite
   def elementIntoValue(element:Element):String = element  match {
     case NameTerm(iri) => iri.stringValue
     case NameStem(stem) => stem.stringValue
     case NameAny(other) => other.toString()
   }

   protected def selectParams(el: HTMLElement):js.Dynamic = {
     js.Dynamic.literal(
       onItemAdd = itemAddHandler _,
       onItemRemove =  itemRemoveHandler _,
       maxItems = 1,
       value = this.elementIntoValue(arc.map(a=>a.name).now),
       valueField = "id",
       labelField = "title",
       searchField = "title"
     )
   }

   def nameClass2Value(name:NameClass) = name match {
     case NameTerm(iri)=>iri.stringValue
     case NameStem(stem)=> this.error("name stem is not yet implemented"); ???
     case NameAny(other)=> this.error("name any is not yet implemented"); ???
   }

   def value2NameClass(value:String) = value match {
     case value if value.contains(":") => NameTerm(IRI(value))
     case _ => this.error("strange value for the name term") ; ???
   }





   override protected def itemRemoveHandler(value: String): Unit = {
     //nothing is needed

   }

   override protected def itemAddHandler(value: String, item: js.Any): Unit = {
     val nt = this.value2NameClass(value)
     if(arc.now.name!=nt) {
       arc() = arc.now.copy(name = nt)
     }



   }

   def fillValues(arc:Var[ArcRule]):this.type = {
     val ss= this.selectizeFrom(el)
     ss.clear()
     val value = this.elementIntoValue(arc.now.name)
     //dom.console.info("value = "+value)
     ss.addItem(value)
     this

   }


 }
