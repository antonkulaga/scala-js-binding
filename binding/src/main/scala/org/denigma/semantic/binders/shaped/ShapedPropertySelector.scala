package org.denigma.semantic.binders.shaped

import java.util.Date

import org.denigma.semantic.binders.{SelectOption, PropertySelector, SelectBinder, SemanticRenderer}
import org.denigma.semantic.rdf.ModelInside
import org.scalajs.dom
import org.scalajs.dom.HTMLElement
import org.scalax.semweb.rdf
import org.scalax.semweb.rdf._
import org.scalax.semweb.rdf.vocabulary.XSD
import org.scalax.semweb.shex._
import rx.core.Var

import scala.scalajs.js
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


  /**
   * Parses string to get RDF value
   * @param str
   * @return
   */
  override protected def parseRDF(str:String) ={
    val input = unescape(str)
      arc.value match { //TODO: rewrite
      case ValueStem(stem)=>
        if (input.contains(stem.stringValue)) IRI(input) else stem / input
      //case lit:DatatypeLiteral(content,tp)
      case ValueType(tp) if !input.contains("^^")=> tp match {
        case  XSD.DecimalDatatypeIRI =>DoubleLiteral(input.toDouble)
        case XSD.IntDatatypeIRI=> IntLiteral(input.toInt)
        case XSD.Date =>DateLiteral(new Date(input))
        case XSD.DateTime => DateLiteral(new Date(input))
        case XSD.StringDatatypeIRI =>StringLiteral(input)
        case other => AnyLit(input)
      }
      case _ if input.contains("^^")=>
        val i = input.indexOf("^^")
        val label = input.substring(1,i-1)
        val dt = input.substring(i+2,input.length)
        rdf.TypedLiteral(label,IRI(dt))
      case _ if input.contains("_:") =>BlankNode(input)
      case _ if input.contains(":")=>IRI(input)
      case _=> AnyLit(input)
    }
  }



  protected val dateRegex = """([0-9]{4}[-_\.][0-9]{2}[-_\.][0-9]{2})"""
  protected val timeRegex = """([0-9]{2}:[0-9]{2}:[0-9]{2}.[0-9]{3})"""
  protected val floatRegex = """[-+]?[0-9]*\.?[0-9]+([eE][-+]?[0-9]+)?"""
  protected val DateOnly = dateRegex.r
  protected val DateTime = (dateRegex + " " + timeRegex).r


  override def createFilter(input:String):Boolean = arc.value match {
    case ValueType(dtp)=> dtp match {
      case XSD.BooleanDatatypeIRI=> input.toLowerCase match{
        case "true" | "false"=> true
        case _=>false}
      //case XSD.Date=> Date.
      case XSD.DecimalDatatypeIRI | XSD.DecimalDatatypeIRI => Try[Double](input.toDouble).isSuccess
      case XSD.IntDatatypeIRI | XSD.IntegerDatatypeIRI => Try(input.toInt).isSuccess
      case XSD.Date => input match {
        case DateOnly(d)=> true
        case _=>false
      }
      case XSD.DateTime => input match {
        case DateTime(d)=> true
        case _=>false
      }

      case _=> input!=""
    }

    //case ValueStem(st)=>
    case ValueSet(els)=>
      val result = els.exists{   case v=>v.stringValue == input || v.label==input   }
      //dom.console.log(s"$input with VALUESET FOR:"+ els.toString+s" RESULT = $result")
      result

    //case ValueAny(stem)=> !ex.exists{   case v=>v.stringValue == input || v.label==input   }

    case other => true //TODO: figure out other cases

  }

  override protected def selectParams(el: HTMLElement):js.Dynamic = {
    js.Dynamic.literal(
      delimiter = "|",
      persist = false,
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

