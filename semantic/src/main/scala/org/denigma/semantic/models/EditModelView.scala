package org.denigma.semantic.models

import org.denigma.binding.binders.extractors.EventBinding
import org.denigma.binding.binders.{GeneralBinder, NavigationBinding}
import org.denigma.binding.views.BindableView
import org.denigma.semantic.binders.editable.EditModelBinder
import rx.core.Var

import scala.collection.immutable.Map
import scala.scalajs.js
import scala.scalajs.js.Dynamic.{global => g}
import scalatags.Text.{attrs => a, styles => s}

object EditModelView {

  implicit def defaultBinders(view:EditModelView) =   new EditModelBinder(view,view.model,view.editMode)::new GeneralBinder(view)::new NavigationBinding(view)::Nil
}

trait EditModelView extends ModelView with BindableView
{

  val mode: String = params.get("mode").fold("htmlmixed")(_.toString())

  val codeParams = js.Dynamic.literal(
    mode = this.mode.asInstanceOf[js.Any],
    lineNumbers = true
  )

  //override val model: Var[ModelInside] = this.params.get("model").map(m=>m.asInstanceOf[Var[ModelInside]]).getOrElse(Var(ModelInside.empty))

  val editMode = Var(false)

  val toggleClick = Var(EventBinding.createMouseEvent())

  val saveClick = Var(EventBinding.createMouseEvent())



}

