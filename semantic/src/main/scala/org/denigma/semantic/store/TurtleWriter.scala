package org.denigma.semantic.store

import java.io.OutputStream

import org.w3.banana.RDFOps
import org.w3.banana.io.{RDFWriter, Turtle}

import scala.concurrent.{Future, Promise}
import scala.scalajs.js
import scala.scalajs.js.annotation.JSName

@JSName("N3.Writer")
class TurtleJSWriter extends js.Object {
  def addTriple(sub:SemanticJS#Node,prop:SemanticJS#URI,obj:SemanticJS#Node,graph:String,done:js.Function1[Any,Unit]):Unit = js.native

  def addTriple(sub:SemanticJS#Node,prop:SemanticJS#URI,obj:SemanticJS#Node,done:js.Function1[Any,Unit]):Unit = js.native

  def addTriple(obj:SemanticJS#Triple):Unit = js.native

  def addTriples(triples:js.Array[SemanticJS#Triple]):Unit = js.native

  def end(done:js.Function2[Any,String,Unit]):Unit = js.native

  def addPrefix(prefix:String, iri:String, done:js.Function1[Any,Unit]):Unit = js.native

  def addPrefixes(prefixes:js.Array[String], done:js.Function1[Any,Unit]) = js.native
}


class TurtleWriter extends RDFWriter[SemanticJS, Future, Turtle]
{
  override def write(graph: SemanticJS#Graph, os: OutputStream, base: String): Future[Unit] = {
    Future.successful(Unit)
  }

  override def asString(graph: SemanticJS#Graph, base: String): Future[String] = {
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
    graph.triples.foreach{
      case (sub,pred,obj)=>
        writer.addTriple(sub,pred,obj,fun)
    }
    writer.end(finishedWriting _)
    promise.future
  }
}
