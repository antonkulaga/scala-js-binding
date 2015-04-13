package org.denigma.semantic.store

import org.w3.banana.MGraphOps
import org.w3.banana.n3js._
import org.w3.banana.plantain
import org.w3.banana._


trait SemanticJSMGraphOps extends MGraphOps[SemanticJS] {

  def makeMGraph(graph: SemanticJS#Graph): SemanticJS#MGraph = new MGraph(graph)
  
  def makeEmptyMGraph(): SemanticJS#MGraph = new MGraph(plantain.model.Graph.empty)
  
  def addTriple(mgraph: SemanticJS#MGraph, triple: SemanticJS#Triple): mgraph.type = {
    val (s, p, o) = triple
    mgraph.graph += (s, p, o)
    mgraph
  }
  
  def removeTriple(mgraph: SemanticJS#MGraph, triple: SemanticJS#Triple): mgraph.type = {
    val (s, p, o) = triple
    mgraph.graph -= (s, p, o)
    mgraph
  }
  
  def exists(mgraph: SemanticJS#MGraph, triple: SemanticJS#Triple): Boolean = {
    val (s, p, o) = triple
    mgraph.graph.find(Some(s), Some(p), Some(o)).nonEmpty
  }
  
  def sizeMGraph(mgraph: SemanticJS#MGraph): Int = mgraph.graph.size
  
  def makeIGraph(mgraph: SemanticJS#MGraph): SemanticJS#Graph = mgraph.graph

}
