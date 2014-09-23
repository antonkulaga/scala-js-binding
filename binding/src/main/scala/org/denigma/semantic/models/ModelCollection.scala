package org.denigma.semantic.models

import org.denigma.binding.extensions._
import org.denigma.binding.picklers.rp
import org.denigma.binding.views.{BindableView, CollectionView}
import org.denigma.semantic.models.ModelView
import org.denigma.semantic.rdf.ModelInside
import org.scalajs.dom
import org.scalajs.dom.HTMLElement
import org.scalajs.dom.extensions._
import rx.core.{Rx, Var}

import scala.collection.immutable.{List, Map}

object ModelCollection
{
  type ItemView = ModelView

  def apply(html:HTMLElement,mp:Map[String,Any]):ItemView= {
    //
    new JustModel("item"+Math.random(),html,mp)
  }


  class JustModel(override val name:String,val elem:HTMLElement,mp:Map[String,Any]) extends ModelView{

    override def activateMacro(): Unit = {this.extractors.foreach(_.extractEverything(this))}

    override def params: Map[String, Any] = Map.empty

    override protected def attachBinders(): Unit = binders = RemoteModelView.defaultBinders(this)
  }

}

/**
 * This trait represents a view that is collection of models (Property models of RDFs)
 */
trait ModelCollection extends BindableView
  with CollectionView
{
  def params:Map[String,Any]

  implicit val registry = rp


  override type Item = Var[ModelInside]
  override type ItemView =  ModelCollection.ItemView

  def defaultItem = ModelInside.empty

  override val items = Var(List.empty[Var[ModelInside]])

  val dirty = Rx{items().filterNot(i=>i().isUnchanged)} //TODO check how it works


  def onItemChange(item:Item) = if(item.now.wantsToDie){
    //dom.alert("WORKS")
    val i = items.now
    items() = items.now.filterNot(_==item)
  }

  /**
   * Adds new item
   * @param item
   */
  def addItem(item:Item = Var(this.defaultItem)) = {
    this.items() = items.now :+ item
  }

  //val dirty = Rx{items().filterNot(_}

  override def newItem(item:Item):ItemView = {
    item.handler(onItemChange(item))
    this.constructItem(item,Map("model"->item)){ (el,mp)=>
        item.handler(onItemChange(item))
        ModelCollection.apply(el,mp)
      }
  }
//  {
//    //dom.console.log(template.outerHTML.toString)
//    val el = template.cloneNode(true).asInstanceOf[HTMLElement]
//
//    el.removeAttribute("data-template")
//    val mp: Map[String, Any] = Map[String,Any]("model"->item)
//
//    val view = el.attributes.get("data-item-view") match {
//      case None=>
//        ModelCollection.apply(el,mp)
//      case Some(v)=> this.inject(v.value,el,mp) match {
//        case iv:ItemView=> iv
//        case _=>
//          dom.console.error(s"view ${v.value} exists but does not inherit ItemView")
//          ModelCollection.apply(el,mp)
//      }
//    }
//    item.handler(onItemChange(item))
//    view
//  }

  /**
   * Fires when view was binded by default does the same as bind
   * @param el
   */
  override def bindView(el: HTMLElement) = {
    attachBinders()
    activateMacro()
    this.bind(el)
    this.subscribeUpdates()
  }



}
