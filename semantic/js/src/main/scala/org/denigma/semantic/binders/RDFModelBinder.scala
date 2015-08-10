package org.denigma.semantic.binders

import org.denigma.binding.views.BindableView
import org.denigma.semantic.binders.binded.{Binded, BindedTextProperty}
import org.scalajs.dom
import org.scalajs.dom.raw.HTMLElement
import org.w3.banana._
import rx.Rx
import rx.core.Var

import scala.collection.immutable.Map
import scala.collection.mutable

class RDFModelBinder[Rdf<:RDF](
                                view:BindableView,
                                graph:Var[PointedGraph[Rdf]],
                                prefixes:Var[Map[String,Rdf#URI]])
                              (implicit operations:RDFOps[Rdf]) extends RDFBinder[Rdf](view,prefixes)(operations) {
  import org.denigma.semantic.extensions._


  val updates: Rx[GraphUpdate[Rdf]] = graph.updates
  val binded:mutable.MultiMap[Rdf#URI,Binded[Rdf]] =
    new mutable.HashMap[Rdf#URI, mutable.Set[Binded[Rdf]]]
      with mutable.MultiMap[Rdf#URI,Binded[Rdf]] //NOTE: In the future I hope to getrid of these


  /**
   * Returns partial function that binds to RDF
   * @param el html element to bind to
   * @param key Key
   * @param value Value
   * @param ats attributes
   * @return
   */
  protected override def rdfPartial(el: HTMLElement, key: String, value: String, ats: Map[String, String]): PartialFunction[String, Unit] = {
    this.vocabPartial(value).orElse(this.propertyPartial(el, key, value, ats))
  }

  /**
   * If it has "value" property"
   * @param el
   * @return
   */
  protected def elementHasValue(el:HTMLElement) =  el.tagName.toLowerCase match {
    case "input" | "textarea" | "option" =>true
    case _ =>false
  }


  protected def propertyPartial(el: HTMLElement, key: String, value: String, ats: Map[String, String]): PartialFunction[String, Unit] = {
    case "property" =>
      bindProperty(el,key,value,ats)

    case "data-name-of" =>
      this.resolve(value).foreach {
        case iri =>
          dom.console.log("usage of data-name-of: it has not been well tested yet")
          binded.addBinding(iri,new BindedTextProperty(el,graph,updates,iri,"innerHTML"))
      }

    case bname if bname.startsWith("property-") =>
      val att: String = key.replace("property-", "")
      this.resolve(value).foreach(  iri =>
        binded.addBinding(iri,new BindedTextProperty(el,graph,updates,iri,att))
      )
  }

  protected def bindProperty(el: HTMLElement, key: String, value: String, ats: Map[String, String]): Unit = {
    this.resolve(value).foreach {
      case iri if this.elementHasValue(el) =>
        val dataType = ats.get("datatype").fold("")(v => v)
        binded.addBinding(iri, new BindedTextProperty(el, graph, updates, iri, "value"))

      case iri =>
        binded.addBinding(iri, new BindedTextProperty(el, graph, updates, iri, "textContent"))
    }
  }
}
/*
    protected def propertyPartial(el: HTMLElement, key: String, value: String, ats: Map[String, String]): PartialFunction[String, Unit] = {
      case "property" =>
        this.resolve(value).foreach {
          case iri =>
            val dataType = ats.get("datatype").fold("")(v => v)

            this.bindRDFProperty(el, iri, dataType)
        }

      case "data-name-of" =>
        this.resolve(value).foreach {
          case iri => this.bindRdfName(el, iri)
        }

      case bname if bname.startsWith("property-") =>
        val att = key.replace("property-", "")

        this.resolve(value).foreach(iri => this.bindRdfAttribute(el, iri, att))
  }





  /**
   * Changes checkbox whenever binded property changes
   * @param el html element of checkbox
   * @param predicate
   */
  protected def bindRdfCheckBox(el: HTMLElement, predicate: Rdf#URI) =
    this.bindRx(ops.fromUri(predicate), el: HTMLElement, graph) { (el, g) =>
      triplets(predicate).toSeq.headOption match {
        case None => dom.console.log(s"${ops.fromUri(predicate)} was not found in the model of ${this.id} with model = ${g.pointer}")
        case Some(value) => value.objectt.fold(
          {  case uri=>  dom.console.error(s"$uri does not fit, because checkbox must be boolean") },
          {  case node=>  dom.console.error(s"$node does not fit, because checkbox must be boolean") },
          {
            case lit@Literal(something, ops.xsd.boolean,lang) => something.toLowerCase match { //TODO: check the case to avoid lower-casing
              case "true" | "1" =>
                if(!el.dyn.checked.asInstanceOf[Boolean]) el.dyn.checked = true
              case "false" | "0" =>
                if(el.dyn.checked.asInstanceOf[Boolean]) el.dyn.checked = false

            }
            case other => dom.console.error(s"$other does not fit, because checkbox must be boolean")      }
          )
        case other=>dom.console.log(s"unsupported checked $other value for ${predicate}")
      }
    }


  /**
   * Assigns property to rdf value
   * @param el
   * @param key
   * @param att
   */
  protected def bindRdfAttribute(el: HTMLElement, key: Rdf#URI, att: String) = this.bindRdfElement(el,key)
  {
    case (elem,value)=>
      val at = dom.document.createAttribute(att)
      at.value = value
      elem.attributes.setNamedItem(at)
      elem.dyn.updateDynamic(att)(value)
  }



  def makeCheckboxHandler[T <: Event](el: HTMLElement, predicate: Rdf#URI): (T) => Unit = (ev) =>
    el \ "checked" match {
      case Some(value)  if value.isInstanceOf[Boolean]=>
          val b = value.asInstanceOf[Boolean]
          el.dyn.checked = b
          update(predicate,b.toString)
      case Some(other)=> dom.console.log(s"checked $other is not boolean")
      case None=> dom.console.log(s" ${ops.fromUri(predicate)} not a checkbox")

    }

  /**
   * Binds property value to attribute
   * @param el Element
   * @param iri name of the binding key
   */
  def bindRDFProperty(el: HTMLElement, iri: Rdf#URI, datatype:String = "xsd:string") = datatype.toLowerCase match {
    case tp if tp.contains("html")=>
      propertyBinder.addChangeHandler(el,"innerHTML"){(el,ev,oldValue,newValue)=>
        println(s"${oldValue} ${newValue}")
      }
      //el.onkeyup = this.onPropertyChange(el, iri, "innerHTML")
      //this.bindRdfInner(el, iri)

    case other if elementHasValue(el)=> //if textarea or input
      el.attributes.get("type").map(_.value.toString) match
      {
        case Some("checkbox") =>
          //this.bindRdfCheckBox(el,iri)
          //el.onclick = this.makeCheckboxHandler(el,iri)

        case _ =>
          //el.onkeyup

          //el.onkeyup
          propertyBinder..addChangeHandler(el,"value"){(el,ev,oldValue,newValue)=>
            println(s"VALUE CHANGE = ${oldValue} ${newValue}")
          }
          //el.onkeyup = this.onPropertyChange(el, iri, "value")
          propertyBinder..bindRdfInput(el, iri)
      }

    case _=>
          //        el.onkeyup = this.makePropHandler(el,str,"value")
          propertyBinder.addChangeHandler(el,"value"){(el,ev,oldValue,newValue)=>
            println(s"TEXT CHANGE = ${oldValue} ${newValue}")
          }
          //el.onkeyup = this.onPropertyChange(el, iri, "textContent")
          //this.bindRdfText(el, iri)

  }

}*/
