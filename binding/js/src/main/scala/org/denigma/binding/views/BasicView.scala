package org.denigma.binding.views

import org.denigma.binding.binders.Binder
import org.denigma.binding.extensions._
import org.scalajs.dom
import org.scalajs.dom.ext._
import org.scalajs.dom.raw.HTMLElement

import scala.annotation.tailrec
import scala.collection.immutable.Map
import scala.scalajs.js

trait IDGenerator{


  def ifNoIDOption(el:HTMLElement,titleOpt: =>Option[String]) = el.id match {
    case s if js.isUndefined(s) || s=="" ||  s==null=>
      for(title<-titleOpt)
      {
        el.id = title + "#" +  math.round(10000*math.random) //to make length shorter
        el.id
      }

    case id=>
      id
  }

  /**
   * Makes id for the binding element
   * @param el html element
   * @param title is used if the element does not have an ID
   * @return
   */
  def ifNoID(el:HTMLElement,title: =>String): String = el.id match {
    case s if js.isUndefined(s) || s=="" ||  s==null /*|| s.isInstanceOf[js.prim.Undefined] */=>
      el.id = title + "#" +  math.round(10000*math.random) //to make length shorter
      el.id

    case id=>
      id
  }

}

/**
 * Basic view class, contains basic binding features and children
 */
trait BasicView extends IDGenerator
{

  require(elem!=null,s"html element of view of class ${this.getClass.getName} with id $id  must not be null!")

  protected def binders:List[ViewBinder]


  type ViewBinder = Binder

  type ChildView <: BasicView

  def makeDefault(el:HTMLElement,props:Map[String,Any] = Map.empty):ChildView

  def elem:HTMLElement

  def name:String = this.getClass.getName.split('.').last


  /**
   * Id of this view
   */
  val id: String = this.ifNoID(elem,this.name)

  implicit var subviews = Map.empty[String,ChildView]


  /**
   * Adds view to subviews
   * @param view
   * @return
   */
  def addView(view:ChildView) = {
    this.subviews = this.subviews + (view.id -> view)
    view
    //view.parent = Some(this)
  }

  /**
   * Extracts view by name from element
   * @param viewName name of the view
   * @param el html element
   * @param params some other optional params needed to init the view
   * @return
   */
  def inject(viewName:String,el:HTMLElement,params:Map[String,Any]): ChildView


  protected def stackToString(e:Throwable) = {
    val trace = e.getStackTrace.toList
    trace.foldLeft("STACK TRACE = "){
      case (acc,el)=>   acc+s"\n ${el.toString}"    }
  }

  protected var _element = elem
  def viewElement: HTMLElement = _element
  def viewElement_=(value:HTMLElement): Unit = if(_element!=value) {
    this._element = value
    this.bindElement(value)
  }

  @tailrec private def bindAts(el:HTMLElement,ats: Map[String, String],bds:List[ViewBinder]):Boolean = bds match {
    case Nil=> true
    case head::tail=> if(head.bindAttributes(el,ats))  bindAts(el,ats,tail) else false
  }

  /**
   * Binds element attributes
   * @param el
   */
  protected def bindElement(el:HTMLElement): Unit = {
    val ats: Map[String, String] =el.attributes.map{ case (key,value)=> (key,value.value)}.toMap
    if(bindAts(el,ats,binders))
    {
      if(el.hasChildNodes()) el.childNodes.foreach {
        case el: HTMLElement => this.bindElement(el)
        case _ => //skip
      }
    }
  }

  def bindView() = this.bindElement(this.viewElement)


  def unbindView() = {
    this.unbind(this.viewElement)
  }

  def unbind(el:HTMLElement)= { } //this is required for those view that need some unbinding


  /** *
    * Extracts view
    * @param el
    * @return
    */
  def viewFrom(el:HTMLElement): Option[String] = el.attributes.get("data-view").map(_.value)


  /**
   * Changes inner HTML removing redundant views
   * @param el
   * @param newInnerHTML
   */
  def switchInner(el:HTMLElement, newInnerHTML:String) = {
    el.innerHTML = newInnerHTML
    bindElement(el)
  }

  /**
   * is overriden in parent views
   * @param name
   * @param params
   * @return
   */
  protected def withParam(name:String,params:Map[String,Any]) = params


  /**
   * Removes view
   * @param nm view name to remove
   * */
  def removeViewByName(nm:String): Unit = this.subviews.get(nm) match {
    case Some(view)=>
      this.removeView(view)

    case None=>
      dom.console.log(s"now subview to remove for $nm from ${this.id}")
  }

  /**
   * Removes a view from subviews
   * @param view
   */
  def removeView(view:ChildView): Unit = {
    view.unbindView()
    if(view.viewElement.parentElement!=null) view.viewElement.parentElement.removeChild(view.viewElement)
    this.subviews = this.subviews - view.id
  }

  /**
   * checks if this view is inside some html element in the tree
   * @param element
   * @param me
   * @return
   */
  def isInside(element:HTMLElement, me:HTMLElement = this.viewElement):Boolean = element==me ||
    ( if(me.parentElement.isNullOrUndef) false else isInside(element,me.parentElement) )
}

