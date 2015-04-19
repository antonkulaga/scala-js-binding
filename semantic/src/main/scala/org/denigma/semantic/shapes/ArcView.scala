package org.denigma.semantic.shapes

import org.denigma.binding.binders.{BasicBinding, GeneralBinder, NavigationBinding}
import org.denigma.binding.views.BindableView
import org.denigma.semantic.binders.shex._
import org.scalajs.dom.raw.HTMLElement
import org.denigma.semweb.rdf.RDFValue
import org.denigma.semweb.shex._
import prickle.Pickle
import rx.Var

import scala.collection.immutable.Map
import scala.concurrent.Future

object ArcView {


  case class SuggestNameTerm(typed:String)


  def apply(el:HTMLElement,params:Map[String,Any]) = {
    new JustArcView(el,params)
  }

  implicit def defaultBinders(view:ArcView): List[BasicBinding] = //new ArcRuleBinder(view,view.arc,view.suggestProperty)::Nil
    new NamesBinder(view,view.arc,view.suggestProperty)::
      new ValueBinder(view,view.arc,view.suggestProperty)::
      //new OccursBinder(view,view.arc)::
      new GeneralBinder(view)::
      new NavigationBinding(view)::Nil

  class JustArcView(val elem:HTMLElement, val params:Map[String,Any]) extends ArcView {

    override def activateMacro(): Unit = {extractors.foreach(_.extractEverything(this))}

    override protected def attachBinders(): Unit = binders =  defaultBinders(this)

  }

}




trait ArcView extends BindableView
{

  /**
   * Pickles ArcRule to string, useful for debugging
   */
  def arcString: String = {
    import org.denigma.binding.composites.BindingComposites._
    Pickle.intoString[ArcRule](this.arc.now)
  }


//  require(params.contains("item"), "ArcView should contain arc item inside")
//  val arc = params("item").asInstanceOf[Var[ArcRule]]

  val arc = this.resolveKey("item"){case k:Var[ArcRule]=>k}

  //require(params.contains("storage"), "ArcView should contain storage inside")

  def suggestProperty(str:String): Future[List[RDFValue]] = {
    //debug("arc suggest works!")
    this.ask[ArcView.SuggestNameTerm,List[RDFValue]](ArcView.SuggestNameTerm(str))
  }

  def suggestValue(str:String): Future[List[RDFValue]] = {
    //debug("arc suggest works!")
    this.ask[ArcView.SuggestNameTerm,List[RDFValue]](ArcView.SuggestNameTerm(str))
  }

}

