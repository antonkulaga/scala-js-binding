package org.denigma.views.lists

import scala.collection.immutable._
import org.scalax.semweb.shex.PropertyModel
import rx.core.Var
import org.scalajs.dom.{MouseEvent, HTMLElement}
import org.denigma.views.core.{OrganizedView, BindingView, OrdinaryView}
import rx.Rx
import scala.Predef
import scalatags.Text.Tag
import org.denigma.views.models.ModelView
import org.scalajs.dom
import org.denigma.extensions._
import org.scalajs.dom._
import org.scalajs.dom
import org.denigma.binding.CollectionBinding
import scala.collection.immutable._
import scala.Some
import org.scalajs.dom.extensions._
import org.denigma.extensions._
import scalatags.Text.Tag


object ModelCollectionView {

  def apply(element:HTMLElement,model:PropertyModel):OrganizedView with ModelView ={

    ???
  }
}

/**
 * Class for model view to initiate
 * @param el
 * @param params
 */
class JustModelView(el:HTMLElement,params:Map[String,Any] = Map.empty) extends OrganizedView("ModelView",el) with ModelView {

}

/**
 * View that visualize collections
 */
abstract  class ModelCollectionView(name:String,element:HTMLElement) extends OrdinaryView(name,element) with CollectionView {

  var items = Var(List.empty[PropertyModel])

  override type Item = PropertyModel

  override def newItem(item: Item): ItemView ={
      val el = template.cloneNode(true).asInstanceOf[HTMLElement]
      val view: ItemView = el.attributes.get("item-view") match {
        case None=> ModelCollectionView.apply(el,item)
        case Some(v)=>
//          this.inject(v.value,el) match {
//          case item:MapView=> item
//          case _=>
//            dom.console.error(s"view ${v.value} exists but does not inherit MapView")
//            ModelCollectionView.apply(el,item)
//          }
          ???
      }
      view
    }



  override type ItemView = OrganizedView with ModelView

}
