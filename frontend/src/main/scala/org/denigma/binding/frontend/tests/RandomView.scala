package org.denigma.binding.frontend.tests

import org.scalajs.dom.{HTMLDivElement, TextEvent, MouseEvent, HTMLElement}
import rx.core.Var
import scala.util.Random
import rx.Rx
import scala.collection.immutable.Map
import org.scalajs.dom
import org.denigma.views.core.OrdinaryView
import scalatags.Text.tags._
import scalatags.Text.{attrs => a, styles => s, _}
/**
 * For test purposes only
 */
class RandomView(el:HTMLElement, params:Map[String,Any]) extends OrdinaryView("random",el){

  val counting: Var[Tag] = Var{

    div(a.`class`:="ui segment",
      h1()("This is title"),
      p("""value that changes: "START" """)
    )
  }

  val foo= Var{"Foo variable text"}
  val bar = Var{"Bar variable text"}


  val list = List("ONE","TWO","THREE","FOUR","SOME TEXT","THAT IS RANDOM")

  def update():Unit ={
    val value =  div(a.`class`:="ui segment",
      h1("This is title"),
      p(s"""value that changes: "${list(Random.nextInt(list.length))}" """)
    )
    counting() = value

  }


  dom.setInterval(update _, 100)

  /** Computes the square of an integer.
    *  This demonstrates unit testing.
    */
  def square(x: Int): Int = x*x

  lazy val tags: Map[String, Rx[Tag]] = this.extractTagRx(this)

  //val doubles: Map[String, Rx[Double]] = this.extractDoubles[this.type]

  lazy val strings: Map[String, Rx[String]] = this.extractStringRx(this)

  lazy val bools: Map[String, Rx[Boolean]] = this.extractBooleanRx(this)

  //override def textEvents:Map[String, rx.Var[TextEvent]] = this.extractTextEvents(this)

  override def mouseEvents: Map[String, rx.Var[MouseEvent]] = this.extractMouseEvents(this)
}
