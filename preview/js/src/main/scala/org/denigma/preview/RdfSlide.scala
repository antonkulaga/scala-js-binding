package org.denigma.preview

import java.net.URI

import org.denigma.binding.binders.Events
import org.denigma.binding.views.BindableView
import org.denigma.controls.binders.CodeBinder
import org.denigma.preview.FrontEnd._
import org.denigma.semantic.binders.RDFModelBinder
import org.denigma.semantic.{WebPlatform, DefaultPrefixes}
import org.denigma.semantic.models.ModelView
import org.scalajs.dom._
import org.scalajs.dom.raw.HTMLElement
import org.w3.banana.plantain.model.Graph
import org.w3.banana._
import org.w3.banana.diesel.PointedGraphW
import org.w3.banana.plantain.Plantain
import rx.core.Var
import rx.ops

import scala.Predef
import scala.collection.immutable.Map
import scala.util.Try


/**
 * Slide about Plantain-related binding
 * @param elem html element to which view is attached
 * @param params
 */
class RdfSlide(val elem:HTMLElement,val params:Map[String,Any] = Map.empty[String,Any])
  extends BindableView
{

  override lazy val injector = defaultInjector.register("TestModelView", (el,params)=>Try(new TestModelView(el,params)))

  override def activateMacro(): Unit = { extractors.foreach(_.extractEverything(this))}

  override protected def attachBinders(): Unit =  binders =  BindableView.defaultBinders(this)
}



class TestModelView(elem:HTMLElement,params:Map[String,Any])(implicit ops:RDFOps[Plantain])
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

  override def activateMacro(): Unit = { extractors.foreach(_.extractEverything(this))}


  val code = Var("")

  override lazy val graph: Var[PointedGraph[Plantain]] = Var[PointedGraph[Plantain]](
    new PointedGraph[Plantain]{
      override def pointer: Plantain#Node = subject

      override def graph: Plantain#Graph = ops.makeGraph(testData)
    }

  )

  //vocab="http://webintelligence.eu/platform/"

  override protected def attachBinders(): Unit = this.withBinders(
      new CodeBinder(this),
      new RDFModelBinder[Plantain](this,
        graph,
        Var(prefs))
    )
}



/*
*
*  <section class="ui segment" data-view="TestModelView" vocab="http://webintelligence.eu/platform/" data-param-path="models/endpoint" data-param-resource="http://page.org" data-param-shape="http://shape.org"v
                <h1>This is test Plantain model that is kep in memory</h1>
                <div>
                    <h1 property="title"></h1>
                    <input property="title">
                    <p property="text"></p>
                    <textarea property="text"></textarea>


                </div>
                <div class="ui button" data-class-positive-if="dirty" data-event-click="saveClick">save</div>

                </section>
* */