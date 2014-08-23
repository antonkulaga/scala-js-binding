package org.denigma.binding.frontend.slides

import org.denigma.binding.extensions._
import org.denigma.binding.views.OrdinaryView
import org.denigma.controls.general.CodeMirrorView
import org.scalajs.dom
import org.scalajs.dom.{HTMLElement, MouseEvent}
import rx._

import scalatags.Text._

class CollectionSlide(val elem:HTMLElement,val params:Map[String,Any] = Map.empty[String,Any]) extends OrdinaryView{


  override def tags: Map[String, Rx[Tag]] = this.extractTagRx(this)

  override def strings: Map[String, Rx[String]] = this.extractStringRx(this)

  override def bools: Map[String, Rx[Boolean]] = this.extractBooleanRx(this)

  override def mouseEvents: Map[String, Var[MouseEvent]] = this.extractMouseEvents(this)

  override def bindView(el:HTMLElement) {
    //jQuery(el).slideUp()
    super.bindView(el)
  }

  val apply = Var(this.createMouseEvent())
  this.apply.handler {
    this.collectFirstView{case v:CodeMirrorView=>v.code.now} match {
      case Some(code)=>
        this.findView("testmenu") match {
          case Some(view:OrdinaryView)=>this.parseHTML(code).foreach(r=>view.refreshMe(r))
          case _=>this.error("test menu not found")
        }
      case _=>error("no codemirror view found")
    }



   }




}
