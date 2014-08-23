package org.denigma.binding.views

import org.denigma.binding.binders.JustBinding
import org.denigma.binding.extensions._
import org.scalajs.dom
import org.scalajs.dom.HTMLElement
import org.scalajs.dom.extensions._

import scala.collection.immutable.Map
import scala.reflect.ClassTag
import scala.util.{Failure, Success}


trait BindingEvent{
  val origin:BindingView
  val latest:BindingView
  val bubble:Boolean

  def withCurrent(cur:BindingView):this.type

}

trait BindingView extends JustBinding with IView
{
  def elem:dom.HTMLElement

  def name:String = this.getClass.getName


  def params:Map[String,Any]

  require(elem!=null,s"html elemenet of view with $id must not be null!")

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

  /**
   * Fires an event
   * @param event
   * @param startWithMe
   */
  def fire(event:BindingEvent,startWithMe:Boolean = false) = if(startWithMe) this.receive(event) else  this.propagate(event)

  protected def propagate(event:BindingEvent) = if(this.parent.isDefined) this.parent.get.receive(event.withCurrent(this))


  /**
   * Event subsystem
   * @return
   */
  def receive:PartialFunction[BindingEvent,Unit] = {
    case event:BindingEvent=> this.propagate(event)
  }

  def hasParent = parent.isDefined

  /**
   * Id of this view
   */
  val id: String =this.makeId(elem,this.name)

  implicit var subviews = Map.empty[String,BindingView]


  def addView(view:BindingView) = {
    this.subviews = this.subviews + (view.id -> view)
    view.parent = Some(this)
  }

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
  def viewElement: HTMLElement = _element
  def viewElement_=(value:HTMLElement): Unit = if(_element!=value) {
    this._element = value
    this.bind(value)
  }

  protected def bindDataAttributes(el:HTMLElement,ats:Map[String, String] )

  /**
   * Binds element attributes
   * @param el
   */
  protected def bindElement(el:HTMLElement): Unit = {
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

  /**
   * is overriden in parent views
   * @param name
   * @param params
   * @return
   */
  protected def withParam(name:String,params:Map[String,Any]) = params


  /**
   * Turns data-param attribue into real param
   * @param el
   * @return
   */
  protected def attributesToParams(el:HTMLElement): Map[String, Any] = el.attributes
   .collect{
      case (key,value) if key.contains("data-param-")=>
        val k = key.replace("data-param-", "")
        val v = value.value
        if(v.startsWith("parent."))
        {
          val pn = v.replace("parent.","")
          this.params.get(pn) match {
            case Some(p)=>
              k->p.asInstanceOf[Any]
            case None=>
              dom.console.log(s"could not find data-param $pn for child")
              k -> pn
          }
        } else k -> v.asInstanceOf[Any]


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

  def findSubView(viewName:String)(implicit where:Map[String,BindingView] = this.subviews):Option[BindingView] = if(where.isEmpty) None else
    if(where.isEmpty) None else where.get(viewName)
    .orElse{    findSubView(viewName)(subviews.flatMap(kv=>kv._2.subviews))  }

  /**
   * Finds view if it exists
   * @param viewName
   * @return
   */
  def findView(viewName:String):Option[BindingView] = if(this.name==viewName) Some(this) else
    this.findSubView(viewName)(this.subviews)



  /**
   * Removes view
   * @param nm view name to remove
   * */
  def removeView(nm:String): Unit = this.subviews.get(nm) match {
    case Some(view)=> this.removeView(view)
    case None=>
      dom.console.log(s"now subview to remove for $nm from ${this.id}")
  }

  /**
   * Removes a view from subviews
   * @param view
   */
  def removeView(view:BindingView): Unit = {
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

