package org.denigma.preview

import org.denigma.binding.binders.{Events, GeneralBinder}
import org.denigma.binding.views.BindableView
import org.denigma.semantic.binders.binded.Typed
import org.denigma.semantic.binders.{PrefixResolver, RDFModelBinder, SelectableModelBinder}
import org.denigma.semantic.models.ModelView
import org.denigma.semantic.{DefaultPrefixes, WebPlatform}
import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.dom.raw.HTMLElement
import org.w3.banana._
import org.w3.banana.plantain.Plantain
import rx.core.Var

import scala.collection.immutable.Map
import scala.concurrent.Future


/**
 * Slide about Plantain-related binding
 * @param elem html element to which view is attached
 * @param params
 */
class RdfSlide(val elem:HTMLElement,val params:Map[String,Any] = Map.empty[String,Any])
  extends BindableView
{

  override lazy val injector = defaultInjector
    .register("TextModelView"){case (el,args)=>new TextModelView(el,args)}
    .register("Selection"){case (el,args)=>
      val sel = new SelectionView(el,args).withBinder(new GeneralBinder(_))
      println("STRINGS ARE: "+sel.binders.head.asInstanceOf[GeneralBinder[SelectionView,_]].strings.mkString("\n"))
        sel
    }

  val modelCode = Var(
    """
      |class TextModelView(elem:HTMLElement,params:Map[String,Any])
      | (implicit ops:RDFOps[Plantain])
      |  extends ModelView[Plantain](elem,params)(ops)
      |{
      |  lazy val graph: Var[PointedGraph[Plantain]] =
      |   Var(PointedGraph[Plantain](
      |    subject,ops.makeGraph(TestData[Plantain](subject).data))
      |  )
      |  val resolver = new PrefixResolver[Plantain](Var(
      |   new DefaultPrefixes[Plantain]().prefixes)
      |   )
      |  binders = List(new RDFModelBinder[Plantain](this,  graph,  resolver))
      |}
    """.stripMargin)

  val rdfa = Var(
  """
    | <section class="ui blue segment" data-view="TextModelView"
    | vocab="http://webintelligence.eu/platform/"
    | data-param-resource="http://page.org">
    | <p>This model binds to RDF graph</p>
    | <div>
    | <h1 property="title"></h1>
    | <input property="title">
    | <p property="text"></p>
    | <textarea property="text"></textarea>
    |</div>
    |</section>
  """.stripMargin
  )


}

case class TestData[Rdf<:RDF](subject:Rdf#Node)(implicit ops:RDFOps[Rdf]){

  lazy val data = Seq(
    ops.makeTriple(subject,WebPlatform[Rdf](ops)("title"),ops.makeLiteral("Title",ops.xsd.string)),
    ops.makeTriple(subject,WebPlatform[Rdf](ops)("text"),ops.makeLiteral("Some text",ops.xsd.string))
  )

}

class TextModelView(elem:HTMLElement,params:Map[String,Any])(implicit ops:RDFOps[Plantain])
  extends ModelView[Plantain](elem,params)(ops)
{

  override lazy val graph: Var[PointedGraph[Plantain]] =  Var(PointedGraph[Plantain](
    subject,ops.makeGraph(TestData[Plantain](subject).data))
  )

  val resolver = new PrefixResolver[Plantain](Var(new DefaultPrefixes[Plantain]().prefixes))

  binders = List(new RDFModelBinder[Plantain](this,  graph,  resolver))
}

class SelectableModelView(elem:HTMLElement,params:Map[String,Any])(implicit ops:RDFOps[Plantain])
  extends ModelView[Plantain](elem,params)(ops)
{
  import org.denigma.binding.extensions._

  val report = Var(Events.createMouseEvent())
  report.handler{
    dom.console.log("REPORT WORKS")
  }

  private val prefs = new DefaultPrefixes[Plantain]().prefixes

  override lazy val graph: Var[PointedGraph[Plantain]] =  Var(PointedGraph[Plantain](
    subject,ops.makeGraph(TestData[Plantain](subject).data))
  )

  def suggest(tp:Typed[Plantain]): Future[Seq[Plantain#Node]] = Future.successful(

    (1 to scala.util.Random.nextInt(4)+1).map { case num=>
      val str = tp.typed
      //dom.console.log(s"suggestion for $str is ${str}_$num")
      ops.makeLiteral(str+"_"+num,ops.xsd.string)
    })

  val resolver = new PrefixResolver[Plantain](Var(prefs))

  binders = List(new SelectableModelBinder[Plantain](this,  graph,  resolver)(suggest) )

}