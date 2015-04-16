package org.denigma.semantic.store

import org.w3.banana._
import org.w3.banana.io.{RDFWriter, RDFReader, Turtle}
import org.w3.banana.n3js.{N3js, N3jsOps}
import org.w3.banana.TurtleWriterModule

import scala.util.Try
import scalajs.concurrent.JSExecutionContext.Implicits.queue

import scala.concurrent.Future

/** A N3.js-based implementation of the RDF model.
  *
  * For now, only the URIs and Literal are natively handled by N3.js.
  */
trait SemanticJS extends RDF {

  // types related to the RDF datamodel
  type Graph = plantain.model.Graph[Node, URI, Node]
  type Triple = (Node, URI, Node)
  type Node = Any
  type URI = String
  type BNode = n3js.BNode
  type Literal = String
  type Lang = String

  type MGraph = n3js.MGraph[Node, URI, Node]

  // types for the graph traversal API
  type NodeMatch = Node
  type NodeAny = Null

}

object SemanticJS extends RDFModule
with RDFOpsModule
with RecordBinderModule
//with TurtleReaderModule
//with TurtleWriterModule
{

  type Rdf = SemanticJS

  override implicit val ops: RDFOps[SemanticJS] = SemanticJSOps

  implicit val recordBinder: binder.RecordBinder[SemanticJS] = binder.RecordBinder[SemanticJS]

  implicit val turtleReader:org.w3.banana.io.RDFReader[SemanticJS,Future,Turtle] = new n3js.io.N3jsTurtleParser[SemanticJS]

  implicit val turtleWriter: RDFWriter[Rdf, Future, Turtle] = new TurtleWriter
}

