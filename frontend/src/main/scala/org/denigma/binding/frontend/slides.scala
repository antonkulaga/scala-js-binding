package org.denigma.binding.frontend

import org.denigma.binding.extensions._
import org.denigma.binding.binders.extractors.EventBinding
import org.denigma.binding.views.OrdinaryView
import org.denigma.controls.editors.editors
import org.denigma.controls.general.CodeMirrorView
import org.denigma.controls.graph.GraphView
import org.denigma.semantic.binding
import org.denigma.semantic.binding.ModelCollection
import org.denigma.semantic.controls._
import org.scalajs.dom
import org.scalajs.dom.{HTMLElement, MouseEvent, TextEvent}
import org.scalax.semweb.rdf.vocabulary.WI
import org.scalax.semweb.rdf.{IRI, StringLiteral}
import org.scalax.semweb.shex.PropertyModel
import rx._
import rx.core.Var

import scala.collection.immutable.Map
import scala.scalajs.js



/**
 * Bind slide
 * @param elem html element
 * @param params
 */
class BindSlide(val elem:HTMLElement,val params:Map[String,Any] = Map.empty[String,Any]) extends OrdinaryView{


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
      |class RandomView(el:HTMLElement, params:Map[String,Any]) extends OrdinaryView("random",el){
      |
      |  val counting: Var[Tag] = Var{
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
      |  lazy val tags: Map[String, Rx[Tag]] = this.extractTagRx(this)
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


class CollectionSlide(val elem:HTMLElement,val params:Map[String,Any] = Map.empty[String,Any]) extends OrdinaryView{


    override def activateMacro(): Unit = { extractors.foreach(_.extractEverything(this))}

  val apply = Var(EventBinding.createMouseEvent())
  this.apply.handler {
    this.collectFirstView{case v:CodeMirrorView=>v.code.now} match {
      case Some(code)=>
        this.findView("testmenu") match {
          case Some(view:OrdinaryView)=>this.parseHTML(code).foreach(r=>view.refreshMe(r))
          case _=>this.error("test menu not found")
        }
      case _=>error("no codemirror view found")
    }



  }




}



class GraphSlide(val elem:HTMLElement, val params:Map[String,Any]) extends GraphView
{


  lazy val path: String = this.params.get("path").map(_.toString).get

  lazy val resource = this.params.get("resource").map(v=>IRI(v.toString)).get

  //require(params.contains("path"))


    override def activateMacro(): Unit = { extractors.foreach(_.extractEverything(this))}




  override def bindView(el:HTMLElement) = {
    super.bindView(el)
    this.draw()
    //jQuery(el).slideUp()
    //    super.bindView(el)
    //    Sigma.utils.pkg("sigma.canvas.edges")
    //    this.sigma =  new Sigma(initial)
    //    this.storage.explore(this.resource).onComplete{
    //      case Success(data) =>
    //        this.loadData(data)
    //      case Failure(th)=>
    //        this.error(s"failure in read of model for $path: \n ${th.getMessage} ")
    //    }

  }


  protected def draw() = {
    js.eval (
      """
        |new Drawing.SimpleGraph({layout: '3d', numNodes: 10, showLabels:true, graphLayout:{attraction: 5, repulsion: 0.5}, showStats: true, showInfo: true})
      """.stripMargin)

  }


  //  override protected def loadData(data:List[Quad]) = {
  //    super.loadData(data)
  //    sigma.startForceAtlas2()
  //  }

  override def container: HTMLElement = dom.document.getElementById("graph-container")

}





class PageEditView(val elem:HTMLElement,val params:Map[String,Any]) extends AjaxLoadView with EditModelView
{


  this.saveClick.takeIf(dirty).handler{
    //dom.console.log("it should be saved right now")
    this.saveModel()
  }


  this.toggleClick.handler{
    this.editMode() = !this.editMode.now
  }

  val editor = Var("ckeditor")

  override def activateMacro(): Unit = { extractors.foreach(_.extractEverything(this))}


}
/**
 * Slide about RDF-related binding
 * @param elem html element to which view is attached
 * @param params
 */
class RdfSlide(val elem:HTMLElement,val params:Map[String,Any] = Map.empty[String,Any]) extends OrdinaryView
{

    override def activateMacro(): Unit = { extractors.foreach(_.extractEverything(this))}

}


class RemoteSlide(val elem:HTMLElement,val params:Map[String,Any] = Map.empty[String,Any]) extends OrdinaryView
{


    override def activateMacro(): Unit = { extractors.foreach(_.extractEverything(this))}


}

/**
 * View for article with some text
 */
class SlideView(val elem:HTMLElement,val params:Map[String,Any] = Map.empty[String,Any]) extends OrdinaryView{

    override def activateMacro(): Unit = { extractors.foreach(_.extractEverything(this))}


}
/**
 * Slide about RDF-related binding
 * @param elem html element to which view is attached
 * @param params
 */
class SparqlSlide(val elem:HTMLElement,val params:Map[String,Any] = Map.empty[String,Any]) extends OrdinaryView
{

    override def activateMacro(): Unit = { extractors.foreach(_.extractEverything(this))}


  val input = Var("01.01.2010")
  val tree = Rx {
    // new Calculator(input()).InputLine.run().map(i=>i.toString).getOrElse("failure") // evaluates to `scala.util.Success(2)`
    //  new DateParser(input()).InputLine.run().map(i=>i.toString).getOrElse("failure")
    ""
  }



}

class TestSuggestBinding(val elem:HTMLElement,val params:Map[String,Any] = Map.empty[String,Any]) extends ModelCollection
{

    override def activateMacro(): Unit = { extractors.foreach(_.extractEverything(this))}





  override def bindView(el:HTMLElement) {
    //jQuery(el).slideUp()
    super.bindView(el)
    this.subscribeUpdates()
    //dom.console.log("collection prefixes = " + prefixes.toString())

    val p1 =binding.ModelInside( PropertyModel(IRI("http://suggessions/one"),IRI(WI / "value")->StringLiteral("one")) )
    val p2= binding.ModelInside( PropertyModel(IRI("http://suggessions/one"),IRI(WI / "value")->StringLiteral("two")))
    val p3 =binding.ModelInside( PropertyModel(IRI("http://suggessions/one"),IRI(WI / "value")->StringLiteral("three")))
    val p4 =binding.ModelInside( PropertyModel(IRI("http://suggessions/one"),IRI(WI / "value")->StringLiteral("four")))
    val p5 =binding.ModelInside( PropertyModel(IRI("http://suggessions/one"),IRI(WI / "value")->StringLiteral("five")))
    val p6 =binding.ModelInside( PropertyModel(IRI("http://suggessions/one"),IRI(WI / "value")->StringLiteral("six")))

    this.items() = List(Var(p1),Var(p2),Var(p3),Var(p4),Var(p5),Var(p6))

  }

}

class TableBinder(element:HTMLElement,params:Map[String,Any] = Map.empty[String,Any]) extends AjaxModelCollection("Todos",element,params)
{
    override def activateMacro(): Unit = { extractors.foreach(_.extractEverything(this))}


}


class TestModelView(val elem:HTMLElement,val params:Map[String,Any]) extends AjaxLoadView
{


  val saveClick: Var[MouseEvent] = Var(EventBinding.createMouseEvent())

  this.saveClick.takeIf(dirty).handler{
    //dom.console.log("it should be saved right now")
    this.saveModel()
  }

  override def activateMacro(): Unit = { extractors.foreach(_.extractEverything(this))}


}
