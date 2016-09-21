package org.denigma.binding

import java.nio.ByteBuffer

import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.dom.ext.EasySeq
import org.scalajs.dom.raw.{Blob, HTMLElement, Node, ProgressEvent, SVGElement, Selection}
import rx.Rx

import scala.collection.immutable.Map
import scala.concurrent.{Future, Promise}
import scala.language.implicitConversions
import scala.scalajs.js.typedarray.{ArrayBuffer, TypedArrayBuffer}

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

  implicit class ClientRectExt(rect: ClientRect) {

    def intersects(other: ClientRect): Boolean = {
      rect.left <= other.right && rect.right >= other.left &&
        rect.top <= other.bottom && rect.bottom >= other.top
    }

  }

  implicit class BoundingExt(element: Element) {

    def intersects(other: Element): Boolean = {
      val rect = element.getBoundingClientRect()
      val otherRect = other.getBoundingClientRect()
      //println(s"RECT: left(${rect.left}) top(${rect.top}) right(${rect.right}) bottom(${rect.bottom})")
      //println(s"OTHER: left(${otherRect.left}) top(${otherRect.top}) right(${otherRect.right}) bottom(${otherRect.bottom})")
      rect.intersects(otherRect)
    }

  }

  implicit def timers[T](source: Rx[T]): TimerExtensions[T] = {
    new TimerExtensions(source)
  }

  implicit class SelectionOps(selection: Selection) extends EasySeq[org.scalajs.dom.raw.Range](selection.rangeCount, i => selection.getRangeAt(i))

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

  implicit class FileListExt(files: FileList) extends EasySeq[File](files.length, files.item)

  implicit class BlobOps(blob: Blob) {

    def readAsByteBuffer: Future[ByteBuffer] = {
      val result = Promise[ByteBuffer]
      val reader = new FileReader()
      def onLoadEnd(ev: ProgressEvent): Any = {
        val buff = reader.result.asInstanceOf[ArrayBuffer]
        val bytes = TypedArrayBuffer.wrap(buff)
        result.success(bytes)
      }
      def onErrorEnd(ev: Event): Any = {
        result.failure(new Exception("READING FAILURE " + ev.toString))
      }
      reader.onloadend = onLoadEnd _
      reader.onerror = onErrorEnd _
      reader.readAsArrayBuffer(blob)
      result.future
    }
  }

}
