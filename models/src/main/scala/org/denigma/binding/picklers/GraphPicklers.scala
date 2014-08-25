package org.denigma.binding.picklers

import org.denigma.binding.messages._
import org.denigma.binding.models.{MenuItem, Menu}
import org.scalajs.spickling.PicklerRegistry
import org.scalajs.spickling.PicklerRegistry._

trait GraphPicklers
{
  self:PicklerRegistry=>


  def registerGraph() = {

    register[GraphMessages.NodeExplore]

  }

}
