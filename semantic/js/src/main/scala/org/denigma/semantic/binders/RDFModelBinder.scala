package org.denigma.semantic.binders

import org.denigma.semantic.binders.binded.{Binded, BindedTextProperty}
import org.scalajs.dom
import org.scalajs.dom.raw.Element
import org.w3.banana._
import rx.Rx
import rx.core.Var

import scala.collection.immutable.Map
import scala.collection.mutable

class RDFModelBinder[Rdf<:RDF](graph:Var[PointedGraph[Rdf]],
                                resolver:Resolver[Rdf]) extends RDFBinder[Rdf](resolver) {
  import org.denigma.semantic.extensions._

  implicit def ops = resolver.ops //diry hack to make graph.updates work

  val updates: Rx[GraphUpdate[Rdf]] = graph.updates
  val binded:mutable.MultiMap[Rdf#URI,Binded[Rdf]] =
    new mutable.HashMap[Rdf#URI, mutable.Set[Binded[Rdf]]]
      with mutable.MultiMap[Rdf#URI,Binded[Rdf]] //NOTE: In the future I hope to get rid of these


  protected override def rdfPartial(el: Element, ats: Map[String, String]): PartialFunction[(String,String), Unit] = {
    this.vocabPartial.orElse(this.propertyPartial(el, ats))
  }

  /**
   * If it has "value" property"
   * @param el
   * @return
   */
  protected def elementHasValue(el:Element) =  el.tagName.toLowerCase match {
    case "input" | "textarea" | "option" =>true
    case _ =>false
  }


  protected def propertyPartial(el: Element,  ats: Map[String, String]):PartialFunction[(String,String),Unit] = {
    case ("property",value) =>  bindProperty(el,value,ats)

    case ("data-name-of",value) =>
      resolver.resolve(value).foreach {
        case iri =>
          dom.console.log("usage of data-name-of: it has not been well tested yet")
          binded.addBinding(iri,new BindedTextProperty(el,graph,updates,iri,"innerHTML"))
      }

    case (bname,value) if bname.startsWith("property-") =>
      val att: String = bname.replace("property-", "")
      resolver.resolve(value).foreach(  iri =>
        binded.addBinding(iri,new BindedTextProperty(el,graph,updates,iri,att))
      )
  }

  protected def bindValueElement(el: Element, iri:Rdf#URI, ats: Map[String, String]) = {
    val dataType = ats.get("datatype").fold("")(v => v)
    binded.addBinding(iri, new BindedTextProperty(el, graph, updates, iri, "value"))
  }

  protected def bindProperty(el: Element, value: String, ats: Map[String, String]): Unit = {
    resolver.resolve(value).foreach {
      case iri if this.elementHasValue(el) => bindValueElement(el,iri,ats)

      case iri =>
        binded.addBinding(iri, new BindedTextProperty(el, graph, updates, iri, "textContent"))
    }
  }
}