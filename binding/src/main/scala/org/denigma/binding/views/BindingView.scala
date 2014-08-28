package org.denigma.binding.views

import org.denigma.binding.binders.BasicBinding
import org.denigma.binding.extensions._
import org.denigma.binding.views.utils.ViewFactory
import org.scalajs.dom
import org.scalajs.dom.HTMLElement
import org.scalajs.dom.extensions._

import scala.collection.immutable.Map
import scala.reflect.ClassTag
import scala.util.{Try, Failure, Success}


trait Injector[ChildView<:BindingView] {

  def inject(viewName:String,element:HTMLElement,params:Map[String,Any]):Option[Try[ChildView]]
}

trait BindingView extends BasicBinding with IView
{


  type ChildView <: BindingView

  //used by default as implicit value
  implicit def defaultInjector:Injector[ChildView]

  /**
   * Works when injectors fails to create a view
   * @param name
   * @param el
   * @return
   */
  def makeDefault(name:String,el:HTMLElement):ChildView


  def elem:dom.HTMLElement

  def name:String = this.getClass.getName.split('.').last


  def params:Map[String,Any]

  require(elem!=null,s"html elemenet of view with $id must not be null!")


  /**
   * Id of this view
   */
  val id: String = this.makeId(elem,this.name)

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
  def inject(viewName:String,el:HTMLElement,params:Map[String,Any])(implicit injector:Injector[ChildView]): ChildView =
  {
    //factories.get(viewName)
    injector.inject(viewName,el,params) match {
    case Some(tr)=>
      tr match {
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
      this.addView(v) //the order is intentional
      v.bindView(el)
      v
    }

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

}

