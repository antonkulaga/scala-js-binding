package org.denigma.binding.frontend.slides

import org.scalajs.dom.{MouseEvent, HTMLElement}
import org.denigma.views.OrdinaryView
import rx._
import scalatags._

/**
 * Bind slide
 * @param element
 * @param params
 */
class BindSlide(element:HTMLElement,params:Map[String,Any] = Map.empty[String,Any]) extends OrdinaryView("slide",element){
  override def tags: Map[String, Rx[HtmlTag]] = this.extractTagRx(this)

  override def strings: Map[String, Rx[String]] = this.extractStringRx(this)

  override def bools: Map[String, Rx[Boolean]] = this.extractBooleanRx(this)

  override def mouseEvents: Map[String, Var[MouseEvent]] = this.extractMouseEvens(this)

  override def bindView(el:HTMLElement) {
    //jQuery(el).slideUp()
    super.bindView(el)

  }

  val html_code = Var{
    """
      |<section class="ui" data-view="random">
      |
      |    <section data-html="counting"></section>
      |    <h1 class="ui purple large header" data-bind="bar"></h1>
      |
      |    <div class="ui two column grid">
      |        <section class="row">
      |            <div class="column">
      |                <input class="ui input" data-bind="foo">
      |                <textarea class="ui textarea" data-bind="foo"></textarea>
      |            </div>
      |            <div class="column">
      |                <textarea class="ui textarea" data-bind="bar"></textarea>
      |                <input class="ui input"  data-bind="bar">
      |            </div>
      |
      |        </section>
      |
      |
      |
      |    </div>
      |
      |    <h1 class="ui teal large header" data-bind="foo"></h1>
      |
      |
      |</section>
    """.stripMargin
  }

  val scala_code = Var{
    """
      |/**
      | * For test purposes only
      | */
      |class RandomView(el:HTMLElement, params:Map[String,Any]) extends OrdinaryView("random",el){
      |
      |  val counting: Var[HtmlTag] = Var{
      |    div(`class`:="ui segment",
      |      h1("This is title"),
      |      p("value that changes: \"START\"")
      |    )
      |  }
      |
      |  val foo= Var{"Foo variable text"}
      |  val bar = Var{"Bar variable text"}
      |
      |
      |  val list = List("ONE","TWO","THREE","FOUR","SOME TEXT","THAT IS RANDOM")
      |
      |  def update():Unit ={
      |    val value =  div(`class`:="ui segment",
      |      h1("This is title"),
      |      p(s"value that changes: \"${list(Random.nextInt(list.length))}\"")
      |    )
      |    counting() = value
      |
      |  }
      |
      |
      |  dom.setInterval(update _, 100)
      |
      |  /** Computes the square of an integer.
      |    *  This demonstrates unit testing.
      |    */
      |  def square(x: Int): Int = x*x
      |
      |  lazy val tags: Map[String, Rx[HtmlTag]] = this.extractTagRx(this)
      |
      |  //val doubles: Map[String, Rx[Double]] = this.extractDoubles[this.type]
      |
      |  lazy val strings: Map[String, Rx[String]] = this.extractStringRx(this)
      |
      |  lazy val bools: Map[String, Rx[Boolean]] = this.extractBooleanRx(this)
      |
      |  //override def textEvents:Map[String, rx.Var[TextEvent]] = this.extractTextEvents(this)
      |
      |  override def mouseEvents: Map[String, rx.Var[MouseEvent]] = this.extractMouseEvens(this)
      |}
      |
    """.stripMargin

  }



}
