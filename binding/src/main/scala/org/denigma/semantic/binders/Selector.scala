package org.denigma.semantic.binders

import org.denigma.binding.commons.ILogged
import org.denigma.binding.extensions._
import org.denigma.binding.views.BindableView
import org.scalajs.dom.HTMLElement
import org.scalajs.selectize._
import org.scalax.semweb.rdf
import org.scalax.semweb.rdf._
import org.scalax.semweb.rdf.vocabulary.XSD
import rx.core.Var

import scala.collection.immutable.Map
import scala.scalajs.js
import scala.scalajs.js.{Function1, ThisFunction1}
import scala.scalajs.js.annotation.JSExportAll
import scalatags.Text.all._


object SelectOption
{
  implicit def convert(op:SelectOption): js.Dynamic =op.asInstanceOf[js.Dynamic]
}


@JSExportAll
case class SelectOption(id:String,title:String)


@JSExportAll
trait SelectRenderer{
  val item: js.Function1[SelectOption,String]
  val option: js.Function1[SelectOption,String]
  //val option_create: js.Function1[InputHolder,  String]

}

class InputHolder extends js.Object{
  val input:String = ???
}



trait Escaper {
  protected val replacements = ("\"", "&#34;") ::("<", "&lt;") ::(">", "&gt;") ::("'", "&#39;") :: Nil

  def escape(str: String) = replacements.foldLeft(str) {case (acc, (from,to))=>acc.replace(from,to) }

  def unescape(str: String) = replacements.foldLeft(str) {case (acc, (from,to))=>acc.replace(to,from) }

}

/**
 * Selector that has some features of converting RDF values
 */
trait SemanticSelector extends Selector with Escaper {


  /**
   * Extracts literal's label
   * @param str
   * @return
   */
  protected def labelOf(str:String) = str.indexOf("^^") match {
    case -1 =>str
    case i=>str.substring(1,i-1)
  }



  /**
   * Parses string to get RDF value
   * @param str
   * @return
   */
  protected def parseRDF(str:String) =
    this.unescape(str) match {
      case st if str.contains("^^") && str.contains(XSD.StringDatatypeIRI.stringValue)=>
        StringLiteral(labelOf(st))
      case st if str.contains("^^")=>
        val dt = st.substring(st.indexOf("^^")+2,st.length)
        rdf.TypedLiteral(labelOf(st),IRI(dt))
      //AnyLit(str)
      case st if str.contains("_:") => BlankNode(st)
      case st if str.contains(":") => IRI(st)
      case st => StringLiteral(st)
    }



  protected def makeOption(v:RDFValue): SelectOption =  this.makeOption(v.stringValue,v.label)
  protected def makeOption(vid:String,title:String): SelectOption = SelectOption(this.escape(vid),title) // js.Dynamic.literal( id = vid, title = title)


  protected def makeOptions(properties:Map[IRI,Set[RDFValue]],iri:IRI) =
    properties.get(iri) match {
      case Some(iris)=>
        val o = iris.map(i=> makeOption(i)).toList
        js.Array( o:_* )
      case None=> js.Array()
    }


  def updateOptions(opts:Seq[RDFValue]): Unit = {

    val ss = selectizeFrom(el)
    for {
      r <- ss.options.keys.filter{case k => !ss.items.contains(k)}
    } ss.options.remove(r)
    opts.foreach { o =>
      ss.addOption(makeOption(o))
    }
    ss.refreshItems()
  }
}


trait Selector extends ILogged {


  def el: HTMLElement

  val sel: js.Dynamic

  protected def makeOption(vid:String,title:String): SelectOption

  protected def itemAddHandler(value:String, item:js.Dynamic): Unit
  protected def itemRemoveHandler(value:String): Unit


  protected def selectParams(el: HTMLElement):js.Dynamic

  protected def selectizeFrom(el:HTMLElement): Selectize = {
    val s = el.dyn.selectize
    s.asInstanceOf[Selectize]
  }

}

trait GeneralSelectBinder
{
  type Element
  type View<:BindableView
  type Selector

  val view:View
  val model:Var[Element]
  var selectors = Map.empty[HTMLElement,Selector]
}