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
 * Bind slide
 * @param elem html element
 * @param params
 */
class BindSlide(val elem:HTMLElement) extends BindableView{


  val html = Var("")

  val scala_code = Var(
    """
      |class RandomView(val elem:HTMLElement, val params:Map[String,Any]) extends BindableView
      |{
      |    val counting = Var{
      |    div(`class`:= "ui segment",
      |        h1("This is title"),
      |        p("value that changes: 'START'")
      |        )
      |    }
      |
      |    val foo= Var{"Foo variable text"}
      |    val bar = Var{"Bar variable text"}
      |
      |
      |    val list = List("ONE","TWO","THREE","FOUR","SOME TEXT","THAT IS RANDOM")
      |
      |    def update():Unit ={
      |        val value =  div(`class`:="ui segment",
      |            h1("This is title"),
      |            p(s"value that changes: '${list(Random.nextInt(list.length))}' ")
      |        )
      |        counting() = value
      |    }
      |
      |    dom.setInterval(update _, 100)
      |
      |    /** Computes the square of an integer.
      |    *  This demonstrates unit testing.
      |    */
      |    def square(x: Int): Int = x*x
      |}
    """.stripMargin)

}
