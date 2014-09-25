package org.denigma.semantic.shapes

import org.denigma.binding.binders.{NavigationBinding, GeneralBinder}
import org.denigma.binding.extensions.sq
import org.denigma.binding.binders.extractors.EventBinding
import org.denigma.binding.views._
import org.denigma.binding.views.collections.CollectionView
import org.denigma.semantic.models.WithShapeView
import org.denigma.semantic.rdf.{ShapeInside, ChangeSlot}
import org.scalajs.dom
import org.scalajs.dom.{MouseEvent, HTMLElement}
import org.scalax.semweb.rdf.{BlankNode, Res, IRI}
import org.scalax.semweb.shex._
import rx.Rx
import rx.core.Var
import rx.ops._
import org.denigma.binding.extensions._
import org.denigma.binding
import org.scalajs.dom.extensions._

import scala.Predef
import scala.collection.immutable.Map
import scala.util.{Success, Failure}
import scalatags.Text.Tag


object ShapeView
{
  def defaultBinders(view:ShapeView) = new GeneralBinder(view)::new NavigationBinding(view)::Nil
}


trait ShapeView extends WithShapeView with CollectionView
{


  override type Item = Var[ArcRule]
  override type ItemView = ArcView

  val removeClick = EventBinding.createMouseEvent()



  val rules: Rx[List[Var[ArcRule]]] = this.shape.map(sh=>sh.current.arcRules().map(a=>Var(a))) //TODO: fix in future

  override val items: Rx[List[Var[ArcRule]]] = rules.map(rl=>rl.sortBy(r=>r.now.priority))

  val newAnd: Rx[AndRule] = rules.map(r=>
    AndRule(r.map(v=>v.now).toSet,this.shapeRes)
  )

  override def newItem(item:Item):ItemView =
  {
    //dom.console.log(template.outerHTML.toString)
    val el = template.cloneNode(true).asInstanceOf[HTMLElement]

    el.removeAttribute("data-template")
    val mp: Map[String, Any] = Map[String,Any]("item"->item)

    val view: ItemView = el.attributes.get("data-item-view") match {
      case None=>
        ArcView.apply(el, mp)
      case Some(v)=> this.inject(v.value,el,mp) match {
        case iv:ItemView=> iv
        case _=>
          dom.console.error(s"view ${v.value} exists but does not inherit ItemView")
          ArcView.apply(el,mp)
      }
    }
    //item.handler(onItemChange(item))
    view

  }



  def save() = {
    val sh = shape.now
    shape() = sh.copy(current = sh.current.copy(rule = newAnd.now))
  }


}

