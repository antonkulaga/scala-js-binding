package org.denigma.binding.binders.extractors

import org.denigma.binding.binders.BasicBinder
import org.denigma.binding.macroses.TagRxMap
import org.scalajs.dom.raw.HTMLElement
import rx._
import rx.ops._
import scala.collection.immutable._
import scalatags.Text.Tag
import org.denigma.binding.extensions._
/**
 * HTML binding to scalatagnode
 */
trait ScalaTagsBinder extends BasicBinder{

   def tags:Map[String,Rx[Tag]]

  //def extractTags[T]:Map[String,Rx[Tag]] = macro Binder.htmlBindings_impl[T]
  //def extractTagRx[T: TagRxMap](t: T) =  implicitly[TagRxMap[T]].asTagRxMap(t)

  def bindHTML(el:HTMLElement,ats:Map[String, String]) ={
    for{
      a<-ats.get("html")
      tg <-tags.getOrError(a)
    }{
      ifNoID(el,"tag_"+tg)
      tg.foreach{  t=> el.innerHTML = t.render  }
    }
  }

}
