package org.denigma.preview

import org.denigma.binding.binders.Events
import org.denigma.binding.extensions._
import org.denigma.binding.macroses.ClassToMap
import org.denigma.binding.views.BindableView
import org.denigma.binding.views.collections.MapCollectionView
import org.denigma.controls.binders.CodeBinder
import org.scalajs.dom
import org.scalajs.dom.MouseEvent
import org.scalajs.dom.raw.HTMLElement
import rx._
import rx.core.Var

import scala.collection.immutable.Map
import scala.util.Random
import scalatags.generic.TypedTag
import scalatags.JsDom.all._

/**
 * Bind slide
 * @param elem html element
 * @param params
 */
class BindSlide(val elem:HTMLElement,val params:Map[String,Any] = Map.empty[String,Any]) extends BindableView{


  override def activateMacro(): Unit = { extractors.foreach(_.extractEverything(this))}


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
      |class RandomView(val elem:HTMLElement, val params:Map[String,Any]) extends BindableView{
      |
      |
      |  val counting: Var[Tag] = Var{
      |
      |    div(a.`class`:= "ui segment",
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
      |    val value =  div(a.`class`:="ui segment",
      |      h1("This is title"),
      |      p(s"value that changes: \"${list(Random.nextInt(list.length))}\" ")
      |    )
      |    counting() = value
      |
      |  }
      |
      |    override def activateMacro(): Unit = { extractors.foreach(_.extractEverything(this))}
      |
      |  override protected def attachBinders(): Unit = binders = BindableView.defaultBinders(this)
      |
      |
      |  dom.setInterval(update _, 100)
      |
      |  /** Computes the square of an integer.
      |    *  This demonstrates unit testing.
      |    */
      |  def square(x: Int): Int = x*x
      |
      |}
    """.stripMargin

  }

  override protected def attachBinders(): Unit =this.withBinders(new CodeBinder(this))
}


class CollectionSlide(val elem:HTMLElement,val params:Map[String,Any] = Map.empty[String,Any]) extends BindableView{


  override def name = "COLLECTION_SLIDE"

  val code = Var("")

  override def activateMacro(): Unit = { extractors.foreach(_.extractEverything(this))}

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

  override protected def attachBinders(): Unit = this.withBinders(new CodeBinder(this))


}



/**
 * Slide about RDF-related binding
 * @param elem html element to which view is attached
 * @param params
 */
class SparqlSlide(val elem:HTMLElement,val params:Map[String,Any] = Map.empty[String,Any]) extends BindableView
{

    override def activateMacro(): Unit = { extractors.foreach(_.extractEverything(this))}
  val text = Var("")


  val input = Var("01.01.2010")
  val tree = Rx {
    // new Calculator(input()).InputLine.run().map(i=>i.toString).getOrElse("failure") // evaluates to `scala.util.Success(2)`
    //  new DateParser(input()).InputLine.run().map(i=>i.toString).getOrElse("failure")
    ""
  }
  override protected def attachBinders(): Unit = withBinders( new CodeBinder(this)::BindableView.defaultBinders(this) )


}

/**
 * For test purposes only
 */
class RandomView(val elem:HTMLElement, val params:Map[String,Any]) extends BindableView{


  val counting = Var{
    div(`class`:= "ui segment",
      h1("This is title"),
      p("value that changes: 'START'")
    )
  }

  val foo= Var{"Foo variable text"}
  val bar = Var{"Bar variable text"}


  val list = List("ONE","TWO","THREE","FOUR","SOME TEXT","THAT IS RANDOM")

  def update():Unit ={
    val value =  div(`class`:="ui segment",
      h1("This is title"),
      p(s"value that changes: '${list(Random.nextInt(list.length))}' ")
    )
    counting() = value

  }

  override def activateMacro(): Unit = { extractors.foreach(_.extractEverything(this))}

  override protected def attachBinders(): Unit = binders = BindableView.defaultBinders(this)


  dom.setInterval(update _, 100)

  /** Computes the square of an integer.
    *  This demonstrates unit testing.
    */
  def square(x: Int): Int = x*x

}

/**
 * Class for testing purposes that makes a long list out of test element
 */
class LongListView(element:HTMLElement, params:Map[String,Any]) extends MapCollectionView(element,params){



  val items: Var[List[Map[String, Any]]] = Var{
    List(
      Map("prop"->"value1"),Map("prop"->"value2"),Map("prop"->"value3"),Map("prop"->"value4"),Map("prop"->"value5")
    )

  }

  override def activateMacro(): Unit = { extractors.foreach(_.extractEverything(this))}

  override protected def attachBinders(): Unit = binders = BindableView.defaultBinders(this)

}

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


