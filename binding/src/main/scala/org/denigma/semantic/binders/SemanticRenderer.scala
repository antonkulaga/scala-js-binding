package org.denigma.semantic.binders

import org.scalajs.dom

import scala.scalajs.js
import scala.scalajs.js.Function1
import scalatags.Text.all._


case object SemanticRenderer extends SemanticRenderer
class SemanticRenderer extends SelectRenderer with Escaper{


  protected def renderItem(item:SelectOption): String =
  {
    import scalatags.Text.all._
    div(`class`:= "name", item.title).render
    //span(`class`:="iri", item.id.toString)
  }

  def renderOption(item:SelectOption):String = if(item.id.contains(":") && !item.id.contains("^^")){
    div(
      div(`class` := "label", item.title),
      div(`class` := "ui tiny blue item", escape(item.id)
      )
    ).render
  } else div(`class` := "label", item.title).render

  def optionCreate(input: InputHolder):String= label(`class` := "ui label","add: "+input.input).render

  override val item: js.Function1[SelectOption, String] = renderItem _
  override val option: js.Function1[SelectOption, String] = renderOption _
 // override val option_create:js.Function1[InputHolder,  String] = optionCreate _
}
