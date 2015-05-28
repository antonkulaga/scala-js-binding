package org.denigma.semantic.store

import org.denigma.semweb.rdf._
import org.w3.banana.RDFOps
import org.w3.banana.io.{RDFReader, RDFWriter, Turtle}

import scala.concurrent.{Future, Promise}
import scala.scalajs.js

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
      val graph = Graph(triplets)
      //      writer.asString(g,"http://longevityalliance.org/resource/")
      def onParse(some:Any):Unit = { /*just a mandatory handler*/  }
      val promise = Promise[String]()
      /**
       * Finish callback
       * @param errors list of errors that occured during execution
       * @param output string with turtle
       */
      def finishedWriting(errors:Any,output:String):Unit = {
        promise.success(output)
      }
      val writer: TurtleJSWriter = new TurtleJSWriter()
      lazy val fun:js.Function1[Any,Unit] = onParse _
      namespaces.foreach{
        case (key,value)=>
          writer.addPrefix(key,value,fun)
      }

      graph.triples.foreach{
        case (sub,pred,obj)=>
          writer.addTriple(sub,pred,obj,fun)
      }
      writer.end(finishedWriting _)
      promise.future
    }
}
