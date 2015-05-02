package org.denigma.binding.views.collections

import org.denigma.binding.extensions._
import org.denigma.binding.views.{BasicView, BindingEvent, BindableView, IView}
import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.dom.raw.HTMLElement
import rx.{Var, Rx}
import rx.extensions.{CollectionUpdate, Moved}
import org.scalajs.dom.ext._
import scala.collection.immutable._


trait CollectionView extends BindableView
{

  type Item
  type ItemView <: IView


  val items:Rx[List[Item]]

  var template: HTMLElement = viewElement

  lazy val span = this.extractStart() //all items are inserted after it
  /**
   * extracts element after wich it inserts children
   * @return
   */
  def extractStart(): HTMLElement = {
    val id = "items_of_"+this.viewElement.id
    sq.byId(id) match {
      case Some(el)=>el
      case None=>
        val sp = document.createElement("span").asInstanceOf[HTMLElement]
        sp.id = id
        if(template==viewElement) {
          viewElement.appendChild(sp)
          dom.console.error(s"items are the same as $id")
        }
        else this.replace(sp,template)
        sp
    }
  }

  /**
   * Replaces elementes
   * @param newChild new child
   * @param oldChild oldchild
   * @return
   */
  def replace(newChild:HTMLElement,oldChild:HTMLElement, switch:Boolean = false) = if(oldChild!=newChild)
    (newChild.parentElement, oldChild.parentElement) match
    {
     case (pn,null)=>
       console.error("old child has not parent")
       oldChild
     case (null,po)=>
       po.replaceChild(newChild,oldChild)
       if(switch) console.error("new child has null parent")

     case (pn,po) if pn==po=>
       if(switch) {
         val io = po.children.indexOf(oldChild)
         val in = pn.children.indexOf(newChild)
         //po.removeChild(oldChild)
         po.children(io) = newChild
         pn.children(in) = oldChild
         //console.error("new child has null parent")
       } else  po.replaceChild(newChild,oldChild)

     case (pn,po) =>
       val io = po.children.indexOf(oldChild)
       val in = pn.children.indexOf(newChild)
       //po.removeChild(oldChild)
       po.children(io) = newChild
       if(switch) {
         pn.children(in) = oldChild
       }


  }

  override def bindView(el: HTMLElement) = {
    if(el.attributes.contains("data-item-view")) el.removeAttribute("data-item-view") //to avoid using self in nested views
    attachBinders()
    activateMacro()
    this.bind(el)
    this.subscribeUpdates()
  }

  /**
   * Binds nodes to the element
   * @param el
   */
  override def bind(el:HTMLElement):Unit =
    if(el.attributes.contains("data-template"))
    {
      el.removeAttribute("data-template")
      this.template = el
    }
    else
      this.viewFrom(el) match
      {

      case Some(view) if el.id.toString!=this.id =>
        this.subviews.getOrElse(el.id, this.createView(el,view))

      case _=>
        this.bindElement(el)
        if(el.hasChildNodes()) el.childNodes.foreach {
          case el: HTMLElement => this.bind(el)
          case _ => //skip
        }
      }

  lazy val updates = Watcher(items).updates


  protected def onInsert(item:Item) = this.addItemView(item,this.newItem(item))
  protected def onRemove(item:Item) = this.removeItemView(item)
  protected def onMove(mv:Moved[Item]) = {
    val fr = itemViews(items.now(mv.from))
    val t = itemViews(items.now(mv.to))
    this.replace(t.viewElement,fr.viewElement)
  }

  /**
   * Adds subscription
   */
  protected   def subscribeUpdates(){
    this.items.now.foreach(i=>this.addItemView(i,this.newItem(i)))
    updates.handler{
      updates.now.inserted.foreach(onInsert)
      updates.now.removed.foreach(onRemove)
      updates.now.moved.foreach(onMove)
      }
  }

  protected def getItemView(el:HTMLElement) = el.attributes.get("data-item-view")//.orElse(el.attributes.get("data-view"))


  /**
   * Function to create Item. Often used for default item creation
   * @param item item that will be added
   * @param construct function that does construction
   * @return
   */
  def constructItem(item:Item,mp:Map[String,Any] = Map.empty)
                   ( construct:(HTMLElement,Map[String,Any])=>ItemView):ItemView =
  {
    //dom.console.log(template.outerHTML.toString)
    val el = template.cloneNode(true).asInstanceOf[HTMLElement]

    el.removeAttribute("data-template")

    val view = getItemView(el) match {
      case None=>
        construct(el,mp)
      case Some(v)=> this.inject(v.value,el,mp) match {
        case iv:ItemView if iv.isInstanceOf[ItemView]=> iv
        case _=>
          dom.console.error(s"view ${v.value} exists but does not inherit ItemView")
          construct(el,mp)
      }
    }
    view
  }

  def newItem(item:Item):ItemView

  var itemViews = Map.empty[Item,ItemView]

  def addItemView(item:Item,iv:ItemView):ItemView = {
    span.parentElement.insertBefore(iv.viewElement,span)
    iv match {
      case b:ChildView=>  this.addView(b)
    }
    itemViews = itemViews + (item->iv)
    iv.bindView(iv.viewElement)
    //dom.console.log(iv.viewElement.innerHTML)
    iv
  }

  def removeItemView(r:Item) =  this.itemViews.get(r) match {
    case Some(rv)=>
      rv.unbindView()
      this.removeViewByName(rv.id)
      this.itemViews = itemViews - r
    case None=>
      dom.console.error("cantot find the view for item: "+r.toString+" in item view "+this.itemViews.toString+"\n")

  }




}