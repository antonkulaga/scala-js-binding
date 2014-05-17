package org.denigma.commons

import scalatags.{Mod, Attr, Modifier, HtmlTag}
import scala.util.Random
import scalatags.all._
import org.scalajs.dom
import scala.scalajs.js

/**
 * A minimal binding between Scala.Rx and Scalatags and Scala-Js-Dom
 */
object Framework {


  /**
   * Sticks an ID to a HtmlTag if it doesnt already have one, so we can refer
   * to it later.
   */
  class DomRef[T](r0: HtmlTag) extends Modifier{
    val elemId = r0.attrs.getOrElse("id", ""+Random.nextInt())
    val r = r0(id := elemId)
    def transform(tag: HtmlTag): HtmlTag = {
      tag(r)
    }

    def transforms = ??? //do something for trasnformation

  }

  implicit def derefDomRef[T](d: DomRef[T]) = {
    dom.document.getElementById(d.elemId).asInstanceOf[T]
  }

  /**
   * Lets you stick Scala callbacks onto onclick and other onXXXX
   * attributes using Scala callbacks, monkey-patching the callback
   * object onto the element itself to avoid it getting prematurely
   * garbage-collected.
   */
  implicit class Transformable(a: Attr){
    class CallbackModifier(a: Attr, func: () => Unit) extends Modifier{
      def transform(tag: HtmlTag): HtmlTag = {
        val elemId = tag.attrs.getOrElse("id", ""+Random.nextInt())
        val funcName = a.name + "Func"
            
        dom.setTimeout(() => {
          val target = dom.document
                          .getElementById(elemId)
                          .asInstanceOf[js.Dynamic]
          if (target != null)
            target.updateDynamic(funcName)(func: js.Function0[Unit])
        }, 10)
        tag(id:=elemId, a:=s"this.$funcName(); return false;")
      }

      def transforms = ??? //do something for trasnformation
    }
    def <~ (func: => Unit) = new CallbackModifier(a, () => func)
  }

}
