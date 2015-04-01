package org.denigma.semantic.shapes


import org.denigma.binding.views._
import org.scalajs.dom.raw.HTMLElement
import org.scalax.semweb.shex.ArcRule
import org.scalax.semweb.shex._
import rx._

object ValuesClassView {



}

class ValuesClassView(val elem:HTMLElement,val params:Map[String,Any]) extends BindableView
{

  val arc = this.resolveKey("item"){case k:Var[ArcRule]=>k}

  override protected def attachBinders(): Unit = {

  }

  /**
   * is used to fill in all variables extracted by macro
   * usually it is just
   * this.extractEverything(this)
   */
  override def activateMacro(): Unit = {

  }
}
