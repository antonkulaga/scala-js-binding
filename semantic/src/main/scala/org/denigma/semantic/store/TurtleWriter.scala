package org.denigma.semantic.store

import java.io.OutputStream

import org.scalajs.dom
import org.w3.banana.io.{RDFWriter, Turtle}
import org.w3.banana.{RDFOps, n3js}
import org.w3.banana.n3js.N3
import org.denigma.binding.extensions._

import scala.concurrent.{Promise, Future}
import scala.scalajs.js
import scala.scalajs.js.JSON
import scala.scalajs.js.annotation.JSName
import scala.util.{Success, Try}
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

@JSName("N3.Writer")
class TurtleJSWriter extends js.Object {
  def addTriple(sub:SemanticJS#Node,prop:SemanticJS#URI,obj:SemanticJS#Node,graph:String,done:js.Function1[Any,Unit]):Unit = js.native
  def addTriple(sub:SemanticJS#Node,prop:SemanticJS#URI,obj:SemanticJS#Node,done:js.Function1[Any,Unit]):Unit = js.native

  def addTriple(obj:SemanticJS#Triple):Unit = js.native
  def addTriples(triples:js.Array[SemanticJS#Triple]):Unit = js.native
  def end(done:js.Function2[Any,String,Unit]):Unit = js.native
}


class TurtleWriter extends RDFWriter[SemanticJS, Future, Turtle]
{
  implicit val ops = implicitly[RDFOps[SemanticJS]]

  override def write(graph: SemanticJS#Graph, os: OutputStream, base: String): Future[Unit] = {
    Future.successful(Unit)
  }

  protected def test(errors:Any,result:Any):Unit = {
    //dom.console.error("WRITER SOMEHOW WORKS!")
    //dom.console.error(s"result is: "+result)

  }

  def onParse(some:Any):Unit = {
    //println("ON PARSE SUCCEED")
  }

  override def asString(graph: SemanticJS#Graph, base: String): Future[String] = {

    val writer: TurtleJSWriter = new TurtleJSWriter()
    //dom.console.log(writer.dyn.addTriple)
    //graph.triples.foreach(t=>println(t))
    lazy val fun:js.Function1[Any,Unit] = onParse _

    graph.triples.foreach{
      case (sub,pred,obj)=>
        writer.addTriple(sub,pred,obj,fun)
    }
    val promise = Promise[String]()
    def finishedWriting(errors:Any,output:String):Unit = {
      promise.success(output)
    }
    writer.end(finishedWriting _)
    promise.future
  }



  /*

  var writer = N3.Writer({ prefixes: { 'c': 'http://example.org/cartoons#' } });
writer.addTriple('http://example.org/cartoons#Tom',
                 'http://www.w3.org/1999/02/22-rdf-syntax-ns#type',
                 'http://example.org/cartoons#Cat');
writer.addTriple({
  subject:   'http://example.org/cartoons#Tom',
  predicate: 'http://example.org/cartoons#name',
  object:    '"Tom"'
});
writer.end(function (error, result) { console.log(result); });

   */
}
