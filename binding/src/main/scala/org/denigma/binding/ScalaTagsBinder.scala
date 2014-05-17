package org.denigma.binding

import rx._
import scalatags.HtmlTag
import org.scalajs.dom
import org.scalajs.dom.HTMLElement
import scala.collection.mutable
import scala.collection.immutable._
import org.denigma.extensions._
import org.scalajs.dom.extensions._
import org.denigma.binding.macroses.TagRxMap

/**
 * HTML binding to scalatagnode
 */
trait ScalaTagsBinder extends JustBinding{

  //TODO: rewrite


  def tags:Map[String,Rx[HtmlTag]]


  //def extractTags[T]:Map[String,Rx[HtmlTag]] = macro Binder.htmlBindings_impl[T]

  def extractTagRx[T: TagRxMap](t: T) =  implicitly[TagRxMap[T]].asTagRxMap(t)


  def bindHTML(el:HTMLElement,ats:mutable.Map[String, dom.Attr]) =
    ats.get("html").flatMap(value=>this.tags.get(value.value).map(v=>(value.value,v))).foreach{case (key,rx)=>
      this.updateAttrByRx(key,el,rx)
    }

  /**
   * Updates attribute by ScalaTag
   * @param key key
   * @param el element to update
   * @param rtag tag to update from
   * @return
   */
  def updateAttrByRx(key:String,el:org.scalajs.dom.HTMLElement ,rtag:Rx[HtmlTag]) = this.bindRx[HtmlTag](key,el,rtag){
    case (elem,tg)=>
      tg.attrs.foreach {
        case (k, v) =>
          val att = (k,v).toAtt
          elem.attributes.setNamedItem(att)

    }
    elem.innerHTML = tg.toString()

  }

}
