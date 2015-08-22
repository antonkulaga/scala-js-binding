package org.denigma.preview

import java.net.URI

import org.denigma.binding.binders.Events
import org.denigma.binding.views.BindableView
import org.denigma.controls.binders.CodeBinder
import org.denigma.preview.FrontEnd._
import org.denigma.semantic.binders.binded.Typed
import org.denigma.semantic.binders.{PrefixResolver, SelectableModelBinder, RDFModelBinder}
import org.denigma.semantic.{WebPlatform, DefaultPrefixes}
import org.denigma.semantic.models.ModelView
import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.dom.ext.Ajax
import org.scalajs.dom.raw.HTMLElement
import org.w3.banana.plantain.model.{Literal, Graph}
import org.w3.banana._
import scala.concurrent.Future
import scala.util._
import org.w3.banana.diesel.PointedGraphW
import org.w3.banana.plantain.Plantain
import rx.core.Var
import rx.ops

import scala.Predef
import scala.collection.immutable.{IndexedSeq, Map}
import scala.util.Try


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
    .register("SelectableModelView"){case (el,args)=>new SelectableModelView(el,args)}
    .register("Selection"){case (el,args)=>new SelectionView(el,args).withBinder(view=>new CodeBinder(view))}


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
  import ops._
  import org.denigma.binding.extensions._
  

  private val prefs =new DefaultPrefixes[Plantain]().prefixes


  lazy val testData = Seq(
    ops.makeTriple(subject,WebPlatform[Plantain](ops)("title"),ops.makeLiteral("Title",ops.xsd.string)),
    ops.makeTriple(subject,WebPlatform[Plantain](ops)("text"),ops.makeLiteral("Some text",ops.xsd.string))
  )


  val saveClick: Var[MouseEvent] = Var(Events.createMouseEvent())

  /*this.saveClick.takeIf(dirty).handler{
    //dom.console.log("it should be saved right now")
    this.saveModel()
  }*/

  import org.denigma.binding.macroses.CSV

  val code = Var("")

  override lazy val graph: Var[PointedGraph[Plantain]] =  Var(PointedGraph[Plantain](
  subject,ops.makeGraph(TestData[Plantain](subject).data))
  )

  val resolver = new PrefixResolver[Plantain](Var(prefs))

  binders = List(new RDFModelBinder[Plantain](this,  graph,  resolver))
}

class SelectableModelView(elem:HTMLElement,params:Map[String,Any])(implicit ops:RDFOps[Plantain])
  extends ModelView[Plantain](elem,params)(ops)
{
  import ops._
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