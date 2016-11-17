package org.denigma.controls.drawing

import org.denigma.controls.drawing.SvgBundle.all._
import rx._
import org.denigma.controls.drawing.SvgBundle.all.attrs._
import org.scalajs.dom.raw.{SVGElement, SVGLocatable}
import org.scalajs.dom.svg.SVG

import scalatags.JsDom
import scalatags.JsDom.TypedTag

/**
  * Draws SVG labels
  */
trait BoxPainter {

  def s: SVG

  type Locatable = SVGElement with SVGLocatable

  lazy val labelStrokeColor:  Var[String] = Var("black")
  lazy val labelStrokeWidth: Var[Double] = Var(1)

  def getTextBox(str: String, fSize: Double): Rectangle = {
    val svg = text(str, fontSize := fSize)
    getBox(svg.render)
  }

  def getBox(e: Locatable): Rectangle = {
    s.appendChild(e)
    val box = e.getBBox()
    s.removeChild(e)
    box
  }

  def drawSVG(w: Double, h: Double, definitions: List[JsDom.Modifier], children: List[JsDom.Modifier]): TypedTag[SVG] = {
    val decs = defs(definitions:_*)
    val params = List(
      height := h,
      width := w,
      decs
    ) ++ children
    svg.apply(params: _*)
  }


  def drawLabel(str: String, rectangle: Rectangle, textBox: Rectangle,
                fSize: Double, grad: String, rX: Double = 15, rY: Double = 15): TypedTag[SVG] = {
    val st = labelStrokeWidth.now
    val r = rect(
      `class` := "caption",
      stroke :=  labelStrokeColor.now,
      fill := s"url(#${grad})",
      strokeWidth := st,
      x := st * 2,
      y := st * 2,
      height := rectangle.height,
      width := rectangle.width,
      rx := rX, ry := rY
    )
    val startX = (rectangle.width - textBox.width) / 2
    val startY = (rectangle.height - textBox.height) - st + textBox.height
    val txt = text(str, fontSize := fSize, x := startX + st, y := startY)
    import scalatags.JsDom.implicits._

    svg(
      height := rectangle.height + st *2,
      width := rectangle.width +st *2,
      x := rectangle.x,
      y := rectangle.y,
      //onclick := { ev: MouseEvent=> println("hello")},
      r, txt
    )
  }

}
