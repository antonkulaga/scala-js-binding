package org.denigma.semantic.models

import org.denigma.binding.views.BindableView
import org.denigma.semantic.rdf.ModelInside
import org.scalax.semweb.shex.{Model, PropertyModel}
import rx._
import rx.core.Var


/**
 * Trait that contains info about model
 */
trait ModelView extends BindableView{

  var createIfNotExists:Boolean = true

  private lazy val initial = params.get("model") match {
    case Some(mod:PropertyModel)=> ModelInside( mod )
    case Some(mis:ModelInside)=> mis
    case None => ModelInside(PropertyModel.empty)
  }

  val model: Var[ModelInside] = params.get("model") match {
    case Some(mv:Var[ModelInside])=>mv
    case Some(mod:PropertyModel)=> Var(ModelInside( mod ))
    case Some(mis:ModelInside)=> Var(mis)
    case None => Var(ModelInside(PropertyModel.empty))
  }

  lazy val dirty = Rx{this.model().isDirty}

  def die() = this.model() = this.model.now.apoptosis
 }
