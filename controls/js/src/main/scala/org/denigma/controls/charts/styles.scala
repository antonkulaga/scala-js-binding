package org.denigma.controls.charts

object ChartStyles {
  lazy val default: ChartStyles  = ChartStyles( LineStyles.default,LineStyles.default,LineStyles.default )

}

object LineStyles {
  lazy val default = LineStyles("green",4,"none" , 1.0)
}

case class ChartStyles( linesStyles:LineStyles, scaleX:LineStyles, scaleY:LineStyles)

case class LineStyles(strokeColor:String,strokeWidth:Double, fill:String, opacity:Double = 1.0)