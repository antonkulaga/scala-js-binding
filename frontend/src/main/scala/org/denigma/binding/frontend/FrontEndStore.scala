package org.denigma.binding.frontend

import java.io.StringWriter

import org.denigma.semantic.store.SemanticJS
import org.scalax.semweb.rdf._
import org.w3.banana.{RDF, RDFOps, RDFStore}
import org.w3.banana.io.{RDFReader, RDFWriter, Turtle}

import scala.concurrent.Future
import scala.util.{Failure, Success, Try}
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

object FrontEndStore
{

  val ops: RDFOps[SemanticJS] = implicitly[RDFOps[SemanticJS]]
  val reader = implicitly[RDFReader[SemanticJS,Future,Turtle]]
  val writer = implicitly[RDFWriter[SemanticJS,Future,Turtle]]
  import ops._

  def res2ValueJS(res:Res) = res match {
    case iri:IRI=>iri.stringValue
    case bnode:BlankNode=>bnode.toString
  }

  def lit2ValueJS(literal:Lit) = literal match {
    case lit:Lit if lit.stringValue.contains("^^")=> lit.stringValue
    case lit:Lit => "\""+lit.stringValue+"\""
  }


  def rdfValue2JS(res:RDFValue) = res match {
    case res:Res => res2ValueJS(res)
    case lit:Lit =>lit2ValueJS(lit)
  }

  def write[T <: BasicTriplet](trips: Set[T], namespaces: (String, String)*): Future[String] = {
      val triplets = for{t <- trips}  yield ops.Triple(res2ValueJS(t.sub),  t.pred.stringValue,  rdfValue2JS(t.obj))
      val g = Graph(triplets)
      writer.asString(g,"http://longevityalliance.org/resource/")
  }
}
