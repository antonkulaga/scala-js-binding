package org.denigma.semantic.binders.binded

import org.w3.banana.{PointedGraph, RDF}

/**
 * Created by antonkulaga on 9/6/15.
 */
case class Typed[Rdf<:RDF](graph:PointedGraph[Rdf],typed:String)
{
  def isEmpty = typed==""
}
