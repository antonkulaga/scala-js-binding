package org.denigma.binding.semantic

import org.denigma.binding.extensions._
import org.denigma.binding.views.{BindingView, OrganizedView}
import org.scalajs.dom
import org.scalajs.dom.extensions._
import org.scalajs.dom.{Event, HTMLElement}
import org.scalax.semweb.rdf._
import org.scalax.semweb.shex.PropertyModel
import rx.core.Var

import scala.collection.immutable.Map
import scala.scalajs.js.Any

trait RDFView extends OrganizedView
{
  /**
   * Default vocabulary is ""
   */
  var prefixes = Map.empty[String,IRI]


  implicit val context = IRI("http://"+dom.location.hostname)

  /**
   * Resolvers IRI from property map
   * @param property
   * @return
   */
  def resolve(property:String):Option[IRI] =  property.indexOf(":") match {
    case -1 =>
      //dom.alert(prefixes.toString())
      prefixes.get(":").orElse(prefixes.get("")).map(p=>p / property)
    case 0 =>
      //dom.alert("0"+prefixes.toString())
      prefixes.get(":").orElse(prefixes.get("")).map(p=>p / property)
    case ind=>
      val key = property.substring(0,ind)

      prefixes.get(key).map(p=>p / property).orElse(Some(IRI(property)))

  }


  type RDFType = OrganizedView with RDFView

  protected def nearestRDFParent(implicit current:BindingView = this):Option[RDFType] = current.parent match {
    case Some(par:RDFType)=>Some(par)
    case Some(par)=>this.nearestRDFParent(par)
    case _=> None

  }


  protected def bindRdf(el: HTMLElement) = {


    val rp = nearestRDFParent
    prefixes= rp.fold(Map.empty[String,IRI])(_.prefixes)++this.prefixes
    def binded(str:String) = str.contains("data") && str.contains("bind")

    val ats = el.attributes.collect { case (key, value) if !binded(value.value) => (key, value.value.toString)}.toMap

    ats.foreach { case (key, value) =>
      this.rdfPartial(el, key, value,ats).orElse(otherPartial)(key)

    }



  }



  protected def rdfPartial(el: HTMLElement, key: String, value: String, ats:Map[String,String]): PartialFunction[String, Unit] = {

    case "vocab" if value.contains(":") => this.prefixes = prefixes + (":"-> IRI(value))
    //dom.alert("VOCAB=>"+prefixes.toString())

    case "prefix" if value.contains(":")=> this.prefixes = prefixes + (value.substring(0,value.indexOf(":"))-> IRI(value))

  }

  protected def otherPartial: PartialFunction[String, Unit]
}

trait ModelView extends RDFView{

  self:OrganizedView=>


   val modelInside = Var(ModelInside( PropertyModel.empty))


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




   protected override def rdfPartial(el: HTMLElement, key: String, value: String, ats:Map[String,String]): PartialFunction[String, Unit] = {

     case "vocab" if value.contains(":") => this.prefixes = prefixes + (":"-> IRI(value))
       //dom.alert("VOCAB=>"+prefixes.toString())

     case "prefix" if value.contains(":")=> this.prefixes = prefixes + (value.substring(0,value.indexOf(":"))-> IRI(value))

     case "property" =>
       this.resolve(value).foreach{
         case iri=> this.bindRDFProperty(el, iri, value, ats.get("datatype").fold("")(v=>v))
       }

     case bname if bname.startsWith("property-") =>
       val att = key.replace("property-", "")

       this.resolve(value).foreach(iri=> this.bindRdfAttribute(el, iri, att))




   }



  /**
   * Extracts STRs from properties
   * @param model
   * @param key
   * @return
   */
   def strOptionFromProperties(model: PropertyModel, key: IRI) = model.properties.get(key) match {
    case Some(values: Set[RDFValue])=> Some(this.vals2String(values))
    case None=>None

    }


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
   def vals2String(values: Set[RDFValue]): String = values.size match {
     case 0 => ""
     case 1 => this.prettyString(values.head)
     case _ => values.foldLeft("") { case (acc, prop) => acc + ";" + this.prettyString(prop)}
   }


  protected def bindRdfInner(el: HTMLElement, key: IRI) = this.bindRx(key.stringValue, el: HTMLElement, modelInside) { (el, model) =>
    strOptionFromProperties(model.current, key) match {
      case None=>
        dom.console.log(s"${key.toString()} was not found in the model")
      case
        Some(value)=>el.innerHTML =  value
    }
  }

   protected def bindRdfText(el: HTMLElement, key: IRI) = this.bindRx(key.stringValue, el: HTMLElement, modelInside) { (el, model) =>
     strOptionFromProperties(model.current, key) match {
       case None=>
         dom.console.log(s"${key.toString()} was not found in the model")
       case Some(value)=>
         el.textContent =  value
     }
   }


   protected def bindRdfInput(el: HTMLElement, key: IRI) = this.bindRx(key.stringValue, el: HTMLElement, modelInside) { (el, model) =>
     strOptionFromProperties(model.current, key) match {
       case None=>dom.console.log(s"${key.toString()} was not found in the model")
       case Some(value)=>     if (el.dyn.value != value) el.dyn.value = value
     }

   }

  /**
   * Changes checkbox whenever binded property changes
   * @param el html element of checkbox
   * @param key
   */
  protected def bindRdfCkeckBox(el: HTMLElement, key: IRI) = this.bindRx(key.stringValue, el: HTMLElement, modelInside) { (el, model) =>
    model.current.properties.get(key).map(_.head) match {
      case None =>dom.console.log(s"${key.toString()} was not found in the model")
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
     strOptionFromProperties(modelInside.now.current, key) match {
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
