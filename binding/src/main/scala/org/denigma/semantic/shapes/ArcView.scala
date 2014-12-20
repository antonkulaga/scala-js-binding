package org.denigma.semantic.shapes

import org.denigma.binding.binders.{BasicBinding, GeneralBinder, NavigationBinding}
import org.denigma.binding.views.BindableView
import org.denigma.semantic.binders.shex._
import org.scalajs.dom.HTMLElement
import org.scalax.semweb.rdf.RDFValue
import org.scalax.semweb.shex._
import rx.Var

import scala.collection.immutable.Map
import scala.concurrent.Future

object ArcView {


  case class SuggestNameTerm(typed:String)

  def apply(el:HTMLElement,params:Map[String,Any]) = {
    new JustArcView(el,params)
  }

  implicit def defaultBinders(view:ArcView): List[BasicBinding] = new NamesBinder(view,view.arc,view.suggestProperty)::new OccursBinder(view,view.arc)::new GeneralBinder(view)::new NavigationBinding(view)::Nil

  class JustArcView(val elem:HTMLElement, val params:Map[String,Any]) extends ArcView {

    override def activateMacro(): Unit = {extractors.foreach(_.extractEverything(this))}

    override protected def attachBinders(): Unit = binders =  defaultBinders(this)

  }

}




trait ArcView extends BindableView
{



//  require(params.contains("item"), "ArcView should contain arc item inside")
//  val arc = params("item").asInstanceOf[Var[ArcRule]]

  val arc = this.resolveKey("item"){case k:Var[ArcRule]=>k}

  //require(params.contains("storage"), "ArcView should contain storage inside")

  def suggestProperty(str:String): Future[List[RDFValue]] = {
    //debug("arc suggest works!")
    this.ask[ArcView.SuggestNameTerm,List[RDFValue]](ArcView.SuggestNameTerm(str))
  }

}

