package org.denigma.semantic.binders.shex

import org.denigma.semantic.binders.SelectOption
import org.denigma.semantic.shapes.ArcView
import org.scalajs.dom
import org.scalajs.dom.HTMLElement
import org.scalax.semweb.rdf.RDFValue
import org.scalax.semweb.shex.ArcRule
import rx.Var

import scala.collection.immutable.Map
import scala.concurrent.Future
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.util.{Failure, Success}

class NamesBinder(view:ArcView,arc:Var[ArcRule], suggest:(String)=>Future[List[RDFValue]]) extends ArcBinder(view,arc){


  var names = Map.empty[HTMLElement,NameTermSelector]

  def nameTypeHandler(el: HTMLElement)(str:String) =
  //this.storage.read()

    this.names.get(el) match
    {
      case Some(s)=>
        //dom.console.info(s"typing: $str")
        this.suggest(str).onComplete{
          case Success(options)=>s.updateOptions(options)
          case Failure(th)=>dom.console.error(s"type handler failure for with failure ${th.toString}")
        }
      case None=>dom.console.error(s"cannot find selector for property")
      //dom.console.log("typed = "+str)
    }



  override protected def arcPartial(el: HTMLElement, value: String): PartialFunction[String, Unit] = {

    case "data" if value=="name" =>
      this.bindVar("name", el: HTMLElement, this.arc) { (e,a)=>
        val sel = this.names.getOrElse(el, {
          val s = new NameTermSelector(el,a,nameTypeHandler(el) )
          names = names+ (el -> s)
          s
        })
        sel.fillValues(arc)

      }

    case "data" if value=="value"=>

  }
}


import org.scalajs.dom
import org.scalajs.dom.HTMLElement
import org.scalax.semweb.rdf.IRI
import org.scalax.semweb.shex._
import rx.ops._

import scala.scalajs.js

/**
 * NameTerm selector for the property
 * @param el
 * @param arc
 * @param typeHandler
 */
class NameTermSelector(val el:HTMLElement,arc:Var[ArcRule], typeHandler:(String)=>Unit) extends ArcSelector(arc) {


  type Value = IRI

  type Element = NameClass



  def valueIntoElement(value:String):Element = this.value2NameClass(value)

  //TODO: rewrite
  def elementIntoValue(element:Element):IRI = element  match {
    case NameTerm(iri) => iri
    case NameStem(stem) => stem ;???
    case NameAny(other) => ???
  }

  protected def selectParams(el: HTMLElement):js.Dynamic = {
    js.Dynamic.literal(
      onItemAdd = itemAddHandler _,
      onItemRemove =  itemRemoveHandler _,
      onType = typeHandler  ,
      maxItems = 1,
      value = this.elementIntoValue(arc.map(a=>a.name).now).stringValue,
      valueField = "id",
      labelField = "title",
      searchField = "title"
    )
  }

  protected def nameClass2Value(name:NameClass) = name match {
    case NameTerm(iri)=>iri.stringValue
    case NameStem(stem)=> dom.console.error("name stem is not yet implemented"); ???
    case NameAny(other)=> dom.console.error("name any is not yet implemented"); ???
  }

  protected def value2NameClass(value:String) = value match {
    case value if value.contains(":") => NameTerm(IRI(value))
    case _ => dom.console.error("strange value for the name term") ; ???
  }





  override protected def itemRemoveHandler(value: String): Unit = {
    //nothing is needed

  }

  override protected def itemAddHandler(value: String, item: js.Dynamic): Unit = {
    val nt = this.value2NameClass(value)
    //dom.console.info(s"VALUE = $value TERM + ${nt.toString}")
    if(arc.now.name!=nt) {
      arc() = arc.now.copy(name = nt)
    }



  }

  def fillValues(arc:Var[ArcRule]):this.type = {
    val ss= this.selectizeFrom(el)
    ss.clear()
    ss.clearOptions()
    val name = arc.now.name
    val value= this.elementIntoValue(name)
    //debug(name + " | "+value)
    //dom.console.info("value = "+value)
    ss.addOption(this.makeOption(value))
    ss.addItem(value.stringValue)
    this

  }


}




