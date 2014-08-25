package org.denigma.binding.picklers

import org.scalax.semweb.picklers.SemanticRegistry

/**
 * Registers picklers
 */
object rp extends BindingPicklers with GraphPicklers{
  this.register()

  override def register() = {
    super.register()
    this.registerModels()
    this.registerExploration()
    this.registerGraph()

  }

}

class BindingPicklers extends SemanticRegistry with ModelPicklers
{
  override def register() = {
    super.register()
    this.registerModels()
    this.registerExploration()

  }
}