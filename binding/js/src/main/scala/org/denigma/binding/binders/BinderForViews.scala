package org.denigma.binding.binders

import org.denigma.binding.views.{ViewInjector, OrganizedView}
import org.scalajs.dom
import org.scalajs.dom.raw.{Element, HTMLElement}
import org.scalajs.dom.ext._
import org.denigma.binding.extensions._

import scala.collection.immutable.Map
import scala.util.{Failure, Success}

/**
  * Binder that creates view when it finds data-view="SomeViewName" in HTML
  * @param parent parent view
  * @tparam View View that should be created
  */
class BinderForViews[View<:OrganizedView](val parent: View) extends Binder
{

  protected def attributesToParams(ats: Map[String, String]): Map[String, Any] =
    ats.collect{ case (key, value) if key.contains("data-param-")=>
      key.replace("data-param-", "") -> value.asInstanceOf[Any]   }


  def createView(el: Element, ats: Map[String, String], viewName: String): View#ChildView =     {
    val params = attributesToParams(ats)
    el.id = if(parent.subviews.keySet.contains(viewName)) viewName + "#" +  math.round(1000000*math.random) else viewName //adds view id to the element
    val v = parent.inject(viewName, el, params)
    parent.addView(v) //the order is intentional
    v.bindView()
    v
  }


  override def bindAttributes(el: Element, attributes: Map[String, String]): Boolean =
    attributes.get("data-view") match {
      case Some(v) if el.id.toString!=parent.id =>
        parent.subviews.getOrElse(el.id, this.createView(el,attributes, v))
        false //tells that we do not continue
      case other => true
    }
}
