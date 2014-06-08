package org.denigma.views.core

import org.scalajs.dom.HTMLElement
import scala.scalajs.js
import org.scalajs.dom
import scala.collection.immutable.Map
import org.scalajs.dom.{Attr, HTMLElement}
import org.scalajs.dom.extensions._
import scala.collection.mutable
import org.scalajs.dom
import org.denigma.extensions
import extensions._

import scala.scalajs.js
import js.Dynamic.{ global => g }
import org.denigma.binding.{GeneralBinding, PropertyBinder, JustBinding}
import scala.util.{Success, Failure}
import scala.annotation.tailrec
import rx.core.{Obs, Rx}




/**
 * A view that works well with hierarchy
 */
abstract class OrganizedView(name:String,elem:HTMLElement) extends BindingView(name,elem) //with GeneralBinding
{


  override def loadElementInto(el:HTMLElement, newInnerHTML:String,uri:String=""): Unit =  if(this.isTopView)
  {
    val params = js.Dynamic.literal( "html" -> newInnerHTML)
    if(uri!="") dom.window.history.pushState(params,dom.document.title,uri)
    //dom.console.log(this.findNearestViewName(el).toString)
    this.findNearestViewName(el) match {
      case None=>
        this.switchInner(el,newInnerHTML)
      //dom.console.error(s"cannot find nearest viewname for loaded element from ${this.name}")
      case Some(vn)=>
        val v: Option[BindingView] = this.findView(vn)
        if(v.isDefined)
          v.foreach{ev=>ev.switchInner(el,newInnerHTML) }
        else
          dom.console.error(s"cannot find view for $vn")
    }
  }
  else this.topView.loadElementInto(el,uri, newInnerHTML)


  /**
   * Changes inner HTML removing redundant views
   * @param el element which inner html will change
   * @param newInnerHTML new inner html value
   */
  override def switchInner(el:HTMLElement, newInnerHTML:String) = {
    removeSubViewsFrom(el)
    el.innerHTML = newInnerHTML
    bind(el)
  }


  /**
   * Finds a view that
   * @param el
   * @return
   */
  def findNearestViewName(el:HTMLElement):Option[String] = this.viewFrom(el) match {
    case Some(att)=>Some(att)
    case None=> if(el.parentElement.isNullOrUndef) None else this.findNearestViewName(el.parentElement)
  }



  def removeWithView(element:HTMLElement) = {
    for {
      v <-this.viewFrom(element)
      view: BindingView <-this.topView.findView(v)
      p <-view.parent
    }{
      p.removeView(v)
      dom.console.log("parent removed subview")
    }
  }

  /**
   * Removes all subviews that were inside element in the tree
   * @param element
   */
  def removeSubViewsFrom(element:HTMLElement) = {
    val toRemove = this.subviews.collect{case (key,value) if value.isInside(element)=>key}
    toRemove.foreach(r=>removeView(r))
    dom.console.log(s"removing subviews from $name: ${toRemove.toString()} \n remainin views: ${subviews.toString()}")
  }


}
