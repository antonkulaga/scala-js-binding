package org.denigma.binding.binders.extractors

import org.denigma.binding.macroses._

/**
 * Used in General binder
 */
trait Extractor
{


  def extractEverything[T
  :MapRxMap
  :TagRxMap
  :StringRxMap :BooleanRxMap
  :EventMap :MouseEventMap
  :TextEventMap :KeyEventMap
  :UIEventMap :WheelEventMap :FocusEventMap](value:T)
}
