package org.denigma.models

import org.scalax.semweb.shex.{ArcRule, PropertyModel}
import org.scalax.semweb.rdf._
import javax.annotation.Resource
import org.scalax.semweb.rdf.IRI
import org.scalax.semweb.rdf.StringLiteral
import scala.Some
import org.scalax.semweb.rdf.StringLangLiteral


object ModelInside
{
  def apply(initial:PropertyModel):ModelInside = ModelInside(initial,initial)
}

case class ModelInside(initial:PropertyModel, current:PropertyModel){


  def isUnchanged = initial==current


  /**
   * Updates in a list of values
   * @param iri
   * @param value
   * @return
   */
  def add(iri:IRI,value:RDFValue) = this.current.properties.get(iri) match {
    case Some(values)=> this.copy(initial,current.copy(  properties = this.current.properties.updated(iri,values+value )))
    case None =>this.replace(iri,value)
  }
  def add(iri:IRI,text:String,lang:String): ModelInside  = this.add(iri,StringLangLiteral(text,lang))
  def add(iri:IRI,text:String): ModelInside  = this.add(iri,StringLiteral(text))
  def add(iri:IRI,value:Double): ModelInside  = this.add(iri,DoubleLiteral(value))
  def add(iri:IRI,value:Long): ModelInside  = this.add(iri,LongLiteral(value))

  /**
   * Replaces to one value
   * @param iri
   * @param value
   * @return
   */
  def replace(iri:IRI,value:RDFValue): ModelInside = this.copy(initial,current.copy(properties = this.current.properties.updated(iri,Set(value))))
  def replace(iri:IRI,text:String,lang:String): ModelInside  = this.replace(iri,StringLangLiteral(text,lang))
  def replace(iri:IRI,text:String): ModelInside  = this.replace(iri,StringLiteral(text))
  def replace(iri:IRI,value:Double): ModelInside  = this.replace(iri,DoubleLiteral(value))
  def replace(iri:IRI,value:Long): ModelInside  = this.replace(iri,LongLiteral(value))

  def delete(iri:IRI) = this.copy()



  def refresh = this.copy(initial = current)
}
