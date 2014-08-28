package org.denigma.controls.semantic

import org.denigma.binding.extensions._
import org.denigma.binding.messages.Filters
import org.denigma.binding.messages.Filters.{ValueFilter, ContainsFilter}
import org.denigma.binding.semantic.ModelInside
import org.denigma.storages.AjaxModelStorage
import org.denigma.controls.general.EditModelView
import org.scalajs.dom
import org.scalajs.dom.HTMLElement
import org.scalajs.jquery._
import org.scalax.semweb.rdf._
import org.scalax.semweb.sparql.Filter
import rx.{Rx, Var}

import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.scalajs.js
import scala.scalajs.js.{Dictionary, JSON}
import scala.util.{Failure, Success}

/**
 * Selector that provides suggessions for filter fields
 * @param el
 * @param key
 * @param modifiers
 * @param typeHandler
 */
class FilterSelector(el:HTMLElement, key:IRI, modifiers:Var[Map[IRI,Filters.Filter]], typeHandler:(String)=>Unit)  extends ModifierSelector[Filters.Filter](el,key,modifiers,typeHandler){
  val sel: js.Dynamic = jQuery(el).dyn.selectize(selectParams(el))


  protected def propertyFilterOption = this.modifiers.now.get(key)

  override protected def selectParams(el: HTMLElement):js.Dynamic = {
    js.Dynamic.literal(
      delimiter = "|",
      persist = false,
      valueField = "id",
      labelField = "title",
      searchField = "title",
      onType = typeHandler  ,
      onItemAdd = itemAddHandler _,
      onItemRemove =  itemRemoveHandler _,
      options = js.Array()
    )
  }

  override def itemAddHandler(value:String, item:js.Any): Unit =
  {
    val filters =  this.modifiers.now
    val v = this.parseRDF(value)
    this.propertyFilterOption match {
      case Some(filter:Filters.ValueFilter)=>
        if(!filter.values.contains(v)) this.modifiers() = filters + (key->filter.add(v))


      case Some(other)=> dom.console.log("do not add other filters yet")
      case None=>this.modifiers() = filters + (key->new Filters.ValueFilter(key,Set(v)))
    }
  }


  override def itemRemoveHandler(value:String): Unit = {
    val v = this.parseRDF(value)
    this.propertyFilterOption match {
      case Some(filter:Filters.ValueFilter)=>
        val f = filter.remove(v)
        if(f.isEmpty) this.modifiers() = this.modifiers.now-key else this.modifiers() = this.modifiers.now + (key->f)
      case Some(other)=>
        dom.console.log("do not remove other filters yet")
      case None=>
    }
  }


  def fillValues(fls:Map[IRI,Filters.Filter]):this.type = {
    val ss= this.selectizeFrom(el)
    ss.clearOptions()
    fls.get(key) match {
      case Some(f:ValueFilter)=>
        f.values.foreach{v=>
          dom.console.log("filled = "+v.stringValue)
          ss.addOption(this.makeOption(v))
          ss.addItem(v.stringValue)

        }
      case _ =>
    }
    this
  }

  //protected def makeOption(v:String): js.Dynamic =  js.Dynamic.literal( id = v, title = v.label)


}

abstract class ModifierSelector[T](val el:HTMLElement, val key:IRI, val modifiers:Var[Map[IRI,T]], typeHandler:(String)=>Unit)  extends Selector{

  override def itemRemoveHandler(value:String): Unit = {
    this.modifiers() = this.modifiers.now - key
  }

}


class PropertySelector(val el:HTMLElement,val key:IRI,val modelInside:Var[ModelInside], typeHandler:(String)=>Unit) extends Selector
{

  val sel: js.Dynamic = jQuery(el).dyn.selectize(selectParams(el))

  override protected def selectParams(el: HTMLElement):js.Dynamic = {
    js.Dynamic.literal(
      delimiter = "|",
      persist = false,
      valueField = "id",
      labelField = "title",
      searchField = "title",
      onType = typeHandler  ,
      onItemAdd = itemAddHandler _,
      onItemRemove =  itemRemoveHandler _,
      options = makeOptions()
    )
  }

  def makeOptions():js.Array[js.Dynamic] =
    this.modelInside.now.current.properties.get(key) match {
      case Some(iris)=>
        val o: List[js.Dynamic] = iris.map(i=> makeOption(i)).toList
        js.Array( o:_* )
      case None=> js.Array()
    }


  protected def itemAddHandler(value:String, item:js.Any): Unit = {
    //dom.console.log("added = "+value)
    val mod = modelInside.now
    mod.current.properties.get(key) match {
      case None=> modelInside() = mod.add(key,parseRDF(value))
      case Some(ps) => if(!ps.exists(p=>p.stringValue==value)) modelInside() = mod.add(key,parseRDF(value))
    }
  }

  protected def itemRemoveHandler(value:String): Unit = {
    val mod =  modelInside.now
    val remove: Option[Set[RDFValue]] =mod.current.properties.get(key).map{ps=>  ps.collect{case p if p.stringValue==value=>p}    }
    if(remove.nonEmpty) {
      val n = this.parseRDF(value)
      val s1 = mod.current.properties(key).size
      val md = mod.delete(key,n)
      val s2 = md.current.properties(key).size
      //dom.console.log(s"s1 = $s1 | s2 = $s2")
      modelInside() = md
    }
  }


   def fillValues(model: ModelInside):this.type = {
    val ss= this.selectizeFrom(el)
    ss.clearOptions()
    model.current.properties.get(key).foreach{ps=>
      ps.foreach{p=>
        ss.addOption(this.makeOption(p))
        ss.addItem(p.stringValue)
      }
    }
    this
  }

}

trait Selector {

  val key:IRI
  val el:HTMLElement

  val sel: js.Dynamic


  /**
   * Parses string to get RDF value
   * @param str
   * @return
   */
  def parseRDF(str:String) = str match {
    case s if str.contains("_:") => BlankNode(str)
    case s if str.contains(":") => IRI(s)
    case s if str.contains("^^")=>dom.console.error("any lit is not yet implemented for selectview")
      AnyLit(str)
    case s => StringLiteral(s)

  }

  protected def makeOption(v:RDFValue): js.Dynamic =  js.Dynamic.literal( id = v.stringValue, title = v.label)


  protected def itemAddHandler(value:String, item:js.Any): Unit
  protected def itemRemoveHandler(value:String): Unit


  protected def selectParams(el: HTMLElement):js.Dynamic



  protected def selectizeFrom(el:HTMLElement): Selectize = {
    val s = el.dyn.selectize
    s.asInstanceOf[Selectize]
  }


  protected def makeOptions(properties:Map[IRI,Set[RDFValue]],iri:IRI):js.Array[js.Dynamic] =
    properties.get(iri) match {
      case Some(iris)=>
        val o: List[js.Dynamic] = iris.map(i=> makeOption(i)).toList
        js.Array( o:_* )
      case None=> js.Array()
    }


  def updateOptions(opts:List[RDFValue]) = {

    val ss = selectizeFrom(el)
    for {
      r <- ss.options.keys.filter{case k => !ss.items.contains(k)}
    } ss.options.remove(r)
    opts.foreach { o =>
      ss.addOption(makeOption(o))
    }
    ss.refreshItems()


  }
}

//case class Selectable(el:HTMLElement,ss:Selectize, uri:IRI)
//{
//  var options =
//}

trait Selectize extends js.Object {

  def addOption(option:js.Any):js.Any = ???

  def updateOption(value:js.Any,data:js.Any):js.Any = ???

  def clearOptions():Unit = ???

  def addItem(value:Any):Unit = ???

  def removeItem(value:Any):Unit = ???

  //def createItem(value:js.Any):Unit = ???

  def refreshItems():Unit = ???

  def clear():Unit = ???

  //var options:js.Dictionary[js.Dictionary[js.Object]] = ???

  var options:js.Dictionary[js.Object] = ???


  var items:js.Array[String] = ???

  //  var settings_element:js.Any = ???



}