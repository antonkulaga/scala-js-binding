package org.denigma.preview
import org.denigma.binding.views.BindableView
import org.scalajs.dom.raw.Element
import rx.core.Var


/**
 * Bind slide
 * @param elem html element
 */
class BindSlide(val elem:Element) extends BindableView{


  val html = Var("")

  val scala_code = Var(
    """
      |class RandomView(val elem:Element, val params:Map[String,Any]) extends BindableView
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
