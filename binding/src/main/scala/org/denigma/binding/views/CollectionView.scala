package org.denigma.binding.views

import org.denigma.binding.extensions._
import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.dom.extensions._
import rx.Rx

import scala.collection.immutable._
import scala.scalajs.js


trait CollectionView extends OrdinaryView{

  type Item
  type ItemView <: BindingView

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
        val sp = document.createElement("span")
        sp.id = id
        if(template==viewElement) {
          viewElement.appendChild(sp)
          alert(id)
        }
        else this.replace(sp,template)
        sp
    }

  }

  def replace(newChild:HTMLElement,oldChild:HTMLElement) = if(oldChild!=newChild)   (newChild.parentElement, oldChild.parentElement) match {
     case (pn,null)=>
       console.error("old child has not parent")
     case (pn,po) if pn==po || pn==null=>
       po.replaceChild(newChild,oldChild)
     case (pn,po) =>
       val io = po.children.indexOf(oldChild)
        po.children(io) = newChild
        po.removeChild(oldChild)

  }

  /**
   * Binds nodes to the element
   * @param el
   */
  override def bind(el:HTMLElement):Unit =   if(el.attributes.contains("data-template")) {
    el.removeAttribute("data-template")
    this.template = el
  } else this.viewFrom(el) match {

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



  /**
   * Adds subscription
   */
  protected   def subscribeUpdates(){
    this.items.now.map(this.newItem).foreach(this.addItemView)
    updates.handler{
      val inserted = updates.now.inserted
      val removed = updates.now.removed
      val moved = updates.now.moved
      inserted.foreach(i=>this.addItemView(this.newItem(i)))
      removed.foreach(r=>this.removeItemView(r))
      moved.foreach { case mv =>
        val fr = itemViews(items.now(mv.from))
        val t = itemViews(items.now(mv.to))
        val frp: HTMLElement = fr.viewElement.parentElement
        val tp: HTMLElement = t.viewElement.parentElement
          dom.console.log(moved.toString())
//        frp.replaceChild(t.viewElement, fr.viewElement)
//        tp.replaceChild(fr.viewElement, t.viewElement)
          //DOES NOT WORK YET!
      }

      //inserted.foreach(newItem)
    }
  }




  def newItem(mp:Item):ItemView

  var itemViews = Map.empty[Item,ItemView]

  def addItemView(iv:ItemView):ItemView = {
    span.parentElement.insertBefore(iv.viewElement)
    this.addView(iv)
    iv.bindView(iv.viewElement)
    iv
  }

  def removeItemView(r:Item) = {
    val rv = this.itemViews(r)
    rv.unbindView()
    this.removeView(rv.name)

  }




}