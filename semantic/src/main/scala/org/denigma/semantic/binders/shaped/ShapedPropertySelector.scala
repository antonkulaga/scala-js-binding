package org.denigma.semantic.binders.shaped

import org.denigma.semantic.binders._
import org.denigma.semantic.rdf.ModelInside
import org.scalajs.dom
import org.scalajs.dom.raw.HTMLElement
import org.scalax.semweb.rdf
import org.scalax.semweb.rdf._
import org.scalax.semweb.rdf.vocabulary.XSD
import org.scalax.semweb.shex._
import rx.core.Var

import scala.scalajs.js
import scala.util.Try

/*class ShapedRenderer extends SemanticRenderer {


  val optionCreate:js.Function2[Any,js.Function1[String,String],SelectOption]

  val
}*/
object ShapedPropertySelector {

}

/**
 * Selector for properties validated by shapes
 * @param el html element of the view from which there will be selection
 * @param key iri of the property
 * @param model property model with props and vals
 * @param arc
 * @param typeHandler handler that react on typing
 */
class ShapedPropertySelector(el:HTMLElement,key:IRI,  model:Var[ModelInside], val arc:ArcRule,  prefs:Var[Map[String,IRI]] = Var(RDFBinder.defaultPrefixes))
                            (typeHandler:(String)=>Unit) extends PropertySelector(el,key,model,prefs)(typeHandler)
{


  /**
   * Parses string to get RDF value
   * @param str
   * @return
   */
  override protected def parseRDF(str:String): RDFValue ={
    val input = str//unescape(str)
    arc.value match { //TODO: rewrite
      case ValueStem(stem)=>
        if (input.contains(stem.stringValue)) IRI(input) else stem / input
      //case lit:DatatypeLiteral(content,tp)
      case ValueType(tp) if !input.contains("^^")=> tp match {
        case XSD.DecimalDatatypeIRI =>DoubleLiteral(input.toDouble)
        case XSD.IntDatatypeIRI=> IntLiteral(input.toInt)
        case XSD.Date =>
          DateTimeFormats.parseDate(input).map(d=>DateLiteral(d)).getOrElse{
            dom.console.error(s"cannot parse date = "+input)
            StringLiteral(input)
          }
        case XSD.DateTime =>
          DateTimeFormats.parseDate(input).map(d=>DateLiteral(d)).getOrElse{
            dom.console.error(s"cannot parse datetime = "+input)
            dom.console.error(s"time parsing has not been implemented yet = "+input)
            StringLiteral(input)
          }
        case XSD.StringDatatypeIRI =>StringLiteral(input)
        case other => StringLiteral(input)//AnyLit(input)
      }
      case _ if input.contains("^^")=>
        val dt = input.substring(input.indexOf("^^")+2,input.length)
        rdf.TypedLiteral(labelOf(input),IRI(dt))
      case _ if input.contains("_:") =>BlankNode(input)
      case _ if input.contains(":")=>IRI(input)
      case _=>  StringLiteral(input)
        //AnyLit(input)
    }
  }



  protected val dateRegex = """([0-9]{4}[-_\.][0-9]{2}[-_\.][0-9]{2})"""
  protected val timeRegex = """([0-9]{2}:[0-9]{2}:[0-9]{2}.[0-9]{3})"""
  protected val floatRegex = """[-+]?[0-9]*\.?[0-9]+([eE][-+]?[0-9]+)?"""
  protected val DateOnly = dateRegex.r
  protected val DateTime = (dateRegex + " " + timeRegex).r

  protected def cardinalityFilter(input:String):Boolean =    arc.occurs match
     {
        case card:Cardinality =>
          //TODO:rewrite
          true
      }


  /**
   * Filters that filters value added by users and checks wether they are allowed
   * @param input
   * @return
   */
  protected def valueFilter(input:String):Boolean =    arc.value match {
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


  /**
   * Filters if some value is allowed
   * @param input
   * @return
   */
  override def createFilter(input:String):Boolean =this.cardinalityFilter(input) && this.valueFilter(input)

  /**
   * Settings for selectize selectors
   * @param el
   * @return
   */
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
      render = PrefixedRenderer(prefixes).asInstanceOf[js.Any],
      copyClassesToDropdown = false,
      plugins = js.Array(SelectBinder.pluginName)
    )
  }

}

