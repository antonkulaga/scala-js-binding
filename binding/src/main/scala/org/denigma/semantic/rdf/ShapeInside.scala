package org.denigma.semantic.rdf

import org.scalax.semweb.shex.{ArcRule, Shape}

object ShapeInside {

  def apply(initial:Shape):ShapeInside = ShapeInside(initial,initial)

}

case class ShapeInside(initial:Shape,current:Shape,wantsToDie:Boolean = false) extends ChangeSlot
{
  override type Value = Shape

  def updateArc(arc:ArcRule) = {
    current.arcRules()
  }
}

