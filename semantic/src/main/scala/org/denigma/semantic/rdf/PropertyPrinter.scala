package org.denigma.semantic.rdf

import org.scalax.semweb.rdf._

/**
 * Contains some functions that print properties in a nice way
 */
trait PropertyPrinter {


  /**
   * prints RDF in a pretty way
   * @param value
   * @return
   */
  def prettyString(value:RDFValue) = value match {
    case lit:Lit=>
      lit.label
    case other=>other.stringValue
  }

  /**
   * Defines what to do with many rdf values
   */
  val onMany:Set[RDFValue]=>String = (values)=>{
    values.foldLeft("") { case (acc, prop) => acc + this.prettyString(prop) + "; "}
  }

  /**
   * Set of values to string
   * @param values
   * @return
   */
  def vals2String(values: Set[RDFValue],onZero:String="",onOne:RDFValue=>String = this.prettyString _, onMany:Set[RDFValue]=>String = this.onMany): String = values.size match {
    case 0 => onZero
    case 1 => onOne(values.head)
    case _ => this.onMany(values)
  }

  protected def printProp(prop:Set[RDFValue],delimiter:String="; ") = {
    prop.foldLeft("") { case (acc, prop) => acc + prop.label + delimiter}
  }

  protected val manyNames: Set[RDFValue]=>String = (values)=> printProp(values)
}
