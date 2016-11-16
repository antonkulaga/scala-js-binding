package org.denigma.controls.charts
import org.denigma.binding.extensions._
import rx.Var
//import rx.Ctx.Owner.voodoo
import rx.Ctx.Owner.Unsafe.Unsafe


trait FlexibleLinesPlot extends FlexiblePlot {
  override val scaleX: Var[LinearScale]
  override val scaleY: Var[LinearScale]
  def onMaxChange(value: Point): Unit = value match {
    case Point(x, y) =>
      if(flexible.now) {
        val (sX, sY) = (scaleX.now, scaleY.now)
        val sh = shrinkMult.now
        val st = stretchMult.now
        val updScaleX = sX.stretched(x, stretchMult = st, shrinkMult = sh)
        scaleX.set(updScaleX)
        val updScaleY = sY.stretched(y,  stretchMult = st, shrinkMult = sh)
        scaleY.set(updScaleY)
        //println("TICK LEN = "+scaleX.now.ticks.length)
      }
  }
}



trait FlexiblePlot extends LinesPlot {
  val flexible = Var(true)
  val shrinkMult = Var(1.05)
  val stretchMult = Var(1.05)
  val empty: rx.Rx[Boolean] = items.map(_.isEmpty)
  //def max(series: Series)(fun: Point=>Double): Point = series.points.maxBy(fun)

  val max = items.map{ its =>
    its.foldLeft(Point(0.0, 0.0)){ case (acc, (_, s)) => s.maxOpt match {
      case Some(v) => acc.copy(Math.max(acc.x, v.x), Math.max(acc.y, v.y))
      case _ => acc
    }}
  }

  def onMaxChange(value: Point): Unit

  override def subscribeUpdates() = {
    super.subscribeUpdates()
    max.foreach{onMaxChange}
  }
}
