package org.denigma.graphs.simple

import org.denigma.graphs.core.VisualEdge
import org.denigma.graphs.visual.EdgeView
import org.scalajs.dom
import rx.core.Var


class SimpleEdge(from:SimpleNode,to:SimpleNode,data:Var[String],view:EdgeView[Var[String]]) extends VisualEdge[SimpleNode,Var[String],EdgeView[Var[String]]](from,to,data,view)
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
