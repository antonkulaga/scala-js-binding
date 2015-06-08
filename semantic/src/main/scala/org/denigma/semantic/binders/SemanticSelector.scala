package org.denigma.semantic.binders

import org.denigma.selectize._
import org.denigma.selectize.Selector
import org.denigma.semweb.rdf
import org.denigma.semweb.rdf._
import org.denigma.semweb.rdf.vocabulary.XSD
import org.querki.jquery._
import org.scalajs.dom
import org.scalajs.dom.raw.HTMLElement
import rx.core.Var
import org.denigma.binding.extensions._
import scala.collection.immutable.Map
import scala.scalajs.js
/**
 * Selector that has some features of converting RDF values
 */
trait SemanticSelector extends Selector with PrefixResolver{


  def prefixes:Var[Map[String,IRI]]


  def initSelectize(el:HTMLElement,params:(HTMLElement)=>SelectizeConfigBuilder): Selectize = {
    import org.denigma.binding.extensions._
    val opts:SelectizeConfig = params(el)
    val $el = $(el)
    //$el.dyn.selectize(opts)
    $el.selectize(opts)
    el.dyn.selectize.asInstanceOf[Selectize]
  }

  def initSelectize(el:HTMLElement): Selectize = this.initSelectize(el,this.selectParams)

  protected def selectParams(el: HTMLElement):SelectizeConfigBuilder

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
  protected def parseRDF(str:String): RDFValue =
    str match {
      case st if str.contains("^^") && str.contains(XSD.StringDatatypeIRI.stringValue)=>
        StringLiteral(labelOf(st))
      case st if str.contains("^^")=>
        val dt = st.substring(st.indexOf("^^")+2,st.length)
        rdf.TypedLiteral(labelOf(st),IRI(dt))
      //AnyLit(str)
      case st if str.contains("_:") => BlankNode(st)
      case st if str.contains(":") => this.resolve(st,prefixes.now).getOrElse(IRI(str))

      case st => StringLiteral(st)
    }



  protected def makeOption(v:RDFValue): SelectOption =  v match {
    //case iri:IRI=>this.makeOption(iri.stringValue,this.iri2Prefix(iri,this.prefixes.now))
    case other=> this.makeOption(other.stringValue,other.label)
  }

  protected def makeOption(vid:String,title:String): SelectOption = {
    SelectOption(vid,title)
  } // js.Dynamic.literal( id = vid, title = title)


  protected def makeOptions(properties:Map[IRI,Set[RDFValue]],iri:IRI): js.Array[SelectOption] =
    properties.get(iri) match {
      case Some(iris)=>
        val o = iris.map(i=> makeOption(i)).toList
        js.Array( o:_* )
      case None=> js.Array()
    }

  def updateOptions(el:HTMLElement)(opts:Seq[RDFValue]):Unit =
    selectizeOption(el) match {
      case Some(ss)=> this.updateOptions(ss)(opts)
      case None=>dom.console.error("selectize has not been initialized")
    }

  def updateOptions(ss:Selectize)(opts:Seq[RDFValue]): Unit = {
    for {
      r <- ss.options.keys.filter{case k => !ss.items.contains(k)}
    } ss.options.remove(r)
    opts.foreach { o =>
      ss.addOption(makeOption(o))
    }
    ss.refreshItems()
  }
}
