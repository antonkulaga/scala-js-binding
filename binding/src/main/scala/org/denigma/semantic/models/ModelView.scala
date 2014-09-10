package org.denigma.semantic.models

import org.denigma.binding.views.BindableView
import org.denigma.semantic.rdf.ModelInside
import org.scalax.semweb.shex.PropertyModel
import rx._
import rx.core.Var

/**
 * Created by antonkulaga on 9/10/14.
 */
trait ModelView extends BindableView{

  var createIfNotExists:Boolean = true

  val modelInside =  Var(ModelInside( PropertyModel.empty))

  lazy val dirty = Rx{this.modelInside().isDirty}

  def die() = this.modelInside() = this.modelInside.now.apoptosis
 }
