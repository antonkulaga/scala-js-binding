package org.denigma.semantic.store

import java.io.OutputStream

import org.scalajs.dom
import org.w3.banana.io.{RDFWriter, Turtle}
import org.w3.banana.{RDFOps, n3js}
import org.w3.banana.n3js.N3
import org.denigma.binding.extensions._

import scala.scalajs.js
import scala.scalajs.js.JSON
import scala.util.{Success, Try}

object Writer extends js.Object {

  def addTriple(subject:SemanticJS#Node,sub:SemanticJS#Node,prop:SemanticJS#URI,obj:SemanticJS#Node,done:Any) = js.native
  def addTriple(obj:SemanticJS#Triple) = js.native

  def addTriples(triples:js.Array[SemanticJS#Triple]) = js.native

  def end(done:js.Function2[Any,Any,Unit]) = js.native

}

class Writer(writerSettings:Any) extends js.Object {



}


class TurtleWriter extends RDFWriter[SemanticJS, Try, Turtle]
{
  implicit val ops = implicitly[RDFOps[SemanticJS]]

  override def write(graph: SemanticJS#Graph, os: OutputStream, base: String): Try[Unit] = {
    Success(Unit)
  }

  protected def test(errors:Any,result:Any):Unit = {
    dom.console.error("WRITER SOMEHOW WORKS!")
    //dom.console.error(s"result is: "+result)

  }

  override def asString(graph: SemanticJS#Graph, base: String): Try[String] = {

    val writer = N3.dyn.Writer().asInstanceOf[Writer]
    graph.triples.foreach(t=>Writer.addTriple(t))
    //graph.triples.foreach(writer.addTriple)
    Try("")
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
