package org.denigma.binding.views

import org.denigma.binding.binders.JustBinding
import org.denigma.binding.extensions._
import org.scalajs.dom
import org.scalajs.dom.HTMLElement
import org.scalajs.dom.extensions._

import scala.collection.immutable.Map
import scala.reflect.ClassTag
import scala.util.{Failure, Success}
import org.denigma.binding.views._

trait BindingView extends JustBinding
{
  def name:String
  def elem:dom.HTMLElement


  require(elem!=null,s"html elemenet of view with $name must not be null!")

  var topView:BindingView = this

  var parent:Option[BindingView] = None

  /**
   * finds neares parent of appropriate type
   * @tparam TView type that a parent should satisfy
   * @return
   */
  def nearestParentOf[TView<:BindingView](implicit viewTag: ClassTag[TView]):Option[TView] = this.parent match {
    case None=>None
    case Some(par) if viewTag.runtimeClass.isInstance(par) => Some(par.asInstanceOf[TView])
    case Some(par)=>par.nearestParentOf[TView]
  }

  def searchUp[TView<:BindingView](filter:TView=>Boolean)(implicit viewTag: ClassTag[TView]): Option[TView] = parent match {
    case Some(p) if viewTag.runtimeClass.isInstance(p)=> p match {
      case view:TView=> if(filter(view)) Some(p.asInstanceOf[TView]) else p.searchUp(filter)
    }
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

  def makeDefault(name:String,el:HTMLElement):BindingView

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
          makeDefault(name,el)
    }
    case _ =>
      dom.console.error(s"cannot find view class for $viewName")
      makeDefault(name,el)
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

