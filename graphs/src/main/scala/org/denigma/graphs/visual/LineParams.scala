package org.denigma.graphs.visual

import org.denigma.graphs.core.DataHolder
import org.denigma.graphs.layouts.LayoutInfo
import org.denigma.graphs.misc.Randomizable
import org.denigma.graphs.tools.HtmlSprite
import org.scalajs.dom.HTMLElement
import rx._

import scala.util.Random

object Defs extends Randomizable
{

  val colors = List("green","red","blue",/*"orange",*/"purple","teal")
  val colorMap= Map("green"->0xA1CF64,"red"->0xD95C5C,"blue" -> 0x6ECFF5,/*"orange" ->0xF05940,*/"purple"->0x564F8A,"teal"->0x00B5AD)

  def randColorName = colors(Random.nextInt(colors.size))

  def colorName = randColorName

  def color = colorMap(colorName)

  def headLength = 30

  def headWidth= 15


}

case class LineParams(lineColor:Double = Defs.color,headLength:Double = Defs.headLength, headWidth:Double = Defs.headWidth)
