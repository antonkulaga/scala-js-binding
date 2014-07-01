package org.denigma.binding.picklers

import org.denigma.binding.messages._
import org.denigma.binding.models.{MenuItem, Menu}
import org.scalajs.spickling.PicklerRegistry
import org.scalajs.spickling.PicklerRegistry._

trait ModelPicklers {
  self:PicklerRegistry=>

  def registerModels() = {

    register[ModelMessages.Create]
    register[ModelMessages.Read]
    register[ModelMessages.Update]
    register[ModelMessages.Delete]
    register[ModelMessages.SelectQuery]

    register[Menu]
    register[MenuItem]


  }
}
