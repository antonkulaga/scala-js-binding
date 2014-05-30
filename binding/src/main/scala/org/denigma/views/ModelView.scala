package org.denigma.views

import org.scalax.semweb.shex.{ArcRule, PropertyModel}
import org.scalax.semweb.rdf._
import javax.annotation.Resource
import org.scalax.semweb.rdf.IRI
import org.scalax.semweb.rdf.StringLiteral
import scala.Some
import org.scalax.semweb.rdf.StringLangLiteral
import rx.core.Var
import org.scalajs.dom.{Event, KeyboardEvent, MouseEvent, HTMLElement}
import org.denigma.models.ModelInside
import rx._
import scalatags.HtmlTag
import scala.collection.immutable.Map
import org.scalajs.dom
import scala.Some
import dom.extensions._

import org.denigma.extensions._
import scala.scalajs.js.Any

import org.denigma.extensions._
import org.scalajs.dom.extensions._




 class ModelView(name:String,element:HTMLElement,props:PropertyModel) extends OrganizedView(name,element) {


   val modelInside = Var(ModelInside(props))


     /**
    * Binds element attributes
    * @param el
    */
   override def bindElement(el: HTMLElement) = {
     super.bindElement(el)
     this.bindRdf(el)
   }


   protected def bindRdf(el: HTMLElement) = {
     val ats = el.attributes.collect { case (key, value) if !value.value.toString.contains("data") => (key, value.value.toString)}

     ats.foreach { case (key, value) =>
       this.rdfPartial(el, key, value).orElse(otherPartial)(key)


     }

   }

   protected def otherPartial: PartialFunction[String, Unit] = {
     case _ =>
   }

   protected def rdfPartial(el: HTMLElement, key: String, value: String): PartialFunction[String, Unit] = {
     case "property" =>

       val iri = IRI(value)
       this.bindRDFProperty(el, iri, value)

     case bname if bname.startsWith("property-") =>

       val att = key.replace("property-", "")
       this.bindRdfAttribute(el, IRI(value), att)
   }


   def strFromProperties(model: PropertyModel, key: IRI) = model.properties.get(key).fold("")(v => this.vals2String(v))


   def prettyString(value:RDFValue) = value match {
     case lit:Lit=>
       lit.label
     case other=>other.stringValue
   }

   /**
    * Set of values to string
    * @param values
    * @return
    */
   def vals2String(values: Set[RDFValue]) = values.size match {
     case 0 => ""
     case 1 => this.prettyString(values.head)
     case _ => values.foldLeft("") { case (acc, prop) => acc + ";" + this.prettyString(prop)}
   }

   protected def bindRdfText(el: HTMLElement, key: IRI) = this.bindRx(key.stringValue, el: HTMLElement, modelInside) { (el, model) =>
     el.textContent = Any.fromString(strFromProperties(model.current, key))
   }


   protected def bindRdfInput(el: HTMLElement, key: IRI) = this.bindRx(key.stringValue, el: HTMLElement, modelInside) { (el, model) =>
     val value = strFromProperties(model.current, key)
     if (el.dyn.value != value) el.dyn.value = value
   }

   /**
    * Assigns property to rdf value
    * @param el
    * @param key
    * @param att
    */
   protected def bindRdfAttribute(el: HTMLElement, key: IRI, att: String) = this.bindRx(key.stringValue, el: HTMLElement, modelInside) { (el, model) =>
     val value = strFromProperties(modelInside.now.current, key)
     val at = dom.document.createAttribute(att)
     at.value = value
     el.attributes.setNamedItem(at)
     el.dyn.updateDynamic(att)(value)
   }


   /**
    * Changes RDF property when value changes
    * @param el
    * @param iri
    * @param pname
    * @tparam T
    * @return
    */
   def makeRdfHandler[T <: Event](el: HTMLElement, iri: IRI, pname: String): (T) => Unit = (ev) =>
     el \ pname match {
       case Some(pvalue) =>
         modelInside.now.current.properties.get(iri).headOption match {
           case Some(value) if value == pvalue => //nothing
           case Some(value) => modelInside() = this.modelInside.now.replace(iri, pvalue.toString)
           case None => modelInside() = this.modelInside.now.add(iri, pvalue.toString)
         }

       case None => dom.console.error(s"no attributed for $pname")
     }


   /**
    * Binds property value to attribute
    * @param el Element
    * @param iri name of the binding key
    * @param att binding attribute
    */
   def bindRDFProperty(el: HTMLElement, iri: IRI, att: String) = el.tagName.toLowerCase().toString match {
     case "input" | "textarea" =>

       el.attributes.get("type").map(_.value.toString) match {
         case Some("checkbox") => //skip
         case _ =>
           //el.onkeyup
           el.onkeyup = this.makeRdfHandler(el, iri, "value")
           this.bindRdfInput(el, iri)
       }


     case other =>
       //        el.onkeyup = this.makePropHandler(el,str,"value")
       el.onkeyup = this.makeRdfHandler(el, iri, "value")
       this.bindRdfText(el, iri)

   }

   override protected def bindDataAttributes(el: HTMLElement, ats: Map[String, String]): Unit = {
     /*nothing=)*/
   }
 }
