package org.denigma.binding.picklers

import org.scalax.semweb.picklers.SemanticRegistry

/**
 * Registers picklers
 */
object rp extends BindingPicklers with GraphPicklers{
  this.register()

  override def register() = {
    this.registerCommon()
    this.registerMessages()
    this.registerRdf()
    this.registerModels()
    this.registerExploration()
    this.registerShapeMessages()
    this.registerGraph()

  }

}

class BindingPicklers extends SemanticRegistry with ModelPicklers with ShapePicklers
{
  override def register() = {
    this.registerCommon()
    this.registerMessages()
    this.registerRdf()
    this.registerModels()
    this.registerExploration()
    this.registerShapeMessages()

  }
}