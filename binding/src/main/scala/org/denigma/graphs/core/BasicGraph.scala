package org.denigma.graphs.core

import scala.scalajs.js


trait BasicGraph{

  type Node <:DataHolder
  type Edge <:EdgeLike[Node]


  val nodes:js.Array[Node]  //optimized for speed
  val edges:js.Array[Edge]  //optimized for speed


}

trait EdgeLike[Node] extends DataHolder
{

  val from:Node
  val to:Node

}

trait DataHolder
{
  type Data
  def data:Data
  type View
  val view:View


}