package org.denigma.semantic.schema

import org.denigma.binding.views.{BindingEvent, BasicView}
import org.denigma.semantic.shapes.ShapeView

case class AddShapeCommand(origin:EditShapeView,latest:BasicView) extends  ShapeUpdateCommand(origin,origin)
{
  override def withCurrent(cur: BasicView):this.type = this.copy(latest = cur).asInstanceOf[this.type]
}

case class RemoveShapeCommand(origin:EditShapeView,latest:BasicView) extends  ShapeUpdateCommand(origin,latest)
{
  override def withCurrent(cur: BasicView):this.type = this.copy(latest = cur).asInstanceOf[this.type]
}

abstract class  ShapeUpdateCommand(origin:EditShapeView,latest:BasicView) extends BindingEvent{
  type Origin = ShapeView

  override val bubble: Boolean = false
}
