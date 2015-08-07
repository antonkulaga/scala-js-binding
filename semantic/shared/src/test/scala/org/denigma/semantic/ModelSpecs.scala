package org.denigma.semantic

import org.scalatest._
import org.w3.banana.plantain.Plantain
import org.w3.banana.{PointedGraph, RDF, RDFOps}
import org.denigma.binding.extensions._
import org.denigma.semantic.extensions._
import rx.Rx
import rx.core.Var

class PlantainModelSpec extends ModelSpec[Plantain]{

}


abstract class ModelSpec[Rdf <: RDF](implicit ops: RDFOps[Rdf])  extends WordSpec with Matchers{
  import org.w3.banana.diesel._
  import ops._
  
  def exuri(foo: String = "foo"): Rdf#URI = URI("http://example.com/" + foo)
  
  val foo1gr: Rdf#Graph = Graph(Triple(exuri(), rdf("foo"), Literal("foo")))
  val bar1gr: Rdf#Graph = Graph(Triple(exuri(), rdf("bar"), Literal("bar")))

  def exbnode(n: Int = 1): Rdf#Node = BNode("ex" + n)

  def bnNameGr(n: Int = 1, name: String) = Graph(Triple(exbnode(n), rdf("knows"), Literal(name)))

  val foo = (
    exuri()
      -- rdf("foo") ->- "foo"
      -- rdf("bar") ->- "bar"
    ).graph

  val bar = (
    exuri()
      -- rdf("bar") ->- "bar"
      -- rdf("baz") ->- "baz"
    ).graph

  val foobar = (
    exuri()
      -- rdf("foo") ->- "foo"
      -- rdf("bar") ->- "bar"
      -- rdf("baz") ->- "baz"
    ).graph


  case class Pointed(pointer:Rdf#URI,graph:Rdf#Graph) extends PointedGraph[Rdf]

  "Reactive RDF models" should{
/*
      "should search well in an empty graph" in {
        val g = Var(Pointed(exuri(),ops.emptyGraph))
        val obj1 = ops.getObjects(g.now.graph,g.now.pointer,ops.makeUri("one:test")).toSet
        val obj2 = ops.getObjects(g.now.graph,g.now.pointer,ops.makeUri("one:test")).toSet
        println(obj1)
        println(obj2)

      }*/


      "graph update should produce deletions and additions" in {
        val g = Var(Pointed(exuri(),foobar))
        val u: Rx[GraphUpdate[Rdf]] = g.updates
        g() = Pointed(exuri(),bar)
        val firstDeletion = (exuri()-- rdf("foo") ->- "foo").graph
        val firstUpdate = u.now
        assert(firstUpdate.removed.isIsomorphicWith(firstDeletion))
        g() = Pointed(exuri(),bar)
        assert(firstUpdate==u.now)
        println("PREVIOUS = "+g.now.graph.triples.mkString("\n"))
        g() = Pointed(exuri(),foo)
        val secondAdded =  (exuri()
          -- rdf("foo") ->- "foo")
        val secondRemoved =  (exuri()
          -- rdf("baz") ->- "baz")
        val secondUpdate = u.now
        assert(firstUpdate!=secondUpdate)
        assert(!u.now.added.isIsomorphicWith(firstDeletion))
        println("ADDED: \n"+u.now.added.triples.mkString("\n"))
        println("REMOVED: \n"+u.now.removed.triples.mkString("\n"))

        //assert(u.now.added.isIsomorphicWith(secondAdded.graph))
        //TODO:complete
        assert(u.now.removed.isIsomorphicWith(secondRemoved.graph))
      }
  }

}
