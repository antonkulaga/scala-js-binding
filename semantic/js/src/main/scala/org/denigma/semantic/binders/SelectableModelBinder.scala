package org.denigma.semantic.binders


import org.denigma.binding.views.BindableView
import org.denigma.semantic.binders.binded.{BindedTextProperty, SemanticSelector, Typed}
import org.scalajs.dom.raw.HTMLElement
import org.w3.banana._
import rx.core.Var
import rx.extensions._

import scala.collection.immutable.Map
import scala.concurrent.Future

class SelectableModelBinder[Rdf<:RDF](
                                view:BindableView,
                                graph:Var[PointedGraph[Rdf]],
                                prefixes:Var[Map[String,Rdf#URI]])
                                (typeHandler:Typed[Rdf]=>Future[Seq[Rdf#Node]])
                              (implicit ops:RDFOps[Rdf]) extends RDFModelBinder[Rdf](view,graph,prefixes)(ops) {

  override protected def bindProperty(el: HTMLElement, key: String, value: String, ats: Map[String, String]): Unit = {
    this.resolve(value).foreach {
      case iri if this.elementHasValue(el) =>
        val dataType = ats.get("datatype").fold("")(v => v)
        val selector= new SemanticSelector[Rdf](el,graph,updates,iri)
        selector.typed.onChange(ops.fromUri(iri)){ tp=>
          typeHandler(tp)
        }
        binded.addBinding(iri, selector)

      case iri =>
        binded.addBinding(iri, new BindedTextProperty(el, graph, updates, iri, "textContent"))
    }
  }

}
