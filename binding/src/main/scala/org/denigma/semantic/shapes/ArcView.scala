package org.denigma.semantic.shapes

import org.denigma.binding.views.BindableView
import org.denigma.semantic.rdf.RDFBinder
import org.scalajs.dom.HTMLElement
import org.scalax.semweb.shex._
import rx.core.Var

import scala.collection.immutable.Map


object ArcView {

  def apply(el:HTMLElement,params:Map[String,Any]) = {
    new JustArcView(el,params)
  }


  class JustArcView(val elem:HTMLElement, val params:Map[String,Any]) extends ArcView {

    override def activateMacro(): Unit = {extractors.foreach(_.extractEverything(this))}

    override protected def attachBinders(): Unit = BindableView.defaultBinders(this)

  }

}


trait ArcView extends BindableView
{

  val arc = Var(params("item").asInstanceOf[ArcRule])

  require(params.contains("item"), "ArcView should contain arc item inside")

}

class ArcBinder(view:ArcView, arc:Var[ArcRule]) extends RDFBinder(view) {

  override protected def rdfPartial(el: HTMLElement, key: String, value: String, ats:Map[String,String]): PartialFunction[String, Unit] =
    this.vocabPartial(value).orElse(this.arcPartial(value))

  protected def arcPartial(value:String): PartialFunction[String,Unit] ={
//    case class ArcRule(
//                        id: Label =Rule.genRuleLabel(),
//                        name: NameClass,
//                        value: ValueClass,
//                        occurs: Cardinality,
//                        actions: Seq[Action] = List.empty,
//                        priority:Option[Int] = None, //the smaller the more important
//                        title:Option[String] = None
//                        ) extends Rule


    case "data-id"=>


    case _ =>

  }


}