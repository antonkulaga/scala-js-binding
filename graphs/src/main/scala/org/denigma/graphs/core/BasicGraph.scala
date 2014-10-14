package org.denigma.graphs.core

trait EdgeLike[Node,Data,View] extends DataHolder[Data,View]
{

  val from:Node
  val to:Node

}

trait DataHolder[Data,View]
{
  def data:Data
  type View

}

trait Subject extends Observable with Observer

trait Observable {
  var observers: List[Subject]
  def send(any:Any) = observers.foreach(o=>o.receive(any))
}

trait Observer{

  def receive:PartialFunction[Any,Unit]
  
}