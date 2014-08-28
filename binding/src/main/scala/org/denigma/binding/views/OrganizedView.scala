package org.denigma.binding.views

import org.denigma.binding.extensions
import org.denigma.binding.extensions._
import org.denigma.binding.views.utils.ViewInjector
import org.scalajs.dom
import org.scalajs.dom.HTMLElement

import scala.collection.immutable.Map
import scala.scalajs.js
import scala.scalajs.js.Dynamic.{global => g}

import dom.extensions._

trait BindingEvent
{
  val origin:BindingView
  val latest:BindingView
  val bubble:Boolean

  def withCurrent(cur:BindingView):this.type
}


/**
 * An abstract hirercial view that provides methods to work with hirercy
 */
abstract class OrganizedView extends BindingView //with GeneralBinding
{

  type ParentView = OrganizedView

  override type ChildView = OrganizedView

  implicit def defaultInjector:Injector[OrganizedView] = ViewInjector

  var topView:OrganizedView = this

  def isTopView = this.topView == this


  /**
   * Overrides for load element into that includes view switching
   * NOTE it always executes by top view (if this new is not top it redirects to topview)
   * @param el element
   * @param newInnerHTML new content of the inner html
   * @param uri uri (for push state)
   */
  override def loadElementInto(el:HTMLElement, newInnerHTML:String,uri:String=""): Unit =  if(this.isTopView)
  {
    val params = js.Dynamic.literal( "html" -> newInnerHTML)
    if(uri!="") dom.window.history.pushState(params,dom.document.title,uri)

    this.findNearestParentViewName(el) match {
      case None=>
        this.switchInner(el,newInnerHTML)
      //dom.console.error(s"cannot find nearest viewname for loaded element from ${this.name}")
      case Some(vn)=>
        this.findView(vn) match {
          case Some(v)=>
            v.switchInner(el,newInnerHTML)
          case None=>
            dom.console.error(s"cannot find view for $vn")

        }
    }
  }
  else this.topView.loadElementInto(el, newInnerHTML,uri)

  var parent:Option[ParentView] = None

  def nearestParentOf[TResult](func:PartialFunction[ParentView,TResult]):Option[TResult] =  parent match {
    case None=>None
    case Some(par) if func.isDefinedAt(par)=>Some(func(par))
    case Some(par)=>par.nearestParentOf(func)
  }

  /**
   * Event subsystem
   * @return
   */
  def receive:PartialFunction[BindingEvent,Unit] = {
    case event:BindingEvent=> this.propagate(event)
  }

  def hasParent = parent.isDefined

  /**
   * Fires an event
   * @param event
   * @param startWithMe
   */
  def fire(event:BindingEvent,startWithMe:Boolean = false) = if(startWithMe) this.receive(event) else  this.propagate(event)

  protected def propagate(event:BindingEvent) = if(this.parent.isDefined) this.parent.get.receive(event.withCurrent(this))




  def collectFirstSubView[B](pf: PartialFunction[OrganizedView, B])(implicit where:Seq[ChildView] = this.subviews.values.toSeq):Option[B] =
    if(where.isEmpty) None else   where.collectFirst { case o: ChildView if pf.isDefinedAt(o) =>pf(o)}
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

  def findSubView(viewName:String)(implicit where:Map[String,ChildView] = this.subviews): Option[ChildView]= if(where.isEmpty) None else
  if(where.isEmpty) None else where.get(viewName).orElse{
    val inner: Map[String, ChildView] = subviews.flatMap(kv=>kv._2.subviews)
    findSubView(viewName)(inner)
  }

  /**
   * Finds view if it exists
   * @param viewName
   * @return
   */
  def findView(viewName:String): Option[ChildView] = this match
  {
    case view:ChildView if this.name==viewName => Some(view)
    case _=>  this.findSubView(viewName)(this.subviews)

  }


  /**
   * Removes view, starts search from the top
   * @param element
   */
  def removeWithView(element:HTMLElement) = {
    for {
      v <-this.viewFrom(element)
      view <-this.topView.findView(v)
      p <-view.parent
    }{
      p.removeViewByName(v)
      dom.console.log("parent removed subview")
    }
  }

  /**
   * Removes all subviews that were inside element in the tree
   * @param element
   */
  def removeSubViewsFrom(element:HTMLElement) =
  {
    val toRemove = this.subviews.collect{case (key,value) if value.isInside(element)=>key}.toSet
    toRemove.foreach(r=>this.removeViewByName(r))
   // this.debug(s"removing subviews from $name: ${toRemove.toString()})")
   // this.debug(s"remaining views in $name: ${this.subviews.toString()}")
   // this.debug("========================================================\n")
   // this.debug(this.writeTree())
   // this.debug("========================================================\n")
  }

  /**
   * prepares view tree for printing
   * @param level
   * @return
   */
  def writeTree(level:Int = 0):String = (0 to level).foldLeft("")((acc, l) => acc + "  ") +
    s"$id -> $name\n" +
    this.subviews.values.foldLeft("")(
      (acc, v) => v.writeTree(level +1)
    )


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
      pv.removeView(this:ChildView)
      pv.bind(newElement)
      info("after = "+pv.subviews.toString())


    case None=> this.error("topview refresh is not supported yet")
  }


  override def addView(view:ChildView) = {
    super.addView(view)
    view.parent = Some(this)
    view.topView = this.topView
    view
    //view.parent = Some(this)
  }

  /**
   * Removes a view from subviews
   * @param view
   */
  override def removeView(view:ChildView): Unit = {
    super.removeView(view)
    view.parent = None
    view.topView = view
  }


}
