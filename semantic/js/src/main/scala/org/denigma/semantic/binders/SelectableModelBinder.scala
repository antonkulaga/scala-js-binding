package org.denigma.semantic.binders


import org.denigma.binding.views.BindableView
import org.denigma.semantic.binders.binded.{SemanticSelector, BindedTextProperty, Typed}
import org.scalajs.dom.raw.HTMLElement
import org.w3.banana._
import rx.core.Var
import rx.extensions._
import scalajs.concurrent.JSExecutionContext.Implicits.queue

import scala.collection.immutable.Map
import scala.concurrent.Future

class SelectableModelBinder[Rdf<:RDF](
                                view:BindableView,
                                graph:Var[PointedGraph[Rdf]],
                                resolver:Resolver[Rdf])
                                (typeHandler:Typed[Rdf]=>Future[Seq[Rdf#Node]])
                               extends RDFModelBinder[Rdf](view,graph,resolver) {

  import resolver.ops


  override protected def bindValueElement(el: HTMLElement, iri:Rdf#URI, ats: Map[String, String]) = el.tagName.toLowerCase match {
    case "textarea"=>
      super.bindValueElement(el,iri,ats) //if it is a text area, then no selection is possible
    case other=>
      //val dataType = ats.get("datatype").fold("")(v => v)
      val selector= new SemanticSelector[Rdf](el,graph,updates,iri)
      selector.typed.onChange(ops.fromUri(iri),uniqueValue = true){ tp=>
        typeHandler(tp).foreach{case sugs=>
          selector.suggestions() = sugs.toSet //TODO:rewrite with ranking!
        }
      }
      binded.addBinding(iri, selector)

  }

}
