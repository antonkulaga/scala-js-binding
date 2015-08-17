package org.denigma.semantic.binders

import org.denigma.binding.views.BindableView
import org.denigma.semantic.binders.binded.{Binded, BindedTextProperty}
import org.scalajs.dom
import org.scalajs.dom.raw.HTMLElement
import org.w3.banana._
import rx.Rx
import rx.core.Var
import rx.ops._
import org.denigma.binding.extensions._
import scala.collection.immutable.Map
import scala.collection.mutable

class RDFModelBinder[Rdf<:RDF](
                                view:BindableView,
                                graph:Var[PointedGraph[Rdf]],
                                resolver:Resolver[Rdf]) extends RDFBinder[Rdf](view,resolver) {
  import org.denigma.semantic.extensions._

  implicit def ops = resolver.ops //diry hack to make graph.updates work

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
      resolver.resolve(value).foreach {
        case iri =>
          dom.console.log("usage of data-name-of: it has not been well tested yet")
          binded.addBinding(iri,new BindedTextProperty(el,graph,updates,iri,"innerHTML"))
      }

    case bname if bname.startsWith("property-") =>
      val att: String = key.replace("property-", "")
      resolver.resolve(value).foreach(  iri =>
        binded.addBinding(iri,new BindedTextProperty(el,graph,updates,iri,att))
      )
  }

  protected def bindValueElement(el: HTMLElement, iri:Rdf#URI, ats: Map[String, String]) = {
    val dataType = ats.get("datatype").fold("")(v => v)
    binded.addBinding(iri, new BindedTextProperty(el, graph, updates, iri, "value"))
  }

  protected def bindProperty(el: HTMLElement, key: String, value: String, ats: Map[String, String]): Unit = {
    resolver.resolve(value).foreach {
      case iri if this.elementHasValue(el) => bindValueElement(el,iri,ats)

      case iri =>
        binded.addBinding(iri, new BindedTextProperty(el, graph, updates, iri, "textContent"))
    }
  }
}