package org.denigma.controls.semantic

import org.denigma.binding.semantic.{ModelInside, ChangeSlot}
import org.denigma.binding.views.{CollectionView, OrdinaryView}
import org.denigma.controls.semantic.ShapeInside
import org.scalajs.dom.{MouseEvent, HTMLElement}
import org.scalax.semweb.shex.{PropertyModel, Shape}
import rx.Rx
import rx.core.Var
import org.denigma.binding.extensions._
import rx.extensions._

import scalatags.Text.Tag


object ShapeInside {

  def apply(initial:Shape):ShapeInside = ShapeInside(initial,initial)

}

case class ShapeInside(initial:Shape,current:Shape,wantsToDie:Boolean = false) extends ChangeSlot
{
  override type Value = Shape
}


object ShapeView {



}

class ShapeView(val elem:HTMLElement,val params:Map[String,Any]) extends OrdinaryView{

  require(params.contains("shape"),"ShapeView must contain shape in params")


  val shapeInside: Var[ShapeInside] = Var(ShapeInside(this.params("shape").asInstanceOf[Shape]))



  override def bools: Map[String, Rx[Boolean]] = ???

  override def strings: Map[String, Rx[String]] = ???

  override def tags: Map[String, Rx[Tag]] = ???

  override def mouseEvents: Map[String, rx.Var[MouseEvent]] = ???
}

abstract class ShapeEditor(val elem:HTMLElement,val params:Map[String,Any]) extends OrdinaryView with CollectionView
{

  val path:String = params.get("path").map(v=>if(v.toString.contains(":")) v.toString else sq.withHost(v.toString)).get


  override type Item = Shape

  override def newItem(mp: Item): ItemView = {
    ???
  }

  override type ItemView = ShapeView

  override val items: Rx[List[Item]] = Var(List.empty)

}
