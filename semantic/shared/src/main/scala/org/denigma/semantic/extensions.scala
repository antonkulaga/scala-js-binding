package org.denigma.semantic


import org.w3.banana.{RDFOps, PointedGraph, RDF}
import rx._
import org.denigma.binding.extensions._

object extensions {

  case class GraphUpdate[Rdf<:RDF](from:PointedGraph[Rdf],to:PointedGraph[Rdf])(implicit ops:RDFOps[Rdf]){
    def previousPointer = from.pointer
    def currentPointer = to.pointer
    import ops._

    lazy val removed: Rdf#Graph = ops.diff(from.graph,to.graph)
    lazy val added: Rdf#Graph = ops.diff(to.graph,from.graph)

    private[this] lazy val allDiffs = ops.union(Seq(removed,added))


    lazy val propertiesChanged: Set[Rdf#URI] = ops.getTriples(allDiffs).map(ops.fromTriple(_)._2).toSet

    def dataChanged: Boolean = ops.graphSize(removed) + ops.graphSize(added)>0
    def pointerChanged: Boolean = from.pointer!=to.pointer
    def changed: Boolean =  pointerChanged || dataChanged

  }

  implicit class GraphRx[Rdf<:RDF](graph:Rx[PointedGraph[Rdf]])(implicit ops:RDFOps[Rdf])
  {

    def updates: Rx[GraphUpdate[Rdf]] = graph.zip((a,b)=>GraphUpdate[Rdf](a,b)(ops))

  }


}
