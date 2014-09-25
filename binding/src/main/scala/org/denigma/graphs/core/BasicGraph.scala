package org.denigma.graphs.core


trait BasicGraph{

  type Node <:DataHolder
  type Edge <:EdgeLike[Node]

  var nodes:List[Node]
  var edges:List[Edge]

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