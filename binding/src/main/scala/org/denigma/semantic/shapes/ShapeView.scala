package org.denigma.semantic.shapes

import org.denigma.binding.binders.extractors.EventBinding
import org.denigma.binding.binders.{GeneralBinder, NavigationBinding}
import org.denigma.binding.views.collections.CollectionView
import org.denigma.semantic.models.WithShapeView
import org.scalajs.dom
import org.scalajs.dom.HTMLElement
import org.scalajs.dom.extensions._
import org.scalax.semweb.shex._
import rx.Rx
import rx.core.Var
import rx.ops._

import scala.collection.immutable.Map


object ShapeView
{
  def defaultBinders(view:ShapeView) = new GeneralBinder(view)::new NavigationBinding(view)::Nil
}


trait ShapeView extends CollectionView with WithShapeView
{
  override type Item = Var[ArcRule]
  override type ItemView = ArcView

  val removeClick = EventBinding.createMouseEvent()

  lazy val rules: Rx[List[Var[ArcRule]]] = this.shape.map(sh=>sh.current.arcRules().map(a=>Var(a))) //TODO: fix in future

  override lazy  val items: Rx[List[Var[ArcRule]]] = rules.map(rl=>rl.sortBy(r=>r.now.priority))

  val newAnd: Rx[AndRule] = rules.map(r=>
    AndRule(r.map(v=>v.now).toSet,this.shapeRes)
  )

  protected def makeDefaultItem(el:HTMLElement,mp:Map[String,Any]):ItemView =    ArcView.apply(el,mp)

  override def newItem(item:Item):ItemView = this.constructItem(item,Map("item"->item)) { (e,m)=> ArcView.apply(e,m) }

  def save() = {
    val sh = shape.now
    shape() = sh.copy(current = sh.current.copy(rule = newAnd.now))
  }


}

