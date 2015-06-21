package org.denigma.semantic.shapes

import org.denigma.binding.binders.extractors.EventBinding
import org.denigma.binding.binders.{GeneralBinder, NavigationBinding}
import org.denigma.binding.views.{BindableView, BasicView, BindingEvent}
import org.denigma.binding.views.collections.CollectionView
import org.denigma.semweb.rdf.Res
import org.denigma.semweb.shex._
import org.scalajs.dom
import prickle.Pickle
import rx._
import rx.core.Var
import rx.ops._
import org.denigma.binding.extensions._
import scala.collection.immutable.{Map, SortedSet}


case class RemoveArcCommand(origin:ArcView,latest:BasicView) extends  ArcUpdateCommand(origin,latest)
{
  override def withCurrent(cur: BasicView):this.type = this.copy(latest = cur).asInstanceOf[this.type]
}

case class AddArcCommand(origin:ArcView,latest:BasicView) extends  ArcUpdateCommand(origin,origin)
{
  override def withCurrent(cur: BasicView):this.type = this.copy(latest = cur).asInstanceOf[this.type]
}

abstract class  ArcUpdateCommand(origin:ArcView,latest:BasicView) extends BindingEvent{
  type Origin = ArcView

  override val bubble: Boolean = false
}


object ShapeView
{
  def defaultBinders(view:ShapeView) = new GeneralBinder(view)::new NavigationBinding(view)::Nil

  implicit object ArcOrdering extends Ordering[Var[ArcRule]]{
    override def compare(x: Var[ArcRule], y: Var[ArcRule]): Int = {
      val (a,b) = (x.now,y.now)
      if(a==b) 0 else if(a.priority.getOrElse(0)<b.priority.getOrElse(0)) -1 else 1
    }
  }

}

/**
 * View that deals with Semantic shapes
 */
trait ShapeView extends CollectionView //with WithShapeView
{
  override type Item = Var[ArcRule]
  override type ItemView = ArcView

  def removeShapeHandler() = {
  }

  val removeClick = Var(EventBinding.createMouseEvent())
  removeClick.handler{
    removeShapeHandler()
  }

  val rules:Var[SortedSet[Item]] = Var(SortedSet.empty[Var[ArcRule]](ShapeView.ArcOrdering))


  protected def onArcUpdateCommands:PartialFunction[BindingEvent,Unit] = {

    case removeArc:RemoveArcCommand=>this.rules() = this.rules.now - removeArc.origin.arc
    case addArc:AddArcCommand=> this.rules() = this.rules.now + addArc.origin.arc
  }
  /**
   * Event subsystem
   * @return
   */
  override def receive:PartialFunction[BindingEvent,Unit] = this.onArcUpdateCommands.orElse(super.receive)

  /**
   * Transformts
   */
  def shapeString: String = {
    import org.denigma.binding.composites.BindingComposites._
    Pickle.intoString[Shape](this.shape.now)
  }

  def updateShape(sh:Shape) = {
    val rs: Set[ArcRule] = sh.arcRules().toSet
    val insert: Set[rx.Var[ArcRule]] = (rs diff arcs.now).map(r=>Var(r))
    val its = rules.now.filter(i=>rs.contains(i.now))
    rules() =  its++insert
  }


  lazy val items: Rx[List[Item]] = rules.map(r=>r.toList)

  lazy val arcs: Rx[Set[ArcRule]] = rules.map(ru=>ru.map(r=>r.now))

  def shapeRes: Rx[Res]// = shapeResOption.getOrElse(Var(WI.PLATFORM.EMPTY))

  lazy val shape: Rx[Shape] = Rx{
    val and = AndRule(rules().map(r=>r.now).toSet,shapeRes())
    Shape.apply(and.id,and)
  }


  override def newItem(item:Item):ItemView = this.constructItemView(item,Map("item"->item)) { (e,m)=> ArcView.apply(e,m) }

}

