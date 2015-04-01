package org.denigma.binding.frontend.slides

import org.denigma.binding.binders.extractors.EventBinding
import org.denigma.binding.extensions._
import org.denigma.binding.views.BindableView
import org.denigma.controls.binders.CodeBinder
import org.denigma.semantic.binders.editable.EditModelBinder
import org.denigma.semantic.models._
import org.denigma.semantic.models.collections.ModelCollection
import org.denigma.semantic.rdf
import org.denigma.semantic.rdf.{ModelInside, ShapeInside}
import org.denigma.semantic.shapes.ShapedModelView
import org.scalajs.dom
import org.scalajs.dom.MouseEvent
import org.scalajs.dom.raw.HTMLElement
import org.scalax.semweb.rdf.vocabulary.WI
import org.scalax.semweb.rdf.{BooleanLiteral, IRI, StringLiteral, vocabulary}
import org.scalax.semweb.shex.{PropertyModel, ShapeBuilder, Star}
import rx._
import rx.core.Var

import scala.collection.immutable.Map


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



  val code = Var("")

  override def activateMacro(): Unit = { extractors.foreach(_.extractEverything(this))}

  val apply = Var(EventBinding.createMouseEvent())
  this.apply.handler {
      this.findView("testmenu") match {
        case Some(view:BindableView)=>this.parseHTML(code.now).foreach(r=>view.refreshMe(r))
        case _=>dom.console.error("test menu not found")
  }



  }

  override protected def attachBinders(): Unit = this.withBinders(new CodeBinder(this))


}


class PageEditView(val elem:HTMLElement,val params:Map[String,Any]) extends RemoteLoadView with EditModelView
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

  override protected def attachBinders(): Unit = binders =  this.withBinders(new EditModelBinder(this,this.model,this.editMode)::new CodeBinder(this)::Nil)
}
/**
 * Slide about RDF-related binding
 * @param elem html element to which view is attached
 * @param params
 */
class RdfSlide(val elem:HTMLElement,val params:Map[String,Any] = Map.empty[String,Any]) extends BindableView
{
  val code = Var("")

    override def activateMacro(): Unit = { extractors.foreach(_.extractEverything(this))}
    override protected def attachBinders(): Unit =  this.withBinders(new CodeBinder(this))

}


class RemoteSlide(val elem:HTMLElement,val params:Map[String,Any] = Map.empty[String,Any]) extends BindableView
{


    override def activateMacro(): Unit = { extractors.foreach(_.extractEverything(this))}
  override protected def attachBinders(): Unit = this.withBinders(new CodeBinder(this))

}

/**
 * View for article with some text
 */
class SlideView(val elem:HTMLElement,val params:Map[String,Any] = Map.empty[String,Any]) extends BindableView{

    override def activateMacro(): Unit = { extractors.foreach(_.extractEverything(this))}
  override protected def attachBinders(): Unit =  this.withBinders(new CodeBinder(this))

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

class TestSuggestBinding(val elem:HTMLElement,val params:Map[String,Any] = Map.empty[String,Any]) extends ModelCollection
{

    override def activateMacro(): Unit = { extractors.foreach(_.extractEverything(this))}


  override protected def attachBinders(): Unit =  this.withBinders(new CodeBinder(this))


  override def bindView(el:HTMLElement) {
    //jQuery(el).slideUp()
    super.bindView(el)
    this.subscribeUpdates()
    //dom.console.log("collection prefixes = " + prefixes.toString())

    val p1 =ModelInside( PropertyModel(IRI("http://suggessions/one"),IRI(WI / "value")->StringLiteral("one")) )
    val p2= rdf.ModelInside( PropertyModel(IRI("http://suggessions/one"),IRI(WI / "value")->StringLiteral("two")))
    val p3 =rdf.ModelInside( PropertyModel(IRI("http://suggessions/one"),IRI(WI / "value")->StringLiteral("three")))
    val p4 =rdf.ModelInside( PropertyModel(IRI("http://suggessions/one"),IRI(WI / "value")->StringLiteral("four")))
    val p5 =rdf.ModelInside( PropertyModel(IRI("http://suggessions/one"),IRI(WI / "value")->StringLiteral("five")))
    val p6 =rdf.ModelInside( PropertyModel(IRI("http://suggessions/one"),IRI(WI / "value")->StringLiteral("six")))

    this.items() = List(Var(p1),Var(p2),Var(p3),Var(p4),Var(p5),Var(p6))

  }

}

class RowView(elem:HTMLElement,params:Map[String,Any] = Map.empty[String,Any]) extends ShapedModelView(elem,params)
{

  override def shapeOption = Some(Var(ShapeInside(taskShape)))

  override def activateMacro(): Unit = { extractors.foreach(_.extractEverything(this))}

  val addClick = Var(EventBinding.createMouseEvent())

  addClick handler {
    //this.addItem()
  }

  val isDirty = Rx{  this.dirty()  }

  override protected def attachBinders(): Unit = {}//binders = RemoteModelView.defaultBinders(this)

  import org.scalax.semweb.rdf.vocabulary._

  def de = IRI("http://denigma.org/resource/")
  def dc = IRI(vocabulary.DCElements.namespace)

  def pmid = IRI("http://denigma.org/resource/Pubmed/")

  def article = de / "Article"
  def authors =  de / "is_authored_by"
  def abs = de / "abstract"
  def published = de / "is_published_in"
  def title = de / "title"
  def excerpt = de / "excerpt"


  def priority = (WI.PLATFORM / "has_priority").iri

  def desc = (WI.PLATFORM / "has_description").iri
  def completed = (WI.PLATFORM / "is_completed").iri
  def assigned = (WI.PLATFORM / "assigned_to").iri
  def task = (WI.PLATFORM / "Task").iri
  def Anton = de / "Anton_Kulaga"

  def Daniel = de / "Daniel_Wuttke"

  lazy val taskIntegrase = PropertyModel(IRI(WI.PLATFORM /"Integrase"), title -> StringLiteral("Find more info"),  desc->StringLiteral("Find other papers on using PhiC31 integrase for genes insertion") , priority-> de / "high", assigned->Anton, completed->BooleanLiteral(false) , RDF.TYPE-> task)


  override def modelOption = Some(Var(ModelInside(taskIntegrase)))

  def taskShape() = {
    val ts = new ShapeBuilder(task)

    //ts has de / "is_authored_by" occurs Star //occurs Plus
    ts has title occurs Star //occurs ExactlyOne
    //art has de / "date" occurs Star //occurs ExactlyOne
    ts has desc of XSD.StringDatatypeIRI occurs Star //occurs Star
    ts has assigned occurs Star //occurs Plus
    ts has completed of XSD.BooleanDatatypeIRI occurs Star //occurs Star
    ts.result
  }


}


class TestModelView(val elem:HTMLElement,val params:Map[String,Any]) extends RemoteLoadView
{


  val saveClick: Var[MouseEvent] = Var(EventBinding.createMouseEvent())

  this.saveClick.takeIf(dirty).handler{
    //dom.console.log("it should be saved right now")
    this.saveModel()
  }

  override def activateMacro(): Unit = { extractors.foreach(_.extractEverything(this))}
  override protected def attachBinders(): Unit = this.withBinders( RemoteModelView.defaultBinders(this))

}
