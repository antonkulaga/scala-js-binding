package org.denigma.semantic.binders

import org.denigma.selectors.{Escaper, InputHolder, SelectRenderer, SelectOption}
import org.scalajs.dom
import org.denigma.semweb.rdf.IRI
import rx.core.Var

import scala.None
import scala.scalajs.js
import scala.scalajs.js.Function1
import scala.scalajs.js.annotation.JSName
import scalatags.Text.all._

case class PrefixedRenderer(prefixes:Var[Map[String,IRI]]) extends SemanticRenderer with PrefixResolver{
  import scalatags.Text.all._


  @JSName("renderItem")
  override protected def renderItem(item:SelectOption): String = if(stringIsIRI(item.id))
  {
    this.prefixForIri(item.id,prefixes.now) match {
      case Some((key,v))=>
        div(`class`:= "name",
        /*
          label(`class` := "ui green horizontal label", this.escape(key),":"),
          label(`class` := "ui green horizontal label", this.escape(item.id.replace(v.stringValue,"")))
          */
          item.id.replace(v.stringValue,asPrefix(key))
        ).render

      case None=>super.renderItem(item)
    }
  } else super.renderItem(item)

/*  override def renderOption(item:SelectOption):String = if(item.id.contains(":") && !item.id.contains("^^")){
    div(
      div(`class` := "label", item.title),
      div(`class` := "ui tiny green item", escape(item.id)
      )
    ).render
  } else div(`class` := "label", item.title).render*/

}


case object SemanticRenderer extends SemanticRenderer


class SemanticRenderer extends SelectRenderer with Escaper{


  @JSName("renderItem")
  protected def renderItem(item:SelectOption): String =
  {
    import scalatags.Text.all._
    div(`class`:= "name", escape(item.title)).render
    //span(`class`:="iri", item.id.toString)
  }

  @JSName("renderOption")
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
