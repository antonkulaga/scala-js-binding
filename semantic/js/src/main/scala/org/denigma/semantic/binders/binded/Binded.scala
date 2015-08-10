package org.denigma.semantic.binders.binded

import org.denigma.binding.extensions._
import org.denigma.semantic.extensions.GraphUpdate
import org.scalajs.dom
import org.scalajs.dom.raw.HTMLElement
import org.w3.banana._
import rx.Rx
import rx.core.Var

abstract class Binded[Rdf<:RDF](graph:Var[PointedGraph[Rdf]],
                                updates:Rx[GraphUpdate[Rdf]],
                                createIfNotExist:Boolean = false
                                 )(ops:RDFOps[Rdf])
{

  def predicate:Rdf#URI

  protected def subject: Rdf#Node = graph.now.pointer

  def onUpdate(upd:GraphUpdate[Rdf]): Unit = if(upd.propertiesChanged.contains(this.predicate) || upd.pointerChanged){
      myObjects.set(objectsFromGraph)
    }



  protected def objectsFromGraph = ops.getObjects(graph.now.graph,subject,predicate).toSet

  protected val myObjects: Var[Set[Rdf#Node]] = Var(objectsFromGraph)

  //protected def log() = dom.console.log(s"predicate is ${predicate} and value is ${myObjects.now.mkString("\n")}\n and graph is ${graph.now.graph}")

  protected def init():Unit = { //in case if want to override it
    import rx.ops._
    if(!propertyExists){
      if(createIfNotExist) {
        updateFromHTML()
      } else dom.console.error(s"property $predicate does not exist in RDF graph")
    }
    myObjects.foreach(onObjectsChange)
    updates.foreach(onUpdate)

  }

  protected def propertyString(html:HTMLElement,prop:String,default:String = "") = (html \ prop).fold(default)(d=>d.toString)

  def objectsFromHTML():Set[Rdf#Node]

  //useful for productin labels
  def node2label(node:Rdf#Node) = ops.foldNode(node)(
    ops.lastSegment,
    ops.fromBNode,
    l=> ops.fromLiteral(l)._1
  )

  
  def node2string(node:Rdf#Node) = ops.foldNode(node)(
    ops.fromUri,
    ops.fromBNode,
    l=> ops.fromLiteral(l) match {
      case (str,t,Some(lang))=>s"$str@$lang"
      case (str,t,_)=>s"$str^^$t"
    }
  )

  def orderedUpdate[T](where:Seq[T],from:Set[T]) = {
    val w = where.toSet
    if(w==from)
      where
    else{
        val (minus:Set[T],plus:Set[T]) = (w.diff(from),from.diff(w))
        where.filterNot(minus.contains)  ++ plus
    }
  }


  def nodes2triplets(nodes:Set[Rdf#Node]): Set[Rdf#Triple] = {
    val sub = subject
    nodes.map(ops.makeTriple(sub,predicate,_))
  }

  def string2node(string:String) = string match {
    case lit if lit.contains("\"^^")=>
      val ind = lit.indexOf("\"^^")
      ops.makeLiteral(lit.substring(0,ind),ops.makeUri(lit.substring(ind+3,lit.length)))

    case lit if lit.contains("\"@")=>
      val ind = lit.indexOf("\"@")
      ops.makeLiteral(lit.substring(0,ind),ops.makeUri(lit.substring(ind+2,lit.length)))

    case uri if uri.contains(":")=> ops.makeUri(uri.replace(' ','_'))

    case other => ops.makeLiteral(other,ops.xsd.string)
  }


  protected def onObjectsChange(values: Set[Rdf#Node]): Unit

  import ops._
  protected def propertyExists = ops.find(graph.now.graph, subject, predicate, ops.ANY).hasNext


  /**
   * Updates graph from values extracted from HTML
   */
  protected def updateFromHTML() = {

    val oldValues = nodes2triplets(this.myObjects.now)
    val g = ops.makeMGraph(graph.now.graph)
    ops.removeTriples(g,oldValues)
    val newValues = this.objectsFromHTML()
    myObjects.updateSilent(newValues)
    val newTriplets = nodes2triplets(newValues)
    ops.addTriples(g,newTriplets)
    graph() = PointedGraph[Rdf](subject,ops.makeIGraph(g))
  }

  init()


}
