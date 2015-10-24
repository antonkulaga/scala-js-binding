package org.denigma.preview
import org.denigma.binding.views.BindableView
import org.scalajs.dom.raw.Element
import rx.core.{Rx, Var}


/**
 * Bind slide
 * @param elem html element
 */
class BindSlide(val elem:Element) extends BindableView{

  val flowScala: Var[String] = Var(
    """
      |  val a = Var(1)
      |  val b = Var(2)
      |  val c: Rx[Int] = Rx{ a() + b() }
      |  val d = Rx{ c() * 5 }
      |  val e = Rx{ c() + 4 }
      |  val f = Rx{ d() + e() + 4 }
    """.stripMargin
  )

  val flowViewScala: Var[String] = Var(
    """
      |class ExampleCodeView(val elem:Element) extends BindableView{
      |  val a = Var(1)
      |  val b = Var(2)
      |  val c: Rx[Int] = Rx{ a() + b() }
      |  val d = Rx{ c() * 5 }
      |  val e = Rx{ c() + 4 }
      |  val f = Rx{ d() + e() + 4 }
      |  }
    """.stripMargin
  )

  val registration = Var(
    """
      |@JSExport("FrontEnd")
      |object FrontEnd extends BindableView with scalajs.js.JSApp
      |{
      | //attaches main view to body HTMLElement
      | lazy val elem: Element = dom.document.body
      |
      | //injector is responsible for child views registration and initialization
      | override lazy val injector = defaultInjector
      |    .register("ExampleCodeView"){
      |      case (el,args)=>new BindSlide(el).withBinder(new GeneralBinder(_))
      |    }
      | //as our MainView is also ScalaJS Application we create main method
      |  @JSExport
      |  def main(): Unit = {
      |    //starts binding
      |    //note: in non-main views we do no need to call this method,
      |    //injector does it for us
      |    this.bindView()
      |  }
      | }
    """.stripMargin)


  val flowHTML = Var(
  """
    |                <section class="ui equal width grid" data-view="CodeExampleView" >
    |                    <div class="ui row">
    |                        <section class="ui column ">
    |                            <div class="ui segment">
    |                                <div class="ui label">a = </div>
    |                                <input data-bind="a" size="6">
    |                            </div>
    |                            <div class="ui segment">
    |                                <div class="ui label">b = </div>
    |                                <input data-bind="b" size="6">
    |                            </div>
    |                        </section>
    |                    <section class="ui column ">
    |                        <div class="ui segment">
    |                            <div class="ui label">c = a + b</div>
    |                            <div class="ui label" data-bind="c"></div>
    |                        </div>
    |                    </section>
    |
    |                    <section class="ui column ">
    |                        <div class="ui circular segment">
    |                            <div class="ui label">d = c * 5</div>
    |                            <div class="ui label" data-bind="d"></div>
    |                        </div>
    |                        <br>
    |                        <div class="ui circular segment">
    |                            <div class="ui label">e = c + 4 </div>
    |                            <div class="ui label" data-bind="e"></div>
    |                        </div>
    |                    </section>
    |                    <section class="ui column  middle aligned">
    |                        <div class="ui circular segment">
    |                            <div class="ui label">f = d + e + 4</div>
    |                            <div class="ui label" data-bind="f"></div>
    |                        </div>
    |                    </section>
    |                    </div>
    |                </section>
  """.stripMargin
  )

  val a = Var(1)
  val b = Var(2)
  val c: Rx[Int] = Rx{ a() + b() }
  val d = Rx{ c() * 5 }
  val e = Rx{ c() + 4 }
  val f = Rx{ d() + e() + 4 }

}
