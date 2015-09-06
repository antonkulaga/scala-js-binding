package org.denigma.binding.views

import org.scalajs.dom
import org.scalajs.dom.raw.HTMLElement

import scala.collection.immutable.Map
import scala.concurrent.{Future, Promise}
import org.scalajs.dom.ext._

/**
 * View that supports resolving some data from params as well as pattern matching on parents and events
 */
abstract class ReactiveView extends OrganizedView with BubbleView  {

  def params:Map[String,Any]

  protected def resolveMyKey[Result](key:String)(fun:PartialFunction[Any,Result]) = params.get(key).collectFirst(fun)

  def resolveKeyOption[Result](key:String, who:ReactiveView = this)(fun:PartialFunction[Any,Result]):Option[Result] = {
    who.resolveMyKey(key)(fun) match {
      case None=>
        who.nearestParentOf{case p:ReactiveView=>p.resolveKeyOption(key)(fun)}.flatten
      case other=> other
    }
  }

  /**
   * Resolves mandatory keys from either this view or parent view
   * @param key
   * @param fun
   * @tparam Result
   * @return
   */
  def resolveKey[Result](key:String)(fun:PartialFunction[Any,Result]):Result = this.resolveKeyOption(key)(fun) match {
    case Some(res) =>res
    case None=>
      val message = s"cannot find appropriate value for mandatory param $key in view $id with the following HTML:\n${elem.outerHTML}\n"
      dom.console.error(message)
      throw new Error(message)
  }

}
