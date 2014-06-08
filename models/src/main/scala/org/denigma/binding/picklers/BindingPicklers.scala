package org.denigma.binding.picklers

import org.scalajs.spickling._

trait BindingPicklers extends ModelPicklers{
self:PicklerRegistry=>



  this.registerCommon()
  this.registerRdf()
  this.registerMessages()



  def registerPicklers(): Unit = ()




}
