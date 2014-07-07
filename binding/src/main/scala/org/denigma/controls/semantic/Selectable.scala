package org.denigma.controls.semantic

import org.denigma.binding.extensions._
import org.denigma.binding.storages.AjaxModelStorage
import org.denigma.controls.general.EditModelView
import org.scalajs.dom
import org.scalajs.dom.HTMLElement
import org.scalajs.jquery._
import org.scalax.semweb.rdf._

import scala.scalajs.js

trait SelectableModelView extends EditModelView  {

  def resource = this.modelInside.now.current.id

  protected def makeOption(v:RDFValue): js.Dynamic = v match  {
    case i: IRI => js.Dynamic.literal( id = i.stringValue, title = this.printLabel(i))
    case node:BlankNode => js.Dynamic.literal(id = node.stringValue, title = node.id)
    case other:AnyLit => js.Dynamic.literal(id = other.stringValue, title = other.label)
  }

  def makeOptions(iri:IRI):js.Array[js.Dynamic] =
    this.modelInside.now.current.properties.get(iri) match {
      case Some(iris)=>
        val o: List[js.Dynamic] = iris.map(i=> makeOption(i)).toList
        js.Array( o:_* )
      case None=> js.Array()
    }

  def typeHandler(el: HTMLElement, key: IRI)(str:String) = {
    dom.console.log("typed = "+str)
  }


  def changeHandler(el: HTMLElement, key: IRI)(str:String) = {
    dom.console.log("changed = "+str)
  }

  def selectParams(el: HTMLElement,key:IRI):js.Dynamic = {
    js.Dynamic.literal(
      delimiter = "|",
      persist = false,
      valueField = "id",
      labelField = "title",
      searchField = "title",
      onType = typeHandler(el,key) _ ,
      onChange = changeHandler(el,key) _,
      options = makeOptions(key)
    )
  }

  protected override def bindRdfInput(el: HTMLElement, key: IRI): Unit =
  {

    dom.console.log(el.outerHTML)
    val sel = jQuery(el).dyn.selectize(selectParams(el,key))

    this.bindRx(key.stringValue, el: HTMLElement, modelInside) { (el, model) =>

      val s = el.dyn.selectize
      val ss = s.asInstanceOf[Selectize]
      ss.clear()
      ss.clearOptions()

      model.current.properties.get(key) foreach{ps=>
        ps.foreach{p=>
          ss.addOption(this.makeOption(p))
          ss.addItem(p.stringValue)
        }
      }
    }
  }

  val shape:Res = params.get("shape").map{case sh=>sh.asInstanceOf[Res]}.get


  override def storage: AjaxModelStorage =  params.get("storage").map{case sh=>sh.asInstanceOf[AjaxModelStorage]}.get

}
//
//case class Selectable(el:HTMLElement,key:IRI, modelInside:Var[ModelInside]) {
//  protected def makeOption(v:RDFValue): js.Dynamic = v match  {
//    case i: IRI => js.Dynamic.literal( id = i.stringValue, title = this.printLabel(i))
//    case node:BlankNode => js.Dynamic.literal(id = node.stringValue, title = node.id)
//    case other:AnyLit => js.Dynamic.literal(id = other.stringValue, title = other.label)
//  }
//
//  def makeOptionsjs.Array[js.Dynamic] =
//    this.modelInside.now.current.properties.get(iri) match {
//      case Some(iris)=>
//        val o: List[js.Dynamic] = iris.map(i=> makeOption(i)).toList
//        js.Array( o:_* )
//      case None=> js.Array()
//    }
//
//  def typeHandler(el: HTMLElement, key: IRI)(str:String) = {
//    dom.console.log("typed = "+str)
//  }
//
//
//  def changeHandler(el: HTMLElement, key: IRI)(str:String) = {
//    dom.console.log("changed = "+str)
//  }
//
//  def selectParams(key:IRI):js.Dynamic = {
//    js.Dynamic.literal(
//      delimiter = "|",
//      persist = false,
//      valueField = "id",
//      labelField = "title",
//      searchField = "title",
//      onType = typeHandler(key) _ ,
//      onChange = changeHandler(key) _,
//      options = makeOptions(key)
//    )
//  }
//
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

  //var items:js.Array[js.Any] = ???



}