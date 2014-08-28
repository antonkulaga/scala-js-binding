package org.denigma.binding.semantic

import org.denigma.binding.extensions._
import org.denigma.binding.views.OrganizedView
import org.scalajs.dom
import org.scalajs.dom.extensions._
import org.scalajs.dom.{Event, HTMLElement}
import org.scalax.semweb.rdf._
import org.scalax.semweb.shex.PropertyModel
import rx.core.Var

import scala.collection.immutable.Map
import scala.scalajs.js.Any



trait ModelView extends RDFView{

  self:OrganizedView=>


   val modelInside =  Var(ModelInside( PropertyModel.empty))


     /**
    * Binds element attributes
    * @param el
    */
   override def bindElement(el: HTMLElement) = {
       val ats: Map[String, String] = el.attributes.collect{
         case (key,attr) if key.contains("data-") && !key.contains("data-view") =>
           (key.replace("data-",""),attr.value.toString)
       }.toMap
       this.bindDataAttributes(el,ats)
       this.bindRdf(el)
   }


  /**
   * Returns partial function that binds to RDF
   * @param el html element to bind to
   * @param key Key
   * @param value Value
   * @param ats attributes
   * @return
   */
   protected override def rdfPartial(el: HTMLElement, key: String, value: String, ats:Map[String,String]): PartialFunction[String, Unit] = {

     case "vocab" if value.contains(":") => this.prefixes = prefixes + (":"-> IRI(value))
       //dom.alert("VOCAB=>"+prefixes.toString())

     case "prefix" if value.contains(":")=> this.prefixes = prefixes + (value.substring(0,value.indexOf(":"))-> IRI(value))

     case "property" =>
       this.resolve(value).foreach{
         case iri=> this.bindRDFProperty(el, iri, value, ats.get("datatype").fold("")(v=>v))
       }

     case "data-name-of" =>
       this.resolve(value).foreach{
         case iri=> this.bindRdfName(el, iri)
       }

     case bname if bname.startsWith("property-") =>
       val att = key.replace("property-", "")

       this.resolve(value).foreach(iri=> this.bindRdfAttribute(el, iri, att))




   }


  def prettyString(value:RDFValue) = value match {
    case lit:Lit=>
      lit.label
    case other=>other.stringValue
  }

  /**
   * Defines what to do with many rdf values
   */
  val onMany:Set[RDFValue]=>String = (values)=>{
    values.foldLeft("") { case (acc, prop) => acc + this.prettyString(prop) + "; "}
  }

  /**
   * Set of values to string
   * @param values
   * @return
   */
  def vals2String(values: Set[RDFValue],onZero:String="",onOne:RDFValue=>String = this.prettyString _, onMany:Set[RDFValue]=>String = this.onMany): String = values.size match {
    case 0 => onZero
    case 1 => onOne(values.head)
    case _ => this.onMany(values)
  }

  protected def printProp(prop:Set[RDFValue],delimiter:String="; ") = {
    prop.foldLeft("") { case (acc, prop) => acc + prop.label + delimiter}
  }

  protected val manyNames: Set[RDFValue]=>String = (values)=> printProp(values)

  protected def bindRdfName(el: HTMLElement, key: IRI) = this.bindRx(key.stringValue, el: HTMLElement, modelInside) { (el, model) =>
    model.current.properties.get(key).map {   case values =>     this.vals2String(values,onOne = (v)=>v.label,onMany = manyNames)  }
    match    {
    //TODO: move to propertymodel class
      case None=>
        dom.console.log(s"${key.toString()} was not found in the model with properties = ${model.current.properties.toString()}")
      case
        Some(value)=>el.innerHTML =  value
    }
  }


  protected def bindRdfProperty(el: HTMLElement, key: IRI)(assign:(HTMLElement,String)=>Unit) = this.bindRx(key.stringValue, el: HTMLElement, modelInside) { (el, model) =>
    model.current.properties.get(key).map(values=>this.vals2String(values)) match {
      case None=>
        dom.console.log(s"${key.toString()} was not found in the model with properties = ${model.current.properties.toString()}")
      case
        Some(value)=>assign(el,value)
    }
  }

  protected def bindRdfInner(el: HTMLElement, key: IRI) = bindRdfProperty(el,key)((el,value)=>el.innerHTML=value)
  protected def bindRdfText(el: HTMLElement, key: IRI) = bindRdfProperty(el,key)((el,value)=>el.textContent=value)
  protected def bindRdfInput(el: HTMLElement, key: IRI) = bindRdfProperty(el,key)((el,value)=>if (el.dyn.value != value) el.dyn.value = value)



//  protected def bindRdfInner(el: HTMLElement, key: IRI) = this.bindRx(key.stringValue, el: HTMLElement, modelInside) { (el, model) =>
//    model.current.properties.get(key).map(values=>this.vals2String(values)) match {
//      case None=>
//        dom.console.log(s"${key.toString()} was not found in the model with properties = ${model.current.properties.toString()}")
//      case
//        Some(value)=>el.innerHTML =  value
//    }
//  }
//
//   protected def bindRdfText(el: HTMLElement, key: IRI) = this.bindRx(key.stringValue, el: HTMLElement, modelInside) { (el, model) =>
//    model.current.properties.get(key).map(values=>this.vals2String(values)) match {
//       case None=>
//         dom.console.log(s"${key.toString()} was not found in the model of ${this.id} with model = ${model.current.resource}")
//       case Some(value)=>
//         el.textContent =  value
//     }
//   }
//
//
//   protected def bindRdfInput(el: HTMLElement, key: IRI) = this.bindRx(key.stringValue, el: HTMLElement, modelInside) { (el, model) =>
//      model.current.properties.get(key).map(values=>this.vals2String(values)) match {
//       case None=>  dom.console.log(s"${key.toString()} was not found in the model of ${this.id} with model = ${model.current.resource}")
//
//       case Some(value)=>     if (el.dyn.value != value) el.dyn.value = value
//     }
//
//   }

  /**
   * Changes checkbox whenever binded property changes
   * @param el html element of checkbox
   * @param key
   */
  protected def bindRdfCkeckBox(el: HTMLElement, key: IRI) = this.bindRx(key.stringValue, el: HTMLElement, modelInside) { (el, model) =>
    model.current.properties.get(key).map(_.head) match {
      case None =>         dom.console.log(s"${key.toString()} was not found in the model of ${this.id} with model = ${model.current.resource}")
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
   protected def bindRdfAttribute(el: HTMLElement, key: IRI, att: String) = this.bindRx(key.stringValue, el: HTMLElement, modelInside) { (el, model) =>
    model.current.properties.get(key).map(values=>this.vals2String(values)) match {
       case None=> dom.console.log(s"${key.toString()} was not found in the model")
       case Some(value)=>
         val at = dom.document.createAttribute(att)
         at.value = Any.fromString(value)
         el.attributes.setNamedItem(at)
         el.dyn.updateDynamic(att)(value)
     }

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
    * @param att binding attribute
    */
   def bindRDFProperty(el: HTMLElement, iri: IRI, att: String, datatype:String = "xsd:string") = datatype.toLowerCase() match {
     case tp if tp.contains("html")=>
       el.onkeyup = this.makeRdfHandler(el, iri, "innerHTML")
       this.bindRdfInner(el, iri)

     case _=> el.tagName.toLowerCase().toString match {
       case "input" | "textarea" | "option" =>

         el.attributes.get("type").map(_.value.toString) match {
           case Some("checkbox") =>
             this.bindRdfCkeckBox(el,iri)
             el.onclick = this.makeCheckboxHandler(el,iri)
           case _ =>
             //el.onkeyup
             el.onkeyup = this.makeRdfHandler(el, iri, "value")
             this.bindRdfInput(el, iri)
         }




       case other =>
         //        el.onkeyup = this.makePropHandler(el,str,"value")
         el.onkeyup = this.makeRdfHandler(el, iri, "textContent")
         this.bindRdfText(el, iri)

     }


   }

//   override def bindDataAttributes(el: HTMLElement, ats: Map[String, String]): Unit = {
//     /*nothing=)*/
//   }

  def die() = this.modelInside() = this.modelInside.now.apoptosis
 }
