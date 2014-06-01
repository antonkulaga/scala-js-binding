package org.denigma.binding.models

import org.scalax.semweb.rdf.{RDFValue, IRI, Res}

/**
 * ScalaJS model, the most important component is id here
 */
trait Model {

  def id:Res

}

case class Property(iri:IRI,values:List[RDFValue])
case class TransferModel(properties:List[Property])