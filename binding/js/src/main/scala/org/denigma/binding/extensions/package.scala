package org.denigma.binding

import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.dom.raw.{HTMLElement, SVGElement}
import rx.Rx

import scala.collection.immutable.Map

/**
 * Useful implicit classes
 */
package object extensions extends AttributesOps
  with AnyJsExtensions
  with RxExt
  with Functions
  with EventsOps
  with DataOps
  with MapOps
{

  //implicit def toExtendedElement(el: HTMLElement): ExtendedHTMLElement = new ExtendedHTMLElement(el)

  //implicit def toExtendedElement(el: SVGElement): ExtendedSVGElement = new ExtendedSVGElement(el)

  implicit class OptionOpt[T](source: Option[T]){

    def orError(str: String): Unit = if (source.isEmpty) dom.console.error(str)

  }

  implicit class ThrowableOpt(th: Throwable) {
    def stackString: String = th.getStackTrace.foldLeft("")((acc, el) => acc+"\n"+el.toString)
  }


  implicit class MapOpt[TValue](source: Map[String, TValue]) {

    def getOrError(key: String, inside: String = ""): Option[TValue] = {
      val g = source.get(key)
      val in = if(inside=="") "" else " in "+inside + " "
      if(g.isEmpty) dom.console.error(s"failed to find item with key $key$in, all keys are: [${source.keySet.toList.mkString(", ")}]")
      g
    }
  }


  implicit def extSVG(svg: SVGElement): ExtendedSVGElement = new ExtendedSVGElement(svg)

  implicit def extHTML(el: HTMLElement): ExtendedHTMLElement = new ExtendedHTMLElement(el)

  implicit def extNode(node: Node): ExtendedNode = new ExtendedNode(node)

  implicit def elementWithOps(el: Element): ExtendedElement =  el match {
    case html: HTMLElement => html
    case svg: SVGElement =>   svg
    case other =>
      dom.console.error(s"element ${el.outerHTML} cannot be transformed to extended element")
      ???
  }



}
