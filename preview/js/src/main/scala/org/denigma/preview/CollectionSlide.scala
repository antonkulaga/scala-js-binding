package org.denigma.preview
import org.denigma.binding.binders.Events
import org.denigma.binding.extensions._
import org.denigma.binding.macroses.ClassToMap
import org.denigma.binding.views.{BindableView, MapCollectionView}
import org.scalajs.dom
import org.scalajs.dom.html.Input
import org.scalajs.dom.raw.{HTMLElement, KeyboardEvent}
import rx._
import rx.core.Var

import scala.collection.immutable.Map
import scala.util.Random
import scalatags.JsDom.all._


/**
 * Created by antonkulaga on 8/25/15.
 */
class CollectionSlide(val elem:HTMLElement,val params:Map[String,Any] = Map.empty[String,Any]) extends BindableView{


  override def name = "COLLECTION_SLIDE"

  val code = Var("")



  val apply = Var(Events.createMouseEvent())
  this.apply.handler {
      this.findView("testmenu") match {
        case Some(view:BindableView)=>
          dom.console.log("ID IS = "+view.id)
          dom.console.log("HTML is = "+view.elem.outerHTML)

          this.parseHTML(code.now).foreach{case c=>
            dom.console.log("CODE NOW IS"+code.now)
            dom.console.log("CODE HTML"+c.outerHTML)
            view.refreshMe(c)
          }
        case _=>dom.console.error("test menu not found")
  }
  }

}
