package org.denigma.views

import scala.collection.immutable.Map
import org.scalajs.dom.{Attr, HTMLElement}
import org.scalajs.dom.extensions._
import scala.collection.mutable
import org.scalajs.dom
import org.denigma.extensions
import extensions._

import scala.scalajs.js
import js.Dynamic.{ global => g }
import org.denigma.binding.JustBinding
import scala.util.{Success, Failure}

object BindingView {
  /**
   * created if we do not know the view at all
   * @param name of the view
   * @param elem dom element inside
   */
  class JustView(name:String,elem:dom.HTMLElement) extends BindingView(name,elem){
    override def bindAttributes(el: HTMLElement, ats: mutable.Map[String, dom.Attr]): Unit = {
      //does nothing
    }
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

  protected def bindAttributes(el:HTMLElement,ats:mutable.Map[String, Attr] )

  /**
   * Binds element
   * @param el
   */
  protected def bindElement(el:HTMLElement) = {
    val ats: mutable.Map[String, Attr] = el.attributes.collect{
      case (key,attr) if key.contains("data-") && !key.contains("data-view") => (key.replace("data-",""),attr)
    }
    this.bindAttributes(el,ats)

  }


  /**
   * Fires when view was binded by default does the same as bind
   * @param el
   */
  def bindView(el:HTMLElement) = this.bind(el)


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
  protected def createView(el:HTMLElement,viewAtt:dom.Attr) =     {
      val params = this.attributesToParams(el)
      val v = this.inject(viewAtt.value,el,params)
      v.parent = Some(this)
      v.topView = this.topView
      v.bindView(el)
      this.addView(v) //the order is intentional
      v

    }

  /**
   *
   * Loads element into another one
   * @param el element
   * @param uri uri (for push state)
   * @param newInnerHTML new content of the inner html
   */
  override protected def loadElementInto(el:HTMLElement,uri:String, newInnerHTML:String) =  if(this.isTopView){
      val params = js.Dynamic.literal( "html" -> newInnerHTML)
      dom.window.history.pushState(params,dom.document.title,uri)
      //dom.console.log(this.findNearestViewName(el).toString)
      this.findNearestViewName(el) match {
        case None=>
          this.switchInner(el,newInnerHTML)
          //dom.console.error(s"cannot find nearest viewname for loaded element from ${this.name}")
        case Some(vn)=>
          val v: Option[BindingView] = this.findView(vn)
          if(v.isDefined) v.foreach{ev=>ev.switchInner(el,newInnerHTML) }

          else dom.console.error(s"cannot find view for $vn")
      }
    }
    else this.topView.loadElementInto(el,uri, newInnerHTML)

  /**
   * Changes inner HTML removing redundant views
   * @param el
   * @param newInnerHTML
   */
  def switchInner(el:HTMLElement, newInnerHTML:String) = {
    removeSubViewsFrom(el)
    el.innerHTML = newInnerHTML
    bind(el)
  }

  /**
   * Finds view if it exists
   * @param viewName
   * @return
   */
  def findView(viewName:String):Option[BindingView] = if(this.name==viewName) Some(this) else {
    if(this.subviews.isEmpty) None else this.subviews.values.find(s=>s.findView(viewName).isDefined).flatMap(s=>s.findView(viewName))
  }

//  /**
//   * Removes subview
//   * @param viewName nama of the view to b removed
//   * @return
//   */
//  def removeSubView(viewName:String) = this.subviews.get(viewName) match {
//    case Some(view)=>
//      subviews = subviews - viewName
//      //some other actions if neccesary
//    case None=> dom.console.log(s"failed to remove $viewName from $name because it did not exist in subviews")
//  }

  /**
   * Removes all subviews that were inside element in the tree
   * @param element
   */
  def removeSubViewsFrom(element:HTMLElement) = {
    val toRemove = this.subviews.collect{case (key,value) if value.isInside(element)=>key}
    this.subviews = this.subviews -- toRemove
    dom.console.log(s"removing subviews from $name: ${toRemove.toString()} \n remainin views: ${subviews.toString()}")
  }

  /**
   * checks if this view is inside some html element in the tree
   * @param element
   * @param me
   * @return
   */
  def isInside(element:HTMLElement, me:HTMLElement = this.viewElement):Boolean = element==me ||
    ( if(me.parentElement.isNullOrUndef) false else isInside(element,me.parentElement) )

  /**
   * Finds a view that
   * @param el
   * @return
   */
  def findNearestViewName(el:HTMLElement):Option[String] = this.viewFrom(el) match {
    case Some(att)=>Some(att.value)
    case None=> if(el.parentElement.isNullOrUndef) None else this.findNearestViewName(el.parentElement)
  }



  /** *
    * Extracts view
    * @param el
    * @return
    */
  def viewFrom(el:HTMLElement): Option[Attr] = el.attributes.get("data-view")
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

