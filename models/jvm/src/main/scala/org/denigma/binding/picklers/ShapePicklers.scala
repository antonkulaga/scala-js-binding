package org.denigma.binding.picklers

import org.denigma.binding.messages._
import org.scalajs.spickling.PicklerRegistry
import org.scalajs.spickling.PicklerRegistry._

trait ShapePicklers{
  self:PicklerRegistry=>

  def registerShapeMessages() = {
    register[ShapeMessages.GetShapes]
    register[ShapeMessages.SuggestProperty]


  }
}