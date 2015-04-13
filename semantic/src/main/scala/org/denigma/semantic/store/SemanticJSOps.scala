package org.denigma.semantic.store

import org.w3.banana.n3js._

import org.w3.banana.n3js.N3.Util
import org.w3.banana.{n3js, plantain, DefaultURIOps, RDFOps}

import java.util.UUID
import org.w3.banana.isomorphism._

object SemanticJSOps extends RDFOps[SemanticJS] with SemanticJSMGraphOps with DefaultURIOps[SemanticJS] {

  // graph

  final val emptyGraph: SemanticJS#Graph = plantain.model.Graph(Map.empty, 0)

  final def makeGraph(triples: Iterable[SemanticJS#Triple]): SemanticJS#Graph =
    triples.foldLeft(emptyGraph) { case (g, (s, p, o)) => g + (s, p, o) }

  final def getTriples(graph: SemanticJS#Graph): Iterable[SemanticJS#Triple] = graph.triples

  def graphSize(graph: SemanticJS#Graph): Int = graph.size

  // triple

  final def makeTriple(s: SemanticJS#Node, p: SemanticJS#URI, o: SemanticJS#Node): SemanticJS#Triple =
    (s, p, o)

  final def fromTriple(t: SemanticJS#Triple): (SemanticJS#Node, SemanticJS#URI, SemanticJS#Node) = t

  // node

  final def foldNode[T](
                         node: SemanticJS#Node)(
                         funURI: SemanticJS#URI => T,
                         funBNode: SemanticJS#BNode => T,
                         funLiteral: SemanticJS#Literal => T
                         ): T = node match {
    case bnode @ n3js.BNode(_)          => funBNode(bnode)
    case s: String if Util.isIRI(s)     => funURI(s)
    case s: String if Util.isLiteral(s) => funLiteral(s)
  }

  // URI

  final def fromUri(uri: SemanticJS#URI): String = uri

  final def makeUri(s: String): SemanticJS#URI = s

  // bnode

  final def makeBNode(): SemanticJS#BNode =n3js.BNode(UUID.randomUUID().toString)

  final def makeBNodeLabel(label: String): SemanticJS#BNode = n3js.BNode(label)

  final def fromBNode(bnode: SemanticJS#BNode): String = bnode.label

  // literal

  final val __rdfLangString = makeUri("http://www.w3.org/1999/02/22-rdf-syntax-ns#langString")

  final def makeLiteral(lexicalForm: String, datatype: SemanticJS#URI): SemanticJS#Literal =
    Util.createLiteral(lexicalForm, datatype)

  final def makeLangTaggedLiteral(lexicalForm: String, lang: SemanticJS#Lang): SemanticJS#Literal =
    Util.createLiteral(lexicalForm, lang)

  final def fromLiteral(literal: SemanticJS#Literal): (String, SemanticJS#URI, Option[SemanticJS#Lang]) = {
    val lang = Util.getLiteralLanguage(literal)
    val langOpt = if (lang.isEmpty) None else Some(lang)
    (Util.getLiteralValue(literal), Util.getLiteralType(literal), langOpt)
  }

  // lang

  final def makeLang(langString: String): SemanticJS#Lang = langString

  final def fromLang(lang: SemanticJS#Lang): String = lang

  // graph traversal

  final val ANY: SemanticJS#NodeAny = null

  implicit def toConcreteNodeMatch(node: SemanticJS#Node): SemanticJS#NodeMatch = node

  final def foldNodeMatch[T](
                              nodeMatch: SemanticJS#NodeMatch)(
                              funANY: => T,
                              funConcrete: SemanticJS#Node => T
                              ): T = nodeMatch match {
    case null => funANY
    case node => funConcrete(node)
  }

  final def find(
                  graph: SemanticJS#Graph,
                  subject: SemanticJS#NodeMatch,
                  predicate: SemanticJS#NodeMatch,
                  objectt: SemanticJS#NodeMatch
                  ): Iterator[SemanticJS#Triple] = predicate match {
    case p: SemanticJS#URI => graph.find(Option(subject), Some(p), Option(objectt)).iterator
    case null            => graph.find(Option(subject), None, Option(objectt)).iterator
    case p               => sys.error(s"[find] invalid value in predicate position: $p")
  }

  // graph union

  final def union(graphs: Seq[SemanticJS#Graph]): SemanticJS#Graph = {
    var mgraph = makeEmptyMGraph()
    graphs.foreach(graph => addTriples(mgraph, graph.triples))
    mgraph.graph
  }

  final def diff(g1: SemanticJS#Graph, g2: SemanticJS#Graph): SemanticJS#Graph = {
    val mgraph = makeMGraph(g1)
    try { removeTriples(mgraph, g2.triples) } catch { case nsee: NoSuchElementException => () }
    mgraph.graph
  }

  // graph isomorphism

  final val iso = new GraphIsomorphism[SemanticJS](
    new SimpleMappingGenerator[SemanticJS](VerticeCBuilder.simpleHash(this))(this)
  )(this)

  final def isomorphism(left: SemanticJS#Graph, right: SemanticJS#Graph): Boolean = {
    iso.findAnswer(left, right).isSuccess
  }

}
