package org.denigma.semantic.shapes

import org.denigma.binding.binders.{NavigationBinding, GeneralBinder}
import org.denigma.binding.extensions.sq
import org.denigma.binding.binders.extractors.EventBinding
import org.denigma.binding.views.{Injector, CollectionView, IView, BindableView}
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


trait ShapeView extends BindableView with CollectionView
{

  lazy val initialShape = {
    //require(params.contains("shape"),"ShapeView must contain shape in params")
    this.params.get("shape").map{
      case sh:Shape=> sh
      case id:Res=> Shape(id,AndRule.empty)
      case _ =>
        debug("something else was found")
        Shape.empty
    }.getOrElse{
      debug("no shape or shape resource in params")
      Shape.empty
    }
  }

  val resource = Var(initialShape.id.asResource)


  val shapeInside = Var(ShapeInside(initialShape))



  override type Item = Var[ArcRule]
  override type ItemView = ArcView

  val removeClick = EventBinding.createMouseEvent()


  val rules: Var[List[Var[ArcRule]]] = Var(List.empty[Var[ArcRule]])


  override val items: Rx[List[Var[ArcRule]]] = Rx{rules().sortBy(r=>r().priority)}

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


  protected def updateShape(shape:Shape) = {
    resource() = shape.id.asResource
    this.rules() = shape.arcSorted() map (Var(_))
  }

  def save() = {
    val sh = shapeInside.now
    shapeInside() = sh.copy(initial=sh.current)
  }


  val currentShape = Rx{
    val label = resource() match {
      case iri:IRI=>IRILabel(iri)
      case node:BlankNode=>BNodeLabel(node)
    }
    Shape(label,new AndRule(items().map(its=>its.now).toSet,label) )
  }

  currentShape.handler{
    //TODO rewrite in a safer way
    if(shapeInside.now.current!=currentShape.now) shapeInside() = shapeInside.now.copy(current =currentShape.now)
  }

}

