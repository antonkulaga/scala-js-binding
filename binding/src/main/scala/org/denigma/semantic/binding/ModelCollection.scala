package org.denigma.semantic.binding

import org.denigma.binding.extensions._
import org.denigma.binding.picklers.rp
import org.denigma.binding.views.{CollectionView, BindableView}
import org.denigma.semantic.controls.PropertyModelView
import org.scalajs.dom
import org.scalajs.dom.extensions._
import org.scalajs.dom.{HTMLElement, MouseEvent}
import rx.core.{Rx, Var}

import scala.collection.immutable.{List, Map}
import scalatags.Text._

object ModelCollection
{
  type ItemView = ModelView

  def apply(html:HTMLElement,item:Var[ModelInside]):ItemView= {
    //
    new JustModel("item"+Math.random(),item,html)
  }




  class JustModel(override val name:String,slot:Var[ModelInside],val elem:HTMLElement) extends PropertyModelView{


    override val modelInside = slot

    override def activateMacro(): Unit = {this.extractors.foreach(_.extractEverything(this))}



    override def params: Map[String, Any] = Map.empty

    override protected def attachBinders(): Unit = PropertyModelView.defaultBinders(this)
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

  override def newItem(item:Item):ItemView =
  {
    //dom.console.log(template.outerHTML.toString)
    val el = template.cloneNode(true).asInstanceOf[HTMLElement]

    el.removeAttribute("data-template")
    val mp: Map[String, Any] = Map[String,Any]("model"->item)

    val view = el.attributes.get("data-item-view") match {
      case None=>
        ModelCollection.apply(el,item)
      case Some(v)=> this.inject(v.value,el,mp) match {
        case iv:ItemView=> iv
        case _=>
          dom.console.error(s"view ${v.value} exists but does not inherit ItemView")
          ModelCollection.apply(el,item)
      }
    }
    item.handler(onItemChange(item))
    view
  }

  /**
   * Fires when view was binded by default does the same as bind
   * @param el
   */
  override def bindView(el: HTMLElement) = {
    activateMacro()
    this.bind(el)
    this.subscribeUpdates()
  }



}
