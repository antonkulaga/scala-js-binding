package org.denigma.semantic.binders

import org.denigma.binding.extensions._
import org.denigma.binding.views.BindableView
import org.denigma.semantic.rdf.{ModelInside, PropertyPrinter}
import org.scalajs.dom
import org.scalajs.dom.raw.{Event, HTMLElement}
import org.denigma.semweb.rdf._
import rx.Rx
import rx.core.Var
import org.scalajs.dom.ext._
import scala.collection.immutable.Map
import scala.scalajs.js.{Dynamic, Any}
import rx.extensions._
import rx.ops._

class ModelBinder(view:BindableView,modelInside:Var[ModelInside],subjectOf:Set[IRI] = Set.empty[IRI]) extends RDFBinder(view) with PropertyPrinter
{

  /**
   * Returns partial function that binds to RDF
   * @param el html element to bind to
   * @param key Key
   * @param value Value
   * @param ats attributes
   * @return
   */
  protected override def rdfPartial(el: HTMLElement, key: String, value: String, ats:Map[String,String]): PartialFunction[String, Unit] =
  {
   // debug("properties on start: "+model.now.current.properties.toString())

    this.vocabPartial(value).orElse( this.propertyPartial(el,key,value,ats))
  }

  protected def propertyPartial(el: HTMLElement, key: String, value: String, ats:Map[String,String]): PartialFunction[String, Unit]  = {
    case "property" =>
      this.resolve(value).foreach{
        case iri=>
          val dataType = ats.get("datatype").fold("")(v=>v)
          this.bindRDFProperty(el, iri,  dataType)
      }

    case "data-name-of" =>
      this.resolve(value).foreach{
        case iri=> this.bindRdfName(el, iri)
      }

    case bname if bname.startsWith("property-") =>
      val att = key.replace("property-", "")

      this.resolve(value).foreach(iri=> this.bindRdfAttribute(el, iri, att))

  }


  /**
   * Gets properties out of model
   * @param key
   * @return
   */
  def properties(key:IRI): Option[Set[RDFValue]] = this.modelInside.now.current.properties.get(key)

  def values(key:IRI) = this.properties(key).map {   case values =>     this.vals2String(values,onOne = (v)=>v.label,onMany = manyNames)  }

  protected def keyWarning(key:IRI)=  {
    dom.console.info(s"${key.toString()} was not found in the model with properties = ${modelInside.now.current.properties.toString()}")
  }


  protected def bindRdfName(el: HTMLElement, key: IRI) = this.bindRx(key.stringValue, el: HTMLElement, modelInside) {
    (el, model) =>
      this.properties(key)
        .map{case values=>vals2String(values,onOne = (v)=>v.label,onMany = manyNames)  }
      match
      { case None=> this.keyWarning(key)
        case   Some(value)=>el.innerHTML =  value  }
  }


  //TODO: REHANE!
  protected def bindRdfElement(el: HTMLElement, key: IRI)(assign:(HTMLElement,String)=>Unit) = this.bindRx(key.stringValue, el: HTMLElement, modelInside) { (el, model) =>
    this.values(key)   match {
      case None => this.keyWarning (key)
      case Some (value) => assign (el, value)
    }
  }

  protected def bindRdfInner(el: HTMLElement, key: IRI) = bindRdfElement(el,key)((el,value)=>el.innerHTML=value)
  protected def bindRdfText(el: HTMLElement, key: IRI) = bindRdfElement(el,key)((el,value)=>el.textContent=value)
  protected def bindRdfInput(el: HTMLElement, key: IRI): Unit = bindRdfElement(el,key)((el,value)=>if (el.dyn.value != value) el.dyn.value = value)


  /**
   * Changes checkbox whenever binded property changes
   * @param el html element of checkbox
   * @param key
   */
  protected def bindRdfCheckBox(el: HTMLElement, key: IRI) = this.bindRx(key.stringValue, el: HTMLElement, modelInside) { (el, mod) =>
    this.properties(key).map(_.head) match {
      case None =>         dom.console.log(s"${key.toString()} was not found in the model of ${this.id} with model = ${mod.current.resource}")
      case Some(value:BooleanLiteral)=> el.dyn.checked match {
        case v if v.isInstanceOf[Boolean]=>
          val b = v.asInstanceOf[Boolean]
          if(b!=value.value) el.dyn.checked = value.value
        case _=>dom.console.log(s"unknown checked value for ${key.toString}")
      }

      case Some(value)=>dom.console.log(s"${key.toString()} is not a boolean, it is = ${value.getClass.getName}")
    }
  }



  /**
   * Assigns property to rdf value
   * @param el
   * @param key
   * @param att
   */
  protected def bindRdfAttribute(el: HTMLElement, key: IRI, att: String) = this.bindRdfElement(el,key)
  {
    case (elem,value)=>
          val at = dom.document.createAttribute(att)
          at.value = value
          elem.attributes.setNamedItem(at)
          elem.dyn.updateDynamic(att)(value)
  }

/*
  protected def updateModel(prop:IRI,pvalue:String) = {
    this.properties(prop).headOption match {
      case Some(value) if value == pvalue => //nothing
      case Some(value) if subjectOf.contains(prop) && pvalue.contains(":")   =>
        val curr = model.now.current
        val changed = curr.copy(resource = IRI(pvalue))
         model() = this.model.now.replace(prop, pvalue.toString)

      case Some(value) =>
          model() = this.model.now.replace(prop, pvalue.toString)

      case None =>
        model() = this.model.now.add(prop, pvalue.toString)
    }
  }
*/

  /**
   * Changes RDF property when value changes
   * @param el element
   * @param iri IRI
   * @param pname name of
   * @tparam T
   * @return
   */
  def makeRdfHandler[T <: Event](el: HTMLElement, iri: IRI, pname: String): (T) => Unit =
    (event) =>
      el \ pname match
      {
        case Some(pvalue) =>
          this.properties(iri).headOption match {
            case Some(value) if value == pvalue => //nothing
            case Some(value) =>
              modelInside() = this.modelInside.now.replace(iri, pvalue.toString)
            case None =>
              modelInside() = this.modelInside.now.add(iri, pvalue.toString)
          }

        case None => dom.console.error(s"no attributed for $pname")
      }

  def makeCheckboxHandler[T <: Event](el: HTMLElement, iri: IRI): (T) => Unit = (ev) =>{
    el \ "checked" match {
      case Some(value)=> value match {
        case v if value.isInstanceOf[Boolean]=>
          val b = v.asInstanceOf[Boolean]
          el.dyn.checked = b
          modelInside() = modelInside.now.replace(iri,BooleanLiteral(b))
        case _=>dom.console.log("checked is not boolean")
      }
      case None=> dom.console.log(s" ${iri.stringValue} not a checkbox")

    }

  }

  def bindPropertyName(el: HTMLElement, iri: IRI) = {
    el.onkeyup = this.makeRdfHandler(el, iri, "innerHTML")
    this.bindRdfInner(el, iri)
  }

  /**
   * Binds property value to attribute
   * @param el Element
   * @param iri name of the binding key
   */
  def bindRDFProperty(el: HTMLElement, iri: IRI, datatype:String = "xsd:string") = datatype.toLowerCase match {
    case tp if tp.contains("html")=>
      el.onkeyup = this.makeRdfHandler(el, iri, "innerHTML")
      this.bindRdfInner(el, iri)

    case _=>
      //it it is html element with "Value" property like input or textarea
      if(elementHasValue(el)) el.attributes.get("type").map(_.value.toString) match
      {
        case Some("checkbox") =>
          this.bindRdfCheckBox(el,iri)
          el.onclick = this.makeCheckboxHandler(el,iri)
        case _ =>
          //el.onkeyup
          el.onkeyup = this.makeRdfHandler(el, iri, "value")
          this.bindRdfInput(el, iri)
      }
     else
      {
        //        el.onkeyup = this.makePropHandler(el,str,"value")
        el.onkeyup = this.makeRdfHandler(el, iri, "textContent")
        this.bindRdfText(el, iri)
      }
  }

}
