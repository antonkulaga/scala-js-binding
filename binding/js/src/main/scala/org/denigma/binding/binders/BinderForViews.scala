package org.denigma.binding.binders

import org.denigma.binding.views.{ViewInjector, OrganizedView}
import org.scalajs.dom.raw.HTMLElement
import org.scalajs.dom.ext._

import scala.collection.immutable.Map

class BinderForViews[View<:OrganizedView](val view:View) extends Binder
{

  import org.scalajs.dom.ext._

  protected def attributesToParams(ats:Map[String,String]): Map[String, Any] =
    ats.collect{   case (key,value) if key.contains("data-param-")=>
      key.replace("data-param-", "") -> value.asInstanceOf[Any]   }


  def createView(el:HTMLElement,ats:Map[String,String],viewAtt:String) =     {
    val params = attributesToParams(ats)
    val v = view.inject(viewAtt,el,params)
    view.addView(v) //the order is intentional
    v.bindView()
    v
  }


  override def bindAttributes(el: HTMLElement, attributes: Predef.Map[String, String]): Boolean =
    attributes.get("data-view") match {
      case Some(v) if el.id.toString!=view.id =>
        view.subviews.getOrElse(el.id, this.createView(el,attributes,v))
        false //tells that we do not continue
      case other=> true
    }
}
