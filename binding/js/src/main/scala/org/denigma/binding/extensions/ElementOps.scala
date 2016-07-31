package org.denigma.binding.extensions

import org.scalajs.dom
import org.scalajs.dom.{Element, Node}
import org.scalajs.dom.raw._

import scala.annotation.tailrec
import scala.scalajs.js
import scala.language.implicitConversions


class ExtendedHTMLElement(val el:HTMLElement) extends AnyVal with NodeOps with ExtendedElement{

  def isHidden = el.offsetParent == null

  def isVisible = el.offsetParent !=null

  def fromParent[TOut](matcher:PartialFunction[Node,TOut]):Option[TOut] = fromParent(el)(matcher)

  def updateIfExist(key:String,value:js.Any): Unit = if(el.hasOwnProperty(key) && el.dyn.selectDynamic(key)!=value)
    el.dyn.updateDynamic(key)(value)

  def parentElement: Element = el.parentElement

  def style: CSSStyleDeclaration = el.style

  def display_=(value:String):Unit = this.style.display = value

  def display: String = this.style.display

  def hide() = display ="none"

  def selectTagByAttribute(tag: String, attribute: String, value: String): Element = el.querySelector(s"""${tag}[${attribute}="${value}"]""")

  def selectAllTagsByAttribute(tag: String, attribute: String, value: String): NodeList = el.querySelectorAll(s"""${tag}[${attribute}="${value}"]""")

  def selectByAttribute(attribute: String, value: String): Element = el.querySelector(s"""[${attribute}="${value}"]""")

  def selectAllByAttribute(attribute: String, value: String): NodeList = el.querySelectorAll(s"""[${attribute}="${value}"]""")

  def selectByClass(classname: String): Element = el.querySelector(s".$classname")

  def selectAllByClass(classname: String): NodeList = el.querySelectorAll(s".$classname")

  def children: HTMLCollection = el.children


  @tailrec final def nextAll(current: Element = el, previous: List[Element] = Nil): List[Element] = current.nextElementSibling match {
    case null => previous.reverse
    case value if js.isUndefined(value) => previous.reverse
    case value => nextAll(value, value::previous)
  }

  @tailrec final def nextUntil(current: Element = el, previous: List[Element], until: (Element) => Boolean): List[Element] = current.nextElementSibling match {
    case null => previous.reverse
    case value if until(value) || js.isUndefined(value) => previous.reverse
    case value => nextUntil(value, value::previous, until)
  }

  @tailrec final def nextWhile(current: Element = el, previous: List[Element], whileCondition: (Element) => Boolean): List[Element] = current.nextElementSibling match {
    case null => previous.reverse
    case value if js.isUndefined(value) => previous.reverse
    case value if whileCondition(value)=> nextWhile(value, value::previous, whileCondition)
    case value => previous.reverse
  }

  @tailrec final def previousAll(current: Element = el, previous: List[Element]): List[Element] = current.previousElementSibling match {
    case null => previous.reverse
    case value if js.isUndefined(value) => previous.reverse
    case value => previousAll(value, value::previous)
  }

  @tailrec final def previousUntil(current: Element = el, previous: List[Element], until: (Element) => Boolean): List[Element] =  current.previousElementSibling match {
    case null => previous.reverse
    case value if until(value) || js.isUndefined(value) => previous.reverse
    case value => previousUntil(value, value::previous, until)
  }

  @tailrec final def previousWhile(current: Element = el, previous: List[Element], whileCondition: (Element) => Boolean): List[Element] = current.previousElementSibling match {
    case null => previous.reverse
    case value if whileCondition(value)=> previousWhile(value, value::previous, whileCondition)
    case value => previous.reverse
  }

}

class ExtendedSVGElement(val el: SVGElement) extends AnyVal with NodeOps with ExtendedElement
{

  def fromParent[TOut](matcher: PartialFunction[Node,TOut]):Option[TOut] = super.fromParent(el)(matcher)

  def updateIfExist(key:String,value:js.Any) = if(el.hasOwnProperty(key) && el.dyn.selectDynamic(key)!=value)
    el.dyn.updateDynamic(key)(value)

  def parentElement: Element = this.fromParent[Element](el){case p:Element=>p}.get//el.ownerSVGElement

  def style: CSSStyleDeclaration = el.dyn.style.asInstanceOf[CSSStyleDeclaration]

  def display_=(value:String):Unit = this.style.display = value

  def display: String = this.style.display

  def hide() = display ="none"

  def selectTagByAttribute(tag: String, attribute: String, value: String): Element = el.querySelector(s"""${tag}[${attribute}="${value}"]""")

  def selectAllTagsByAttribute(tag: String, attribute: String, value: String): NodeList = el.querySelectorAll(s"""${tag}[${attribute}="${value}"]""")

  def selectByAttribute(attribute: String, value: String): Element = el.querySelector(s"""[${attribute}="${value}"]""")

  def selectAllByAttribute(attribute: String, value: String): NodeList = el.querySelectorAll(s"""[${attribute}="${value}"]""")

  def children: HTMLCollection = el.children

  def selectByClass(classname: String): Element = el.querySelector(s".$classname")

  def selectAllByClass(classname: String): NodeList = el.querySelectorAll(s".$classname")

  @tailrec final def nextAll(current: Element = el, previous: List[Element] = Nil): List[Element] = current.nextElementSibling match {
    case null => previous.reverse
    case value if js.isUndefined(value) => previous.reverse
    case value => nextAll(value, value::previous)
  }

  @tailrec final def nextUntil(current: Element = el, previous: List[Element], until: (Element) => Boolean): List[Element] = current.nextElementSibling match {
    case null => previous.reverse
    case value if until(value) || js.isUndefined(value) => previous.reverse
    case value => nextUntil(value, value::previous, until)
  }

  @tailrec final def nextWhile(current: Element = el, previous: List[Element], whileCondition: (Element) => Boolean): List[Element] = current.nextElementSibling match {
    case null => previous.reverse
    case value if js.isUndefined(value) => previous.reverse
    case value if whileCondition(value)=> nextWhile(value, value::previous, whileCondition)
    case value => previous.reverse
  }

  @tailrec final def previousAll(current: Element = el, previous: List[Element]): List[Element] = current.previousElementSibling match {
    case null => previous.reverse
    case value if js.isUndefined(value) => previous.reverse
    case value => previousAll(value, value::previous)
  }

  @tailrec final def previousUntil(current: Element = el, previous: List[Element], until: (Element) => Boolean): List[Element] =  current.previousElementSibling match {
    case null => previous.reverse
    case value if until(value) || js.isUndefined(value) => previous.reverse
    case value => previousUntil(value, value::previous, until)
  }

  @tailrec final def previousWhile(current: Element = el, previous: List[Element], whileCondition: (Element) => Boolean): List[Element] = current.previousElementSibling match {
    case null => previous.reverse
    case value if whileCondition(value) => previousWhile(value, value :: previous, whileCondition)
    case value => previous.reverse
  }
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

  def hide(): Unit

  def selectTagByAttribute(tag: String, attribute: String, value: String): Element

  def selectAllTagsByAttribute(tag: String, attribute: String, value: String): NodeList

  def selectByAttribute(attribute: String, value: String): Element

  def selectAllByAttribute(attribute: String, value: String): NodeList

  def selectByClass(classname: String): Element

  def selectAllByClass(classname: String): NodeList

  def children: HTMLCollection

  def nextAll(current: Element, previous: List[Element] = Nil): List[Element]

  def nextUntil(current: Element, previous: List[Element] = Nil, until: Element => Boolean): List[Element]

  def nextWhile(current: Element, previous: List[Element] = Nil, whileCondition: Element => Boolean): List[Element]

  def previousAll(current: Element, previous: List[Element] = Nil): List[Element]

  def previousUntil(current: Element, previous: List[Element] = Nil, until: Element => Boolean): List[Element]

  def previousWhile(current: Element, previous: List[Element] = Nil, whileCondition: Element => Boolean): List[Element]

}

