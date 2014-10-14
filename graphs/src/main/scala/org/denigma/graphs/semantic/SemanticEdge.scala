package org.denigma.graphs.semantic

import org.denigma.graphs.core.VisualEdge
import org.denigma.graphs.visual.EdgeView
import org.scalajs.dom
import org.scalax.semweb.rdf.IRI
import org.scalax.semweb.shex.PropertyModel
import rx.core.Var


class SemanticEdge(from:SemanticNode,to:SemanticNode,data:Var[IRI],view:EdgeView[Var[IRI]]) extends VisualEdge[SemanticNode,Var[IRI],EdgeView[Var[IRI]]](from,to,data,view)
 {
   def id = data.now

   override def receive:PartialFunction[Any,Unit] = {

     case "mouseover"=>
       //dom.console.log("mouse over works")
       this.view.sprite.element.className = this.view.sprite.element.className.replace("tiny","small")

     case "mouseout"=>
       dom.console.log("mouse out works")
       this.view.sprite.element.className = this.view.sprite.element.className.replace("small","tiny")


     case other => dom.console.log(s"unknown message $other")
     //nothing
   }
 }
