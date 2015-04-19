package org.denigma.semantic.binders.shex

import org.denigma.binding.views.BindableView
import org.denigma.selectize.Selectize
import org.scalajs.dom
import org.scalajs.dom.raw.HTMLElement
import org.scalajs.jquery._
import org.denigma.semweb.rdf.IRI
import org.denigma.semweb.shex.{ArcRule, _}
import rx.core.Var
import rx.ops._
import org.denigma.binding.extensions._
import scala.collection.immutable.Map
import scala.scalajs.js


class OccursBinder(view:BindableView, arc:Var[ArcRule],prefs:Var[Map[String,IRI]] = Var(Map.empty)) extends ArcBinder(view,arc,prefs){
  var occurs = Map.empty[HTMLElement,OccursSelector]


  override protected def rdfPartial(el: HTMLElement, key: String, value: String, ats:Map[String,String]): PartialFunction[String, Unit] = {
    this.vocabPartial(value).orElse(this.arcPartial(el:HTMLElement,value.toLowerCase))
  }


  override protected def arcPartial(el: HTMLElement,value:String): PartialFunction[String,Unit] =
  {
    case "data" if value=="occurs" =>

      this.bindVar("occurs", el: HTMLElement, this.arc) { (e,arc)=>
        val sel = this.occurs.getOrElse(el, {
          val s = new OccursSelector(el,arc)
          occurs = occurs + (el -> s)
          s
        })
        sel.fillValues(arc)

      }
  }

}





class OccursSelector(val el:HTMLElement,arc:Var[ArcRule],prefs:Var[Map[String,IRI]]= Var(Map.empty)) extends ArcSelector(arc, prefs) {


  val sel= this.initSelectize(el)
  protected val values = List(Star.obj,Plus.obj,ExactlyOne.obj)

  type Element = Cardinality

  type Value = String

  def valueIntoElement(value:String):Element =  value match {
    case ExactlyOne.obj.stringValue=>ExactlyOne
    case Plus.obj.stringValue=>Plus
    case Star.obj.stringValue=>Star
    case Opt.obj.stringValue=>Opt
    case other => dom.console.error("unknown cardinality value ="+other)
      Star
  }

  def elementIntoValue(element:Element):String = element  match {
    case ExactlyOne=>ExactlyOne.obj.stringValue
    case Plus=>Plus.obj.stringValue
    case Star=>Star.obj.stringValue
    case Opt=>Opt.obj.stringValue
    case org.denigma.semweb.shex.Range(from,to)=> (from,to) match {
      case (0,1)=> Opt.obj.stringValue
      case (1,1)=> ExactlyOne.obj.stringValue
      case (0,Int.MaxValue)=> Star.obj.stringValue
      case (1,Int.MaxValue)=> Plus.obj.stringValue
      case other=>
        dom.console.error(s"does not support ranges from $from to $to yet")
        Star.obj.stringValue

    }

    case other => dom.console.error("unknown cardinality value ="+other)
      Star.obj.stringValue
  }



  protected def selectParams(el: HTMLElement):js.Dynamic = {
    js.Dynamic.literal(
      onItemAdd = itemAddHandler _,
      onItemRemove =  itemRemoveHandler _,
      maxItems = 1,
      options = makeOptions(),
      value = elementIntoValue(arc.map(a=>a.occurs).now),
      valueField = "id",
      labelField = "title",
      searchField = "title"
    )
  }

  def makeOptions()=
  {
    val o =    List(Star.obj,Plus.obj,ExactlyOne.obj,Opt.obj).map{case i=>
      makeOption(i)
    }.toList
    js.Array( o:_* )
  }





  override protected def itemRemoveHandler(value: String): Unit = {
    //nothing is needed

  }

  override protected def itemAddHandler(value: String, item: js.Dynamic): Unit = {

    val oc = valueIntoElement(value)
    if(oc!=arc.now.occurs){
      arc() = arc.now.copy(occurs = oc)
    }

  }

  def fillValues(arc:Var[ArcRule]):this.type = {
    val ss= this.selectizeFrom(el)
    ss.clear()
    val value = this.elementIntoValue(arc.now.occurs)
    //dom.console.info("value = "+value)
    ss.addOption(this.makeOption(value))
    ss.addItem(value)
    this

  }

}