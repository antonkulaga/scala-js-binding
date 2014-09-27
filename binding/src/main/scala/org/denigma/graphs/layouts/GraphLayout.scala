package org.denigma.graphs.layouts

import scala.scalajs.js





trait GraphLayout[Node, Edge]
{
  def width:Double
  def height:Double
  def active:Boolean
  def start(nodes:js.Array[Node],edges:js.Array[Edge]):Unit
  def tick(nodes:js.Array[Node],edges:js.Array[Edge]):Unit
  def stop():Unit
  def pause():Unit

}