package org.denigma.binding.frontend.controls

import org.denigma.binding.binders.extractors.EventBinding
import org.denigma.binding.extensions._
import org.denigma.binding.messages.Suggestion
import org.denigma.binding.picklers.rp
import org.denigma.binding.views.{JustPromise, PromiseEvent, BindableView}
import org.denigma.semantic.models.EditModelView
import org.denigma.semantic.rdf.{ShapeInside, ModelInside}
import org.denigma.semantic.shapes.{ArcView, ShapeView}
import org.denigma.semantic.storages.{ShapeStorage, AjaxModelStorage}
import org.scalajs.dom
import org.scalajs.dom.HTMLElement
import org.scalax.semweb.rdf.vocabulary.XSD
import org.scalax.semweb.rdf.{RDFValue, IRI, vocabulary}
import org.scalax.semweb.shex.{Shape, ShapeBuilder, Star}
import rx.Var
import scalajs.concurrent.JSExecutionContext.Implicits.queue

import scala.concurrent.{Promise, Future}
import scala.util.{Success, Failure}

object ShapeEditor
{
  lazy val testShape: Shape =  {
    val de = IRI("http://denigma.org/resource/")
    val dc = IRI(vocabulary.DCElements.namespace)
    val art = new ShapeBuilder(de / "Article_Shape")
    art has de /"is_authored_by" occurs Star //*/occurs Plus
    art has de / "is_published_in" occurs Star //occurs Plus
    art has dc / "title" occurs Star //occurs ExactlyOne
    //art has de / "date" occurs Star //occurs ExactlyOne
    art has de / "abstract" of XSD.StringDatatypeIRI  occurs Star//occurs Star
    art has  de / "excerpt" of XSD.StringDatatypeIRI  occurs Star//occurs Star
    art.result
  }
}


class ShapeEditor (val elem:HTMLElement,val params:Map[String,Any]) extends  ShapeView
{

  override lazy val shape = Var(ShapeInside(ShapeEditor.testShape))

  implicit val registry = rp

  //require(params.contains("path"),"ShapeEditor should have path view-param") //is for exploration by default

  val path:String = this.resolveKey("path"){
    case v=>if(v.toString.contains(":")) v.toString else sq.withHost(v.toString)
  }

  val storage = new ShapeStorage(path)


  val addClick = Var(EventBinding.createMouseEvent())


  val applyShape = Var(EventBinding.createMouseEvent())


  override protected def attachBinders(): Unit = this.withBinders(ShapeView.defaultBinders(this))

  override def activateMacro(): Unit = { extractors.foreach(_.extractEverything(this))}

  override def receiveFuture:PartialFunction[PromiseEvent[_,_],Unit] = {

    case JustPromise(ArcView.SuggestNameTerm(typed),origin,latest,bubble,promise:Promise[List[RDFValue]])=>
      storage.suggestProperty(typed).onComplete{
      case Success(res)=>
        promise.success(res.options)
      case Failure(th)=>
        promise.failure(th)
        dom.console.error("suggession is broken"+th)
    }

    case ev=>
      //debug("propogation")
      this.propagateFuture(ev)

  }

}

class ShapeProperty(val elem:HTMLElement, val params:Map[String,Any]) extends ArcView
{

  override protected def attachBinders(): Unit = binders =  ArcView.defaultBinders(this)

  override def activateMacro(): Unit = {extractors.foreach(_.extractEverything(this))}



  val removeClick = Var(EventBinding.createMouseEvent())

  removeClick.handler{
    //this.die()
  }


}