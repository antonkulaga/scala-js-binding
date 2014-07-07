package org.denigma.binding.semantic

import org.denigma.binding.views.OrdinaryView
import org.scalajs.dom.HTMLElement
import rx._

import scala.collection.immutable.Map


trait ActiveModelView extends OrdinaryView with ModelView
{

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

  lazy val dirty = Rx{this.modelInside().isDirty}

}