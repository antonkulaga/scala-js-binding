package org.denigma.binding.views

import org.denigma.binding.binders.{BinderForViews, TemplateBinder}
import org.denigma.binding.extensions._
import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.dom.ext._
import org.scalajs.dom.raw.{SVGElement, HTMLElement, Element}

import scala.collection.immutable._
import scala.util.{Failure, Success, Try}
import org.denigma.binding.extensions._

trait CollectionView extends BindableView
{

  type Item
  type ItemView <: BasicView

 override protected lazy val defaultBinders: List[ViewBinder] = List(new BinderForViews[this.type](this), new TemplateBinder[this.type](this))

  private var _template: Element = viewElement // .cloneNode(true).asInstanceOf[Element]
  var templateDisplay = _template.dyn.style.display
  def template_=(value:Element) = if(value!=_template){
    _template = value
    templateDisplay = _template.dyn.style.display
  }

  def template: Element = _template
  /**
   * extracts element after which it inserts children
   * @return
   */
  def extractStart(): Element = {
    val id = "items_of_"+viewElement.id
    sq.byId(id) match {
      case Some(el)=>el
      case None=>
        val sp = document.createElement("span").asInstanceOf[Element]
        sp.id = id
        if(template==viewElement) {
          viewElement.appendChild(sp)
          dom.console.error(s"items are the same as $id")
        }
        else {
          //dom.console.log(template.outerHTML)
          this.replace(sp,template)
        }
        template.dyn.style.display = "none"
        sp
    }
  }

  def replaceHTML(newChild: Element, oldChild: Element, switch: Boolean = false) = {
    def oldNew() = s"\nnewChild = ${newChild.outerHTML}\noldChild = ${oldChild.outerHTML}"
    if(oldChild!=newChild)
      (newChild.parentElement, oldChild.parentElement) match {
        case (pn, null) =>
          console.error(s"old child has no parent:"+oldNew())
          oldChild

        case (null, po) =>
          po.replaceChild(newChild, oldChild)
          if (switch) console.error("new child has null parent"+oldNew)

        case (pn, po) if pn == po =>
          if (switch) {
            val io = po.children.indexOf(oldChild)
            val in = pn.children.indexOf(newChild)
            //po.removeChild(oldChild)
            po.children(io) = newChild
            pn.children(in) = oldChild
            //console.error("new child has null parent")
          } else po.replaceChild(newChild, oldChild)

        case (pn, po) =>
          val io = po.children.indexOf(oldChild)
          val in = pn.children.indexOf(newChild)
          //po.removeChild(oldChild)
          po.children(io) = newChild
          if (switch) {
            pn.children(in) = oldChild
          }
      }
  }

    /**
   * Replaces elementes
   * @param newChild new child
   * @param oldChild oldchild
   * @return
   */
  def replace(newChild: Element, oldChild: Element, switch: Boolean = false) = {
      (newChild, oldChild) match {
        case (n: HTMLElement, o: HTMLElement) => replaceHTML(n , o, switch)
        case (n: SVGElement, o: SVGElement) =>  replaceHTML(n ,o, switch)//replaceSVG(n,o,switch)
        case _ => dom.console.error(s"unknown elements types of old ${oldChild.outerHTML} and new ${newChild.outerHTML} elements, cannot replace")
      }
  }

  override def bindView(): Unit = {
    this.bindElement(this.viewElement)
    this.subscribeUpdates()
  }

  protected def onInsert(item: Item): ItemView = this.addItemView(item, this.newItemView(item))
  protected def onRemove(item: Item): Unit = this.removeItemView(item)

  protected def getItemView(el: Element) = el.attributes.get("data-item-view")//.orElse(el.attributes.get("data-view"))

  def copyTemplate(): ViewElement = {
    val el = template.cloneNode(true).asInstanceOf[Element]
    el.dyn.style.display = templateDisplay
    el
  }

  /**
   * Function to create Item. Often used for default item creation
   * @param item item that will be added
   * @param construct function that does construction
   * @return
   */
  def constructItemView(item: Item, mp: Map[String, Any] = Map.empty)
                   ( construct: (Element, Map[String, Any]) => ItemView): ItemView =
  {
    //dom.console.log(template.outerHTML.toString)
    val el = copyTemplate()
    val view = getItemView(el) match {
      case None=> construct(el,mp)
      case Some(v)=> this.inject(v.value, el, mp) match {
        case iv:ItemView if iv.isInstanceOf[ItemView]=> iv
        case _=>
          dom.console.error(s"view ${v.value} exists but does not inherit ItemView")
          construct(el, mp)
      }
    }
    view
  }

  var itemViews = Map.empty[Item, ItemView]

  def addItemView(item: Item, iv: ItemView): ItemView = {
    Try ( template.parentElement.insertBefore(iv.viewElement, template) ) match {
      case Failure(th) =>
        dom.console.error("stack trace "+th.getMessage+"\n"+th.getStackTrace.toList.mkString("/n"))
        println("TEMPLATE ="+template.outerHTML)
        println("IV ="+iv.viewElement.outerHTML)
        println("TEMPLATE PARENT ="+template.parentElement.outerHTML)
        println("and EL = \n**********\n"+this.viewElement.outerHTML)
      case Success(res) =>
    }
    iv match {
      case b: ChildView =>  this.addView(b)
    }
    itemViews = itemViews + (item->iv)
    iv.bindView()
    iv
  }

  def removeItemView(r: Item): Unit =  this.itemViews.get(r) match {
    case Some(rv) =>
      rv.unbindView()
      this.removeViewByName(rv.id)
      this.itemViews = itemViews - r
    case None =>
      dom.console.error("cannot find the view for item: "+r.toString+" in item view "+this.itemViews.toString+"\n")
  }

  def newItemView(item: Item): ItemView
  /**
   * Adds subscription
   */
  protected def subscribeUpdates(): Unit

}
