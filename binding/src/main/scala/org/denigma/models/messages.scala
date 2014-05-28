package org.denigma.models

import org.scalax.semweb.rdf.Res


trait Model {

  def id:Res

}

trait Operation{
  type ValueType

  val value:ValueType
}

trait Create extends Operation
trait Read extends Operation
trait Update extends Operation
trait Delete extends Operation

