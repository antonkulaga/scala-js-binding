package org.denigma.semantic.models

import org.denigma.binding.views.BindableView
import org.denigma.semantic.WebPlatform
import org.scalajs.dom
import org.scalajs.dom.raw.HTMLElement
import org.w3.banana.{PointedGraph, RDF, RDFOps}
import rx.core.Var

abstract class ModelView[Rdf<:RDF](val elem:HTMLElement,val params:Map[String,Any])(implicit ops:RDFOps[Rdf]) extends BindableView
{
  import ops._


  val subject = this.resolveKeyOption("resource"){
    case str:String=> ops.makeUri(str)
    case uri:Rdf#URI=>uri //todo fix erasure problem
  }.getOrElse{
    dom.console.error("no resource for the view found, random subject is created")
    WebPlatform.random[Rdf](ops)
  }


  lazy val graph: Var[PointedGraph[Rdf]] = Var[PointedGraph[Rdf]](
    new PointedGraph[Rdf]{
      override def pointer: Rdf#Node = subject

      override def graph: Rdf#Graph = ops.emptyGraph
    }

  )

  /*

    var createIfNotExists:Boolean = true

    val model: Var[ModelInside] = this.modelOption.getOrElse(Var(ModelInside(PropertyModel.empty)))

    lazy val dirty = Rx{this.model().isDirty}

    def die() = this.model() = this.model.now.apoptosis*/
}
