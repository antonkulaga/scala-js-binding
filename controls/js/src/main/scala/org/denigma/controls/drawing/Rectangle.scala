package org.denigma.controls.drawing

import org.scalajs.dom.raw.SVGRect


case class Rectangle(x: Double, y: Double, width: Double, height: Double) {
  require( width >= 0.0 && height >= 0.0, s"rectangle width and height should always be positive")

  def left = x
  def top = y
  lazy val right = x + width
  lazy val bottom = y + height

  def merge(rect: Rectangle) = {
    Rectangle.fromCorners(
      Math.min(rect.left, left),
      Math.min(rect.top, top),
      Math.max(rect.right, right),
      Math.max(rect.bottom, bottom))
  }

  lazy val ox = x + width / 2.0
  lazy val oy = x + height / 2.0

  def centerHor(rect: Rectangle) = copy(x = rect.ox - 0.5 * width)
  def centerVert(rect: Rectangle) = copy(y = rect.oy - 0.5 * height)

  def withPadding(horPadding: Double, verPadding: Double): Rectangle = copy(height = height + verPadding * 2, width = width + horPadding *2)

}


object Rectangle {

  implicit def fromSVG(rect: SVGRect): Rectangle = Rectangle(rect.x, rect.y, rect.width, rect.height)

  def fromCorners(left: Double, top: Double, right: Double, bottom: Double): Rectangle = {
    require( right >= left && bottom >= top, s"rectangle width and height should always be positive")

    Rectangle(left, top, right - left, bottom - top)
  }

  def apply(width: Double, height: Double): Rectangle = Rectangle(0.0, 0.0, width, height)


}