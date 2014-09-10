package org.denigma.semantic.shapes.binders

import org.denigma.semantic.rdf.RDFBinder
import org.denigma.semantic.shapes.ArcView
import org.scalajs.dom.HTMLElement
import org.scalax.semweb.shex.ArcRule
import rx.core.Var

import scala.collection.immutable.Map

/**
 * Created by antonkulaga on 9/10/14.
 */
class ArcBinder(view:ArcView, arc:Var[ArcRule]) extends RDFBinder(view) {

  override protected def rdfPartial(el: HTMLElement, key: String, value: String, ats:Map[String,String]): PartialFunction[String, Unit] =
    this.vocabPartial(value).orElse(this.arcPartial(el:HTMLElement,value))



  protected def arcPartial(el: HTMLElement,value:String): PartialFunction[String,Unit] ={

    case "data-occurs"=>


    case "data-name"=>


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
