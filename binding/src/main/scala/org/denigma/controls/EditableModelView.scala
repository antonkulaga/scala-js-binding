package org.denigma.controls

import org.denigma.views.ModelView
import scala.collection.immutable.Map
import rx._
import scalatags._
import rx.core.Var
import org.scalajs.dom.{TextEvent, HTMLElement, MouseEvent}
import org.scalax.semweb.shex.PropertyModel
import org.scalax.semweb.rdf.{RDFValue, StringLiteral, IRI}
import org.denigma.binding.GeneralBinding
import org.denigma.binding.EventBinding
import org.denigma.binding.{GeneralBinding, EventBinding}
import org.scalajs.dom
import org.denigma.extensions._
import org.denigma.models.{Storage, AjaxStorage}
import org.scalajs.dom.HTMLElement
import org.scalax.semweb.shex.PropertyModel
import org.scalax.semweb.rdf.IRI
import org.scalax.semweb.rdf.StringLiteral
import org.denigma.views.ModelView
import org.scalajs.dom.MouseEvent
import org.scalajs.dom
import org.scalajs.dom.TextEvent


abstract class EditableModelView(name:String,element:HTMLElement,model:PropertyModel,params:Map[String,Any] = Map.empty ) extends ModelView(name,element,model) with EventBinding with GeneralBinding
{
  self:ModelView=>

  override protected def otherPartial:PartialFunction[String,Unit] = {case _=>}

  override def bindDataAttributes(el:HTMLElement,ats:Map[String, String]) = {
    this.bindProperties(el,ats)
    this.bindEvents(el,ats)
  }



  //TODO: rewrite
  override def bindProperties(el:HTMLElement,ats:Map[String, String]): Unit = for {
    (key, value) <- ats
  }{
    this.visibilityPartial(el,value)
      .orElse(this.classPartial(el,value))
      .orElse(this.propertyPartial(el,key.toString,value))
      .orElse(this.loadIntoPartial(el,value))
      .orElse(this.otherPartial)(key.toString)//key.toString is the most important!
  }

  val dirty = Rx{!this.modelInside().isUnchanged}

  val saveClick: Var[MouseEvent] = Var(this.createMouseEvent())

}
