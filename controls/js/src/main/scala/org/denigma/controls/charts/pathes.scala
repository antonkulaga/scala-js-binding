package org.denigma.controls.charts

import org.denigma.binding.views.BindableView
import org.scalajs.dom._
import rx._
//import rx.Ctx.Owner.voodoo
import rx.Ctx.Owner.Unsafe.Unsafe



import scala.collection.immutable._

/**
  * View to show
  * @param elem element to bind to
  * @param series series of points
  * @param transform function to transfor coordinates of the point
  * @param threshold
  * @param closed if the shape is closed
  */
class SeriesView(elem: Element, val series: Var[Series], transform: Rx[Point => Point], threshold:Point = Point(1,1), closed: Boolean = false) extends PathView(
  elem,
  Rx{series().points.map(transform())},
  series.map(s=>s.style),
  threshold,
  closed
  )
{
  val title = series.map(s=>s.title)
}

class PathView(val elem: Element, val points: Rx[List[Point]], style: Rx[LineStyles], threshold: Point = Point(1,1), closed: Boolean = true) extends BindableView {


  val strokeColor: rx.Rx[String] = style.map(s=>s.strokeColor)
  val strokeWidth: rx.Rx[Double] = style.map(s=>s.strokeWidth)
  val strokeOpacity: rx.Rx[Double] = style.map(s=>s.opacity)

  val path: rx.Rx[String] = points.map(points2Path)

  def points2Path(items: List[Point]): String = items match{
    case Point(sx, sy) :: tail =>
      val (_, str) = tail.foldLeft(Point(sx,sy) -> s"M$sx $sy") {
        case ( (v, acc), p) =>
          val diff = Math.abs(v.x-p.x) > threshold.x || Math.abs(v.y-p.y) > threshold.y
          if (diff)
            p -> (acc + s" L${p.x} ${p.y}")
          else (v, acc)
      }
      if (closed) str +" Z" else str
    case Nil => if (closed) " Z" else ""
  }

  /*
   M = moveto
   L = lineto
   H = horizontal lineto
   V = vertical lineto
   C = curveto
   S = smooth curveto
   Q = quadratic Bézier curve
   z = smooth quadratic Bézier curveto
   A = elliptical Arc
   Z = closepath*/
}
