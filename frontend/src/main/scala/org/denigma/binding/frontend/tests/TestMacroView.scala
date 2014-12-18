package org.denigma.binding.frontend.tests

import org.denigma.binding.macroses.ClassToMap
import org.denigma.binding.views.BindableView
import org.denigma.binding.views.collections.MapCollectionView
import org.scalajs.dom.HTMLElement
import org.scalajs.dom
import rx._

import scala.collection.immutable.Map

class TestMacroView(val elem:HTMLElement, val params:Map[String,Any]) extends BindableView{

  case class HelloWorld(hello:String){

    val one = "ONE"
    val two = "TWO"
    val three = "THREE"

    val num = 12345678
  }
  val h = HelloWorld("WORLD")
  val result = implicitly[ClassToMap[HelloWorld]].asMap(h)
  dom.alert(result.toString)

  override def activateMacro(): Unit = { extractors.foreach(_.extractEverything(this))}

  override protected def attachBinders(): Unit = binders = BindableView.defaultBinders(this)

}
