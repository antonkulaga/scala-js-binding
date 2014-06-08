package org.denigma.binding

import rx._
import org.scalajs.dom
import org.scalajs.dom.HTMLElement
import scala.collection.mutable
import scala.collection.immutable._
import org.denigma.extensions._
import org.scalajs.dom.extensions._


import org.denigma.binding.macroses.TagRxMap
import org.denigma.extensions._
import scalatags.Text.{Tag, TypedTag}

/**
 * HTML binding to scalatagnode
 */
trait ScalaTagsBinder extends JustBinding{

  //type Tag = TypedTag[_]
  //TODO: rewrite


  def tags:Map[String,Rx[Tag]]


  //def extractTags[T]:Map[String,Rx[Tag]] = macro Binder.htmlBindings_impl[T]

  def extractTagRx[T: TagRxMap](t: T) =  implicitly[TagRxMap[T]].asTagRxMap(t)


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
  def updateAttrByRx(key:String,el:org.scalajs.dom.HTMLElement ,rtag:Rx[Tag]) = this.bindRx[Tag](key,el,rtag){
    case (elem,tg)=>

//      tg.attrs.foreach {
//        case (k, v) =>
//          val att = (k,v).toAtt
//          elem.attributes.setNamedItem(att)

//    }
    elem.innerHTML = tg.toString()

  }

}
