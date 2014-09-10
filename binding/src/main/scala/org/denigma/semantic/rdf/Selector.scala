package org.denigma.semantic.rdf

import org.denigma.binding.extensions._
import org.scalajs.dom
import org.scalajs.dom.HTMLElement
import org.scalajs.selectize._
import org.scalax.semweb.rdf._

import scala.scalajs.js


trait Selector {

  def key:IRI
  def el:HTMLElement

  val sel: js.Dynamic


  /**
   * Parses string to get RDF value
   * @param str
   * @return
   */
  def parseRDF(str:String) = str match {
    case s if str.contains("_:") => BlankNode(str)
    case s if str.contains(":") => IRI(s)
    case s if str.contains("^^")=>dom.console.error("any lit is not yet implemented for selectview")
      AnyLit(str)
    case s => StringLiteral(s)

  }


  protected def makeOption(v:RDFValue): js.Dynamic =  js.Dynamic.literal( id = v.stringValue, title = v.label)


  protected def itemAddHandler(value:String, item:js.Any): Unit
  protected def itemRemoveHandler(value:String): Unit


  protected def selectParams(el: HTMLElement):js.Dynamic



  protected def selectizeFrom(el:HTMLElement): Selectize = {
    val s = el.dyn.selectize
    s.asInstanceOf[Selectize]
  }


  protected def makeOptions(properties:Map[IRI,Set[RDFValue]],iri:IRI):js.Array[js.Dynamic] =
    properties.get(iri) match {
      case Some(iris)=>
        val o: List[js.Dynamic] = iris.map(i=> makeOption(i)).toList
        js.Array( o:_* )
      case None=> js.Array()
    }


  def updateOptions(opts:List[RDFValue]): Unit = {

    val ss = selectizeFrom(el)
    for {
      r <- ss.options.keys.filter{case k => !ss.items.contains(k)}
    } ss.options.remove(r)
    opts.foreach { o =>
      ss.addOption(makeOption(o))
    }
    ss.refreshItems()


  }
}
