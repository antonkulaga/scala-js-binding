package org.denigma.binding.binders.extractors

import org.denigma.binding.binders.ReactiveBinder
import org.denigma.binding.macroses.TagRxMap
import rx._

import scala.collection.immutable._
import scalatags.Text
import scalatags.Text.Tag
/**
 * HTML binding to scalatagnode
 */
trait ScalaTagsBinder extends ReactiveBinder
{
  def tags: Map[String, Rx[Tag]]

  protected def extractTagRx[T: TagRxMap](t: T): Map[String, Rx[Text.Tag]] =  implicitly[TagRxMap[T]].asTagRxMap(t)

/*  def bindHTML(el: Element, ats: Map[String, String]):Unit  =
    for{
      a<-ats.get("html")
      tg <- tags.getOrError(a)
      t <- tg
    } el.innerHTML = t.render
    */

}
