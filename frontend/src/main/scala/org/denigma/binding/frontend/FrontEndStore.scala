package org.denigma.binding.frontend

import java.io.StringWriter

import org.denigma.semantic.store.SemanticJS
import org.scalax.semweb.rdf._
import org.w3.banana.{RDF, RDFOps, RDFStore}
import org.w3.banana.io.{RDFReader, RDFWriter, Turtle}

import scala.concurrent.Future
import scala.util.{Failure, Success, Try}


object FrontEndStore
{

  val ops: RDFOps[SemanticJS] = implicitly[RDFOps[SemanticJS]]
  val reader = implicitly[RDFReader[SemanticJS,Future,Turtle]]
  val writer = implicitly[RDFWriter[SemanticJS,Try,Turtle]]
  import ops._

  def rdfValue2JS(res:RDFValue) = res match {
    case res:Res => res.stringValue
    case lit:Lit if lit.stringValue.contains("^^")=> lit.stringValue
    case lit:Lit => "\""+lit.stringValue+"\""
  }

  def write[T <: BasicTriplet](trips: Set[T], namespaces: (String, String)*): Try[String] = {
      val triplets = for{t <- trips}  yield ops.Triple(t.sub.stringValue,  t.pred.stringValue,  rdfValue2JS(t.obj))
      val g = Graph(triplets)
      writer.asString(g,"http://longevityalliance.org/resource/")
  }
}
