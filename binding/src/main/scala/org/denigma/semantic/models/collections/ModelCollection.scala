package org.denigma.semantic.models.collections

import org.denigma.binding.extensions._
import org.denigma.binding.views.BindableView
import org.denigma.binding.views.collections.CollectionView
import org.denigma.semantic.models.{ModelView, RemoteModelView}
import org.denigma.semantic.rdf.ModelInside
import org.scalajs.dom.HTMLElement
import rx.core.{Rx, Var}

import scala.collection.immutable.{List, Map}

object ModelCollection
{
  def apply(html:HTMLElement,params:Map[String,Any]):ModelView= {
    //
    new JustModel("item"+Math.random(),html,params)
  }

  class JustModel(override val name:String,val elem:HTMLElement, val params:Map[String,Any]) extends ModelView{

    override def activateMacro(): Unit = { extractors.foreach(_.extractEverything(this))}

    override protected def attachBinders(): Unit = binders = RemoteModelView.defaultBinders(this)
  }
}

/**
 * This trait represents a view that is collection of models (Property models of RDFs)
 */
abstract class ModelCollection extends BindableView
  with CollectionView
{
  def params:Map[String,Any]



  override type Item = Var[ModelInside]
  override type ItemView =  ModelView

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
}
