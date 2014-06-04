package org.denigma.views

import scala.collection.immutable.Map
import org.scalajs.dom.{MouseEvent, Attr, HTMLElement}
import org.scalajs.dom.extensions._
import scala.collection.mutable
import org.scalajs.dom
import org.denigma.extensions
import extensions._

import scala.scalajs.js
import js.Dynamic.{ global => g }
import org.denigma.binding.JustBinding
import scala.util.{Success, Failure}
import scala.annotation.tailrec
import rx._
import scalatags._
import scala.util.Failure
import scala.Some
import scala.util.Success
import scala.util.Failure
import scala.Some
import scala.util.Success

object BindingView {
  /**
   * created if we do not know the view at all
   * @param name of the view
   * @param elem dom element inside
   */
  class JustView(name:String,elem:dom.HTMLElement) extends OrdinaryView(name,elem)
  {

    override def tags: Map[String, Rx[HtmlTag]] = this.extractTagRx(this)

    override def strings: Map[String, Rx[String]] = this.extractStringRx(this)

    override def bools: Map[String, Rx[Boolean]] = this.extractBooleanRx(this)

    override def mouseEvents: Map[String, Var[MouseEvent]] = this.extractMouseEvents(this)


  }

  def apply(name:String,elem:dom.HTMLElement) = new JustView(name,elem)

}

/**
 * Basic view for binding
 * @param name of te view
 * @param elem element inside
 */
abstract class BindingView(val name:String,elem:dom.HTMLElement) extends JustBinding
{
  var topView:BindingView = this

  var parent:Option[BindingView] = None

  /**
   * finds neares parent of appropriate type
   * @tparam TView type that a parent should satisfy
   * @return
   */
  def nearestParentOf[TView<:BindingView]:Option[TView] = this.parent match {
    case None=>None
    case Some(par:TView)=>Some(par)
    case Some(par)=>par.nearestParentOf[TView]
  }

  def searchUp[TView<:BindingView](filter:TView=>Boolean): Option[TView] = parent match {
    case Some(p:TView)=>if(filter(p)) Some(p) else p.searchUp(filter)
    case Some(p)=> p.searchUp[TView](filter)
    case None=>None
  }

  def hasParent = parent.isDefined

  /**
   * Id of this view
   */
  val id: String =this.makeId(elem,this.name)

  var subviews = Map.empty[String,BindingView]


  def addView(view:BindingView) = this.subviews = this.subviews + (view.id -> view)


  /**
   * Extracts view by name from element
   * @param viewName name of the view
   * @param el html element
   * @param params some other optional params needed to init the view
   * @return
   */
  def inject(viewName:String,el:HTMLElement,params:Map[String,Any]): BindingView ={factories.get(viewName) match {
    case Some(fun)=>
      fun(el,params) match {
        case Success(view)=>view

        case Failure(e)=>
          //dom.console.error(e.toString)
          if(e!=null)
            dom.console.error(s"cannot initialize the view for $viewName because of ${e.toString}")
          else
            dom.console.error(s"Cannot initialize the view for $viewName")
          BindingView.apply(name,el)
    }
    case _ =>
      dom.console.error(s"cannot find view class for $viewName")
      BindingView.apply(name,el)
  }
  }

  protected var _element = elem
  def viewElement = _element
  def viewElement_=(value:HTMLElement): Unit = if(_element!=value) {
    this._element = value
    this.bind(value)
  }

  protected def bindDataAttributes(el:HTMLElement,ats:Map[String, String] )

  /**
   * Binds element attributes
   * @param el
   */
  protected def bindElement(el:HTMLElement) = {
    val ats: Map[String, String] = el.attributes.collect{
      case (key,attr) if key.contains("data-") && !key.contains("data-view") =>
        (key.replace("data-",""),attr.value.toString)
    }.toMap
    this.bindDataAttributes(el,ats)

  }


  /**
   * Fires when view was binded by default does the same as bind
   * @param el
   */
  def bindView(el:HTMLElement) = this.bind(el)


  def unbindView() = {
    this.unbind(this.viewElement)
  }

  def unbind(el:HTMLElement)= {
    //is required for thos view that do need some unbinding

  }
  /**
   * Changes inner HTML removing redundant views
   * @param el
   * @param newInnerHTML
   */
  def switchInner(el:HTMLElement, newInnerHTML:String) = {
    el.innerHTML = newInnerHTML
    bind(el)
  }


  protected def attributesToParams(el:HTMLElement): Map[String, Any] = el.attributes
   .collect{
      case (key,value) if key.contains("data-param-")=>
        key.replace("data-param-", "") -> value.value.asInstanceOf[Any]
    }.toMap


  /**
   * Creates view
   * @param el
   * @param viewAtt
   * @return
   */
  protected def createView(el:HTMLElement,viewAtt:String) =     {
      val params = this.attributesToParams(el)
      val v = this.inject(viewAtt,el,params)
      v.parent = Some(this)
      v.topView = this.topView
      v.bindView(el)
      this.addView(v) //the order is intentional
      v

    }


  /**
   * Finds view if it exists
   * @param viewName
   * @return
   */
  def findView(viewName:String):Option[BindingView] = if(this.name==viewName) Some(this) else {
    if(this.subviews.isEmpty) None else this.subviews.values.find(s=>s.findView(viewName).isDefined).flatMap(s=>s.findView(viewName))
  }


  /**
   * Removes view
   * @param name view name to remove
   * */
  def removeView(name:String): Unit =
    for{
      view <-    this.subviews.get(name)
    }    {
      view.unbindView()
      this.subviews = this.subviews - name
    }

  /**
   * checks if this view is inside some html element in the tree
   * @param element
   * @param me
   * @return
   */
  def isInside(element:HTMLElement, me:HTMLElement = this.viewElement):Boolean = element==me ||
    ( if(me.parentElement.isNullOrUndef) false else isInside(element,me.parentElement) )




  /** *
    * Extracts view
    * @param el
    * @return
    */
  def viewFrom(el:HTMLElement): Option[String] = el.attributes.get("data-view").map(_.value)
  //
  /**
   * Binds nodes to the element
   * @param el
   */
  def bind(el:HTMLElement):Unit =   this.viewFrom(el) match {

        case Some(view) if el.id.toString!=this.id =>
          this.subviews.getOrElse(el.id, this.createView(el,view))

        case _=>
          this.bindElement(el)
          if(el.hasChildNodes()) el.childNodes.foreach {
            case el: HTMLElement => this.bind(el)
            case _ => //skip
          }
      }

  def isTopView = this.topView == this

}

