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
class BindSlide(val elem:HTMLElement,val params:Map[String,Any] = Map.empty[String,Any]) extends BindableView{


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



/**
 * Slide about RDF-related binding
 * @param elem html element to which view is attached
 * @param params
 */
class SparqlSlide(val elem:HTMLElement,val params:Map[String,Any] = Map.empty[String,Any]) extends BindableView
{


  val text = Var("")


  val input = Var("01.01.2010")
  val tree = Rx {
    // new Calculator(input()).InputLine.run().map(i=>i.toString).getOrElse("failure") // evaluates to `scala.util.Success(2)`
    //  new DateParser(input()).InputLine.run().map(i=>i.toString).getOrElse("failure")
    ""
  }

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
  dom.console.log(result.toString)



}


class Test(val elem:HTMLElement, val params:Map[String,Any]) extends BindableView{


  protected def onKeyChange(fun:Input=>Unit)(k:KeyboardEvent) =  k.target match {
      case n:dom.html.Input if k.currentTarget==k.target=> fun(n)
      case other=> //nothing
    }



  def onChange(input:dom.html.Input) = {
    var (oldvalue,newvalue) = ("","")
    input.onkeydown = onKeyChange(input=>oldvalue=input.value) _
    input.onkeyup = onKeyChange{input=>
      oldvalue=input.value
      dom.console.log(s"VALUES = $oldvalue and $newvalue")
    } _
  }

  override def bindView(el:HTMLElement) = {
    super.bindView(el)
    dom.console.log("let us start!")
    sq.byId("txt") match {
      case Some(input:dom.html.Input)=>
        onChange(input)
      case other=>dom.console.error("cannot find txt")
    }

  }



}


