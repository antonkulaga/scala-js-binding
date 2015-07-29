package org.denigma.binding.views

import org.denigma.binding.extensions._
import org.scalajs.dom
import org.scalajs.dom.raw.{HTMLDocument, HTMLElement}

import scala.Predef
import scala.collection.immutable.Map
import scala.scalajs.js
import scala.scalajs.js.Dynamic.{global => g}
import scala.util.{Try, Failure, Success}

object OrganizedView {

  case class OrganizedInjector(
                                view:OrganizedView,
                               factories:Map[String,(HTMLElement,Map[String,Any])=>Try[OrganizedView#ChildView]])
    extends ViewInjector[OrganizedView]
  {
    self=>
    override type This = OrganizedInjector

    def register(name:String,init:(HTMLElement,Map[String,Any])=>Try[OrganizedView#ChildView]) =
      this.copy(factories = self.factories+(name->init))

  override protected def parentInjection(
                                            viewName: String,
                                            element: HTMLElement,
                                            params: Map[String, Any])
    =  view.nearestParentOf{ case p:OrganizedView=>p.injector.inject(viewName,element,params)}.flatten
  }


  implicit def injector(view:OrganizedView): InjectorMagnet[OrganizedView] =
    new InjectorMagnet[OrganizedView]  {
      type Injector = OrganizedInjector
      lazy val injector = OrganizedInjector(view,Map.empty)
    }

  implicit def injector(vf:(OrganizedView,Map[String,(HTMLElement,Map[String,Any])
                           =>Try[OrganizedView#ChildView]])): InjectorMagnet[OrganizedView] =

    new InjectorMagnet[OrganizedView]  {
      type Injector = OrganizedInjector
      lazy val injector =   OrganizedInjector(vf._1,vf._2)
    }

}

/**
 * An abstract hirercial view that provides methods to work with hirercy
 */
abstract class OrganizedView extends BasicView
{

  type ParentView = OrganizedView

  override type ChildView = OrganizedView

  lazy val injector:ViewInjector[OrganizedView] = this.defaultInjector

  def defaultInjector = ViewInjector[OrganizedView](this)

  var topView:OrganizedView = this

  def isTopView = this.topView == this


  override def inject(viewName:String,el:HTMLElement,params:Map[String,Any]): ChildView =
  {
    injector.inject(viewName,el,params) match {
      case Some(tr)=>
        tr match {
          case Success(view) => view

          case Failure(e) =>
            //dom.console.error(e.toString)
            if (e != null){
              dom.console.error(s"" +
                s"cannot initialize the $viewName view inside $id with params ${params.toString()} because of ${e.toString}")
              dom.console.error(stackToString(e))
            }
            else  dom.console.error(s"Cannot initialize the $viewName view in $id")
            makeDefault(el,params)
        }
      case _ =>
        dom.console.error(s"cannot find view class for $viewName")
        makeDefault(el,params)
    }
  }

  /**
   * Overrides for load element into that includes view switching
   * NOTE it always executes by top view (if this new is not top it redirects to topview)
   * @param el element
   * @param newInnerHTML new content of the inner html
   * @param uri uri (for push state)
   */
  def loadElementInto(el:HTMLElement, newInnerHTML:String,uri:String=""): Unit =  if(this.isTopView)
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

  /**
   * Take nearest parent that satisfy partial function conditions and maps it to arbitary result
   * In other words you can find a parent that satisfy your needs and extract everything you want from it
   * @param func
   * @tparam TResult
   * @return
   */
  def nearestParentOf[TResult](func:PartialFunction[ParentView,TResult]):Option[TResult] =  parent match {
    case None=>None
    case Some(par) if func.isDefinedAt(par)=>Some(func(par))
    case Some(par)=>par.nearestParentOf(func)
  }

  /**
   * If this view has any parent. Usually top view as well as views that are not in dome yet do not have parents
   * @return
   */
  def hasParent = parent.isDefined


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
      dom.console.error(s"cannot find $viewId among childs")
    }

  /**
   * Trick to create an html
   * @param string
   * @return
   */
  def createHTML(string:String) = {
    val d = dom.document.implementation.createHTMLDocument("")
    d match {
      case doc:HTMLDocument=>
        doc.body.innerHTML = string
        doc.body

      case _=> throw new Exception("dom.document.implementation.createHTMLDocument(\"\")")
    }
  }

  /**
   * Replaces viewElement of the view and rebinds it
   * @param newElement
   */
  def refreshMe(newElement:HTMLElement) = this.parent match {
    case Some(pv)=>

      //dom.console.info("before = "+pv.subviews.toString())
      this.viewElement.parentElement match {
        case null=>
          dom.console.error(s"cannot replace element of ${viewElement.id}")
          pv.viewElement.appendChild(newElement)


        case other=>
          dom.console.info("from =" + viewElement.outerHTML)
          dom.console.info("to = " + newElement.outerHTML)
          other.replaceChild(newElement,this.viewElement)
      }
      newElement.setAttribute("id",this.id)
      pv.removeView(this:ChildView)
      pv.bind(newElement)
      //dom.console.info("after = "+pv.subviews.toString())


    case None=> dom.console.error("topview refresh is not supported yet")
  }


  /**
   * Adds view to children
   * @param view
   * @return
   */
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
