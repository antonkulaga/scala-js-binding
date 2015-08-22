package org.denigma.binding.binders.extractors

import org.denigma.binding.binders.BasicBinder
import org.denigma.binding.macroses.TagRxMap
import org.scalajs.dom.raw.HTMLElement
import rx._

import scala.collection.immutable._
import scalatags.Text.Tag

/**
 * HTML binding to scalatagnode
 */
trait ScalaTagsBinder extends BasicBinder{

   def tags:Map[String,Rx[Tag]]

  //def extractTags[T]:Map[String,Rx[Tag]] = macro Binder.htmlBindings_impl[T]
  //def extractTagRx[T: TagRxMap](t: T) =  implicitly[TagRxMap[T]].asTagRxMap(t)


  def bindHTML(el:HTMLElement,ats:Map[String, String]) =
    ats.get("html").flatMap(value=>this.tags.get(value).map(v=>(value,v))).foreach{case (key,rx)=>
      this.updateAttrByRx(key,el,rx)
    }

  /**
   * Updates attribute by ScalaTag
   * @param key key
   * @param el element to update
   * @param rtag tag to update from
   * @return
   */
  def updateAttrByRx(key:String,el:HTMLElement ,rtag:Rx[Tag]) = this.bindRx[Tag](key,el,rtag){
    case (elem,tg)=>
    elem.innerHTML = tg.render
  }

}
