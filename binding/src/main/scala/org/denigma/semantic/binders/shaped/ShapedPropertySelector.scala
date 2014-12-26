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

  override def createItem(input:String):SelectOption =  arc.value match {
    case ValueStem(stem)=> this.makeOption(this.parseRDF(if(input.contains(stem.stringValue)) input else (stem / input).stringValue))
    case other => this.makeOption(this.parseRDF(input))
  }

  override def createFilter(input:String):Boolean = arc.value match {
    case ValueType(dtp)=> dtp match {
      case XSD.BooleanDatatypeIRI=> input.toLowerCase == "true"
      //case XSD.Date=> Date.
      case XSD.DecimalDatatypeIRI | XSD.DecimalDatatypeIRI => Try[Double](input.toDouble).isSuccess
      case XSD.IntDatatypeIRI | XSD.IntegerDatatypeIRI => Try(input.toInt).isSuccess
      case XSD.Date | XSD.DateTime => Try(new Date(input)).isSuccess
      case _=> true
    }

    //case ValueStem(st)=>
    case ValueSet(els)=>
      val result = els.exists{   case v=>v.stringValue == input || v.label==input   }
      dom.console.log(s"$input with VALUESET FOR:"+ els.toString+s" RESULT = $result")
      result

    //case ValueAny(stem)=> !ex.exists{   case v=>v.stringValue == input || v.label==input   }

    case other => true //TODO: figure out other cases

  }

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
      create = true,
      createItem = this.createHandler,
      createFilter = this.createFilterHandler,
      options = makeOptions(),
      render =  SemanticRenderer.asInstanceOf[js.Any],
      copyClassesToDropdown = false,
      plugins = js.Array(SelectBinder.pluginName)
    )
  }

}

