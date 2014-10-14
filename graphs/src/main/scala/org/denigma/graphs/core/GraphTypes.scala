package org.denigma.graphs.core

/**
 * Created by antonkulaga on 10/14/14.
 */
trait GraphTypes {
  type NodeId
  type EdgeId
  type NodeData
  type EdgeData
  type ViewOfNode
  type ViewOfEdge
  type Node<:VisualNode[NodeData,ViewOfNode]
  type Edge<:VisualEdge[Node,EdgeData,ViewOfEdge]

}
