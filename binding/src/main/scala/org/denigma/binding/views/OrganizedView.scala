package org.denigma.binding.views

import org.denigma.binding.extensions
import org.denigma.binding.extensions._
import org.scalajs.dom
import org.scalajs.dom.HTMLElement

import scala.scalajs.js
import scala.scalajs.js.Dynamic.{global => g}

import dom.extensions._


/**
 * A view that works well with hierarchy
 */
abstract class OrganizedView extends BindingView //with GeneralBinding
{


  /**
   * Overrides for load element into that includes view switching
   * @param el element
   * @param newInnerHTML new content of the inner html
   * @param uri uri (for push state)
   */
  override def loadElementInto(el:HTMLElement, newInnerHTML:String,uri:String=""): Unit =  if(this.isTopView)
  {
    val params = js.Dynamic.literal( "html" -> newInnerHTML)
    if(uri!="") dom.window.history.pushState(params,dom.document.title,uri)
    //dom.console.log(this.findNearestViewName(el).toString)
    this.findNearestParentViewName(el) match {
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

  def collectFirstSubView[B](pf: PartialFunction[OrganizedView, B])(implicit where:Seq[BindingView] = this.subviews.values.toSeq):Option[B] =
    if(where.isEmpty) None else   where.collectFirst { case o: OrdinaryView if pf.isDefinedAt(o) =>pf(o)}
    .orElse{
      this.collectFirstSubView(pf)(where.flatMap(v => v.subviews.values))
    }

  def collectFirstView[B](pf: PartialFunction[OrganizedView, B]):Option[B] =
    if(pf.isDefinedAt(this)) Some(pf(this)) else this.collectFirstSubView(pf)(this.subviews.values.toSeq)


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
  def findNearestParentViewName(el:HTMLElement):Option[String] = this.viewFrom(el) match {
    case Some(att)=>Some(att)
    case None=> if(el.parentElement.isNullOrUndef) None else this.findNearestParentViewName(el.parentElement)
  }


  /**
   * Removes view, starts search from the top
   * @param element
   */
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
    dom.console.log(s"removing subviews from $name: ${toRemove.toString()} \n remaining views: ${subviews.toString()}")
  }


  /**
   * refreshes child view
   * @param viewId
   * @param newElement
   */
  def refreshChildView(viewId:String,newElement:HTMLElement):Unit = this.findView(viewId) match {
    case Some(v:OrganizedView)=>
      v.refreshMe(newElement)
    case None=>
      error(s"cannot find $viewId among childs")
    }



//  def refreshView(v:BindingView,tag:String) = {
//   // jQuery.
//  }

  /**
   * Trick to create an html
   * @param string
   * @return
   */
  def createHTML(string:String) = {
    val d = dom.document.implementation.createHTMLDocument("")
    d.body.innerHTML = string
    d.body
  }

  def refreshMe(newElement:HTMLElement) = this.parent match {
    case Some(pv)=>

      info("before = "+pv.subviews.toString())
      this.viewElement.parentElement match {
        case null=>
          error(s"cannot replace element of ${viewElement.id}")
          pv.viewElement.appendChild(newElement)


        case other=>
          info("from =" + viewElement.outerHTML)
          info("to = " + newElement.outerHTML)
          other.replaceChild(newElement,this.viewElement)
      }
      newElement.setAttribute("id",this.id)
      pv.removeView(this)
      pv.bind(newElement)
      info("after = "+pv.subviews.toString())


    case None=> this.error("topview refresh is not supported yet")
  }




}
