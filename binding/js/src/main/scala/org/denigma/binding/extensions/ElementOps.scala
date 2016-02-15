package org.denigma.binding.extensions

import org.scalajs.dom
import org.scalajs.dom.{Element, Node}
import org.scalajs.dom.raw._

import scala.annotation.tailrec
import scala.scalajs.js
import scala.language.implicitConversions


class ExtendedHTMLElement(val el:HTMLElement) extends AnyVal with NodeOps with ExtendedElement{
  def fromParent[TOut](matcher:PartialFunction[Node,TOut]):Option[TOut] = fromParent(el)(matcher)

  def updateIfExist(key:String,value:js.Any): Unit = if(el.hasOwnProperty(key) && el.dyn.selectDynamic(key)!=value)
    el.dyn.updateDynamic(key)(value)

  def parentElement: Element = el.parentElement

  def style: CSSStyleDeclaration = el.style

  def display_=(value:String):Unit = this.style.display = value

  def display: String = this.style.display

  def hide() = display ="none"

}

class ExtendedSVGElement(val el:SVGElement) extends AnyVal with NodeOps with ExtendedElement{
  def fromParent[TOut](matcher:PartialFunction[Node,TOut]):Option[TOut] = super.fromParent(el)(matcher)

  def updateIfExist(key:String,value:js.Any) = if(el.hasOwnProperty(key) && el.dyn.selectDynamic(key)!=value)
    el.dyn.updateDynamic(key)(value)

  def parentElement: Element = this.fromParent[Element](el){case p:Element=>p}.get//el.ownerSVGElement

  def style: CSSStyleDeclaration = el.dyn.style.asInstanceOf[CSSStyleDeclaration]

  def display_=(value:String):Unit = this.style.display = value

  def display: String = this.style.display

  def hide() = display ="none"

}

trait NodeOps extends Any{
  @tailrec final def fromParent[TOut](node: Node)(matcher: PartialFunction[Node, TOut]): Option[TOut] =
    if (node.parentNode == null) None
    else
    if(matcher.isDefinedAt(node.parentNode))
      {
        Option(matcher(node.parentNode))
      }
    else {
      val parent = node.parentNode.parentNode
      if(parent==null) None else fromParent(parent)(matcher)
    }
}

class ExtendedNode(val node: Node) extends AnyVal with NodeOps
{
  def fromParentNode[TOut](matcher: PartialFunction[Node, TOut]): Option[TOut] = super.fromParent(node)(matcher)
}

trait ExtendedElement extends Any{
  def fromParent[TOut](node: Node)(matcher: PartialFunction[Node, TOut]): Option[TOut]

  def parentElement: Element

  def updateIfExist(key: String, value: js.Any): Unit

  implicit def extNode(el: Node): ExtendedNode = new ExtendedNode(el)
  def style: CSSStyleDeclaration

  def display_=(value:String):Unit

  def display: String

  def hide():Unit

}
/**
 * Extensions for HTMLElement
 */
trait ElementOps {

  implicit def extSVG(svg: SVGElement): ExtendedSVGElement = new ExtendedSVGElement(svg)
  implicit def extHTML(el: HTMLElement): ExtendedHTMLElement = new ExtendedHTMLElement(el)
  implicit def extNode(node: Node): ExtendedNode = new ExtendedNode(node)


  implicit def elementWithOps(el: Element):ExtendedElement =  el match {
    case html: HTMLElement => html
    case svg: SVGElement =>   svg
    case other =>
      dom.console.error(s"element ${el.outerHTML} cannot be transformed to extended element")
      ???
  }

}


