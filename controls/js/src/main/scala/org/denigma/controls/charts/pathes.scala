package org.denigma.controls.charts

import org.denigma.binding.views.BindableView
import org.scalajs.dom.raw.{SVGPathElement, HTMLElement}
import org.scalajs.dom.svg.Path

class PathView(val elem:SVGPathElement) extends BindableView {

}

class ChartPath(points:List[PointValue]) {
  val path = new Path {}
}


/*
<svg height="210" width="400">
  <path d="M150 0 L75 200 L225 200 Z" />
  Sorry, your browser does not support inline SVG.
</svg>
 */