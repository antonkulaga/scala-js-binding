package org.denigma.semantic.binders.binded

import org.denigma.binding.extensions._
import org.denigma.semantic.extensions.GraphUpdate
import org.scalajs.dom
import org.scalajs.dom.Element
import org.w3.banana._
import rx.Rx
import rx.Var
import rx._
import rx.Ctx.Owner.Unsafe.Unsafe
/**
 * For each binded property
 * @param graph RDF graph
 * @param updates
 * @param createIfNotExist
 * @param ops
 * @tparam Rdf
 */
abstract class Binded[Rdf<:RDF](graph: Var[PointedGraph[Rdf]],
                                updates: Rx[GraphUpdate[Rdf]],
                                createIfNotExist:Boolean = false
                                 )(ops: RDFOps[Rdf])
{

  def predicate: Rdf#URI

  protected def subject: Rdf#Node = graph.now.pointer

  def onUpdate(upd: GraphUpdate[Rdf]): Unit = if(upd.propertiesChanged.contains(this.predicate) || upd.pointerChanged){
      myObjects.set(objectsFromGraph)
    }

  protected def objectsFromGraph = ops.getObjects(graph.now.graph,subject,predicate).toSet

  protected val myObjects: Var[Set[Rdf#Node]] = Var(objectsFromGraph)

  //protected def log() = dom.console.log(s"predicate is ${predicate} and value is ${myObjects.now.mkString("\n")}\n and graph is ${graph.now.graph}")

  /**
   * Is used to subscribe to default behaviour
   */
  protected def subscribe(): Unit = { //in case if want to override it

    if(!propertyExists){
      if(createIfNotExist) {
        //if no properties exist then creates them
        updateFromHTML()
      } else dom.console.error(s"property $predicate does not exist in RDF graph")
    }
    //subscribes and runs myObjects change
    myObjects.foreach(onObjectsChange)
    //subscribes and runs updates
    updates.foreach(onUpdate)
  }

  protected def propertyString(html: Element, prop: String, default: String = "") = (html \ prop).fold(default)(d=>d.toString)

  def objectsFromHTML(): Set[Rdf#Node]

  def nodes2triplets(nodes: Set[Rdf#Node]): Set[Rdf#Triple] = {
    val sub = subject
    nodes.map(ops.makeTriple(sub,predicate,_))
  }

  protected def onObjectsChange(values: Set[Rdf#Node]): Unit

  import ops._
  protected def propertyExists = ops.find(graph.now.graph, subject, predicate, ops.ANY).hasNext


  /**
   * Updates graph from values extracted from HTML
   */
  protected def updateFromHTML(): Unit = {
    val oldValues = nodes2triplets(this.myObjects.now)
    val g = ops.makeMGraph(graph.now.graph)
    ops.removeTriples(g,oldValues)
    //extract new values from html
    val newValues = this.objectsFromHTML()
    myObjects.Internal.value = newValues
    val newTriplets = nodes2triplets(newValues)
    ops.addTriples(g,newTriplets)
    graph() = PointedGraph[Rdf](subject,ops.makeIGraph(g))
  }


}
