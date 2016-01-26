package org.denigma.preview

import org.denigma.binding.binders.Events
import org.denigma.binding.extensions._
import org.denigma.binding.views._
import org.denigma.semantic.binders._
import org.denigma.semantic.binders.binded.Typed
import org.denigma.semantic.extensions.GraphUpdate
import org.scalajs.dom.raw.Element
import org.w3.banana.{PointedGraph, RDF, RDFOps}
import rx._
import rx.Ctx.Owner.Unsafe.Unsafe


import scala.collection.immutable.SortedSet

class SemanticSelectBinder[Rdf<:RDF](graph: Var[PointedGraph[Rdf]], resolver: Resolver[Rdf]) extends RDFModelBinder[Rdf](graph, resolver)
{
  override def bindAttributes(el: Element, attributes: Map[String, String]): Boolean = {
    attributes.contains("property") //TODO: rewrite
  }
}

//case class SemanticSuggester()
case class TripleSelection[Rdf <: RDF](triple: Rdf#Triple)(val position: Int)(implicit ops: RDFOps[Rdf])
{

  import ops._


  lazy val value = ops.fromTriple(triple)._3

  lazy val label: String = ops.foldNode(value)(
   u => u.lastPathSegment,
   b => ops.fromBNode(b),
   l => {
     val (str, tp, lanOpt) = ops.fromLiteral(l)
     str
   }
  )
}
case class SelectTripleOption[Rdf<:RDF](item: Var[TripleSelection[Rdf]], origin: BindableView, latest: BasicView) extends ViewEvent{

  override def withCurrent(cur: BasicView): SelectTripleOption[Rdf] = {
    copy(latest = cur)
  }

}

class SemanticOptionView[Rdf<:RDF](val elem: Element, item: Var[TripleSelection[Rdf]], val params: Map[String, Any]) extends BindableView
{

  val label: Rx[String] = item.map(_.label)
  val position: Rx[Int] = item.map(_.position)
  val order: Rx[String] = item.map(_.position.toString)
  def onSelect():Unit = {
    fire(SelectTripleOption[Rdf](item,this,this))
  }

  val select = Var(Events.createMouseEvent())

  select.onChange{
    case ev=> onSelect()
  }

}

class SemanticSelectionView[Rdf<:RDF](val elem: Element,
                                      val graph: Var[PointedGraph[Rdf]],
                                      val graphUpdates: Rx[GraphUpdate[Rdf]],
                                      //val suggester:  TypedSuggester,
                                      val params: Map[String, Any])(implicit ops: RDFOps[Rdf])
  extends  ItemsSetView
{


  implicit val varOrdering:Ordering[rx.Var[TripleSelection[Rdf]]] = new Ordering[Var[TripleSelection[Rdf]]]{
    override def compare(x: Var[TripleSelection[Rdf]], y: Var[TripleSelection[Rdf]]): Int = if(x.now.position<y.now.position)
      -1 else if(x.now.position > y.now.position) 1 else 0
  }
  implicit val selectionOrdering:Ordering[TripleSelection[Rdf]] = new Ordering[TripleSelection[Rdf]]{
    override def compare(x: TripleSelection[Rdf], y: TripleSelection[Rdf]): Int = if(x.position<y.position)
      -1 else if(x.position > y.position) 1 else 0
  }
  override type Item = Var[TripleSelection[Rdf]]

  override type ItemView = SemanticOptionView[Rdf]

  override lazy val items = Var(SortedSet.empty[Item])

  val typed = Var(Typed[Rdf](graph.now, ""))

  /**
   * Adds subscription
   */
  override protected def subscribeUpdates(): Unit = {
    val g = graph.now

    //this.items.now.foreach(i=>this.addItemView(i,this.newItem(i))) //initialization of views
/*    updates.onChange("ItemsUpdates")(upd=>{
      upd.added.foreach(onInsert)
      upd.removed.foreach(onRemove)
    })*/
  }

  override def newItemView(item: Item):ItemView = this.constructItemView(item){case (el, mp) =>
      new SemanticOptionView[Rdf](el, item, mp)
  }


}
