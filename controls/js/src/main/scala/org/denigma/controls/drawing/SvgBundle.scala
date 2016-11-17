package org.denigma.controls.drawing

/**
  * Gathers SVG tags and properties together to avoid naming conflicts with HTML tags
  */
object SvgBundle {
  import scalatags.JsDom._
  import scalatags._

  object all extends Cap
    with jsdom.SvgTags
    with DataConverters
    with Aggregate
    with LowPriorityImplicits {
    object attrs extends Cap with SvgAttrs
  }
}