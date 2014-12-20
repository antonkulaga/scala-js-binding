package org.denigma.semantic.binders.shaped

import org.denigma.semantic.binders.{SelectOption, PropertySelector, SelectBinder, SemanticRenderer}
import org.denigma.semantic.rdf.ModelInside
import org.scalajs.dom
import org.scalajs.dom.HTMLElement
import org.scalax.semweb.rdf.IRI
import org.scalax.semweb.rdf.vocabulary.XSD
import org.scalax.semweb.shex._
import rx.core.Var

import scala.scalajs.js
import scala.scalajs.js.Date
import scala.scalajs.js.annotation.JSName
import scala.util.Try
import scalatags.Text.all._

/*class ShapedRenderer extends SemanticRenderer {


  val optionCreate:js.Function2[Any,js.Function1[String,String],SelectOption]

  val
}*/
object ShapedPropertySelector {

}


class ShapedPropertySelector(el:HTMLElement,key:IRI,  model:Var[ModelInside], val arc:ArcRule)
                            (typeHandler:(String)=>Unit) extends PropertySelector(el,key,model)(typeHandler)
{

  def createFilter(self:js.Dynamic,input:String) = arc.value match {
    case ValueType(dt)=> dt match {
      case XSD.BooleanDatatypeIRI=> input.toLowerCase == "true"
      //case XSD.Date=> Date.
      case XSD.DecimalDatatypeIRI | XSD.DecimalDatatypeIRI => Try[Double](input.toDouble).isSuccess
      case XSD.IntDatatypeIRI | XSD.IntegerDatatypeIRI => Try(input.toInt).isSuccess
      case XSD.Date | XSD.DateTime => Try(new Date(input)).isSuccess
      case _=> true
    }

    //case ValueStem(st)=>
    case ValueSet(els)=> els.exists{   case v=>v.stringValue == input || v.label==input   }

    //case ValueAny(stem)=> !ex.exists{   case v=>v.stringValue == input || v.label==input   }
  }

  @JSName("createItem")
  def createItem(input:String):SelectOption = {
    dom.console.log("CREATE WORKS!")
    val opt =arc.value match {
      case  ValueStem(stem: IRI) =>
        val value = if(input.startsWith(stem.stringValue)) IRI(input) else stem / input
        this.makeOption(value)
      case other=>
        this.makeOption(this.parseRDF(input))
    }
    dom.alert("CREATE WORKS!")
    opt
  }

  protected val createHandler:js.Function1[String,SelectOption] = createItem _

  override protected def selectParams(el: HTMLElement):js.Dynamic = {
    js.Dynamic.literal(
      delimiter = "|",
      persist = true,
      valueField = "id",
      labelField = "title",
      searchField = "title",
      onType = typeHandler  ,
      onItemAdd = itemAddHandler _,
      onItemRemove =  itemRemoveHandler _,
      create = createHandler,
      //create = true,
      //createFilter = createFilterHandler,
      options = makeOptions(),
      render =  SemanticRenderer.asInstanceOf[js.Any],
      copyClassesToDropdown = false,
      plugins = js.Array(SelectBinder.pluginName)
    )
  }



}
