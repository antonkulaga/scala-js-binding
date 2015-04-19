package org.denigma.semantic.models

import org.denigma.binding.views.BindableView
import org.denigma.semantic.rdf.ModelInside
import org.scalajs.dom
import org.denigma.semweb.shex.{Model, PropertyModel}
import rx._
import rx.core.Var


/**
 * Trait that contains info about model
 */
abstract class ModelView extends BindableView{


  var createIfNotExists:Boolean = true

  def modelOption = params.get("model").map{
    case null =>
      dom.console.error("model cannot be null")
      Var(ModelInside(PropertyModel.empty))
    case mod:PropertyModel=> Var(ModelInside( mod ))
    case mis:ModelInside=> Var(mis)
    case mv:Var[ModelInside] if mv.now.isInstanceOf[ModelInside]=>mv //TODO: add type tag
    case _ => Var(ModelInside(PropertyModel.empty))
  }


  val model: Var[ModelInside] = this.modelOption.getOrElse(Var(ModelInside(PropertyModel.empty)))

  lazy val dirty = Rx{this.model().isDirty}

  def die() = this.model() = this.model.now.apoptosis
 }
