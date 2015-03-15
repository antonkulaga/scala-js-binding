package org.denigma.semantic.shapes

import org.denigma.binding.binders.extractors.EventBinding
import org.denigma.binding.binders.{GeneralBinder, NavigationBinding}
import org.denigma.binding.views.collections.CollectionView
import org.scalax.semweb.rdf.Res
import org.scalax.semweb.shex._
import rx._
import rx.core.Var
import rx.ops._

import scala.collection.immutable.{Map, SortedSet}
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


trait ShapeView extends CollectionView //with WithShapeView
{
  override type Item = Var[ArcRule]
  override type ItemView = ArcView

  val removeClick = EventBinding.createMouseEvent()

  val rules:Var[SortedSet[Item]] = Var(SortedSet.empty[Var[ArcRule]](ShapeView.ArcOrdering))

  //val rules:Var[Set[Item]] = Var(Set.empty[Var[ArcRule]])



  def updateShape(sh:Shape) = {
    val rs: Set[ArcRule] = sh.arcRules().toSet
    val insert: Set[rx.Var[ArcRule]] = (rs diff arcs.now).map(r=>Var(r))
    val its = rules.now.filter(i=>rs.contains(i.now))
    rules() =  its++insert
  }


  lazy val items: Rx[List[Item]] = rules.map(r=>r.toList)

  lazy val arcs: Rx[Set[ArcRule]] = rules.map(ru=>ru.map(r=>r.now))

  def shapeRes: Rx[Res]// = shapeResOption.getOrElse(Var(WI.PLATFORM.EMPTY))

  lazy val shape = Rx{
    val and = AndRule(rules().map(r=>r.now).toSet,shapeRes())
    Shape.apply(and.id,and)
  }

/*
  protected def shapeResOption: Option[Var[Res]] = this.resolveKeyOption("shape-resource"){
    case sh:String if sh.contains(":") =>Var(IRI(sh))
    case sh:Res =>Var(sh)
    case sh:Var[Res] => sh
    case _=> throw new Exception(s"shape param of unknown type in ShapeView $id")
  }
*/

  override def newItem(item:Item):ItemView = this.constructItem(item,Map("item"->item)) { (e,m)=> ArcView.apply(e,m) }

}

