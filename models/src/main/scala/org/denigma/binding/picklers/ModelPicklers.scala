package org.denigma.binding.picklers

import org.scalajs.spickling.PicklerRegistry
import org.scalajs.spickling.PicklerRegistry._
import org.denigma.binding.models.ModelMessages

trait ModelPicklers extends RDFPicklers{
  self:PicklerRegistry=>

  def registerMessages() = {

    register[ModelMessages.Create]
    register[ModelMessages.Read]
    register[ModelMessages.Update]
    register[ModelMessages.Delete]

  }
}
