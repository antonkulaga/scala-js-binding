package org.denigma.views.lists

import org.denigma.extensions._
import org.denigma.views.core.{BindingView, OrdinaryView}
import org.scalajs.dom._
import org.scalajs.dom.extensions._
import rx.Rx

import scala.collection.immutable._


trait CollectionView {
  self:OrdinaryView=>

  type Item
  type ItemView <: BindingView

  val items:Rx[List[Item]]

  val template: HTMLElement = this.extractTemplate()

  /**
   * Extracts item template
   * @return
   */
  protected def extractTemplate(): HTMLElement =   self.viewElement.childNodes.collectFirst{
    case n:HTMLElement if n.attributes.contains("data-template")=>n}.getOrElse {
    self.viewElement.childNodes.collectFirst {
      case viewElement: HTMLElement => viewElement
    }.getOrElse(self.viewElement)
  }


  def newItem(mp:Item):ItemView


  //TODO: maybe dangerous!!!
  /**
   *
   * @param el html element to which view is binded
   */
  override def bindView(el:HTMLElement) = {

    val id = "items_of_"+this.viewElement.id
    val span: HTMLElement = sq.byId(id) match {
      case Some(el)=>el
      case None=>
        val sp = document.createElement("span")
        sp.id = id
        if(template==viewElement) viewElement.appendChild(sp) else viewElement.replaceChild(sp,template)
        sp
    }
    val viewElements: List[ItemView] = this.items.now.map(this.newItem)

    viewElements.foreach{i=>
      this.addView(i)

      //viewElement.appendChild(i.viewElement)
      viewElement.insertBefore(i.viewElement,span)
      //viewElement.insertAdjacentviewElement()
      i.bindView(i.viewElement)
    }

    viewElement.children.collect{case el:HTMLElement=>el}.foreach(bind)

  }

}