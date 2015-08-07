package org.denigma.semantic.binders

import org.denigma.binding.binders.BasicBinding
import org.denigma.binding.extensions._
import org.denigma.binding.views.BindableView
import org.scalajs.dom
import org.scalajs.dom.raw.HTMLElement
import org.w3.banana._
import rx.core.Var

import scala.collection.immutable.Map

/**
 * View that can do RDFa binding (binding to semantic properties)
 */
class RDFBinder[Rdf<:RDF](
                           view:BindableView,
                           val prefixes:Var[Map[String,Rdf#URI]])
                         (implicit ops:RDFOps[Rdf]) extends BasicBinding
{

  import ops._

  /**
   * Resolvers IRI from property map
   * @param property
   * @return
   */
  def resolve(property:String):Option[Rdf#URI] =  this.resolve(property,prefixes.now)

  def resolve(property:String,prefixes:Map[String,Rdf#URI]):Option[Rdf#URI]  = property.indexOf(":") match {
    case ind if ind<=0 =>
      prefixes.get(":").orElse(prefixes.get("")).map(p=>p / property)

    case ind=>
      val key = property.substring(0,ind)
      prefixes.get(key).map(p=>p / property).orElse(Some(ops.makeUri(property)))
  }

  protected def prefixed(str:String) = if(str.last==':') str else str+":"

  implicit val context = ops.makeUri("http://"+dom.location.hostname) //TODO: deprecate

  def nearestRDFBinders(): List[RDFBinder[Rdf]] = view.nearestParentOf{
      case p:BindableView if p.binders.exists(b=>b.isInstanceOf[RDFBinder[Rdf]])=>
        p.binders.collect{case b:RDFBinder[Rdf]=>b}
    }.getOrElse(List.empty)


  /**
   * Adds new prefixes to Var
   * @param el
   */
  protected def updatePrefixes(el:HTMLElement) = {
    val rps: List[RDFBinder[Rdf]] = this.nearestRDFBinders()
    prefixes.set(rps.foldLeft(Map.empty[String,Rdf#URI])((acc,el)=>acc++el.prefixes.now)++this.prefixes.now)
  }

  override def bindAttributes(el: HTMLElement, ats: Map[String, String]): Unit = {

    this.updatePrefixes(el)

    ats.foreach { case (key, value) =>
      this.rdfPartial(el, key, value,ats).orElse(otherPartial)(key)
    }

  }

  /**
   * Returns partial function that extracts vocabulary data (like prefixes) from html
   * @param value
   * @return
   */
  protected def vocabPartial(value: String): PartialFunction[String, Unit] ={

    case "vocab" if value.contains(":") => this.prefixes.set(prefixes.now + (":"-> ops.makeUri(value)))
    //dom.alert("VOCAB=>"+prefixes.toString())

    case "prefix" if value.contains(":")=> this.prefixes.set(prefixes.now + (value.substring(0,value.indexOf(":"))-> ops.makeUri(value)))
  }


  /**
   * Binds vocabs and prefixes
   * @param el html element to bind to
   * @param key Key
   * @param value Value
   * @param ats attributes
   * @return
   */
  protected def rdfPartial(el: HTMLElement,
                           key: String,
                           value: String,
                           ats:Map[String,String]): PartialFunction[String, Unit] =  this.vocabPartial(value)

  override def id: String = view.id


  /*



    def resolve(property:String):Option[Rdf#URI] =  this.resolve(property,prefixes.now)

    def prefixedIRIString(iri:Rdf#URI): String = prefixes.now.find{ case (key,value)=>  ops.getString(iri).contains(value)}.map
    {
      //TODO: test if it works and includes ":"
      case (key,value)=>
        ops.fromUri(iri).replace(value,)
        iri.stringValue.replace(value.stringValue,key)
    }.getOrElse(iri.stringValue)





    protected def forBinding(str:String) = str.contai, ats:Map[String,String]): PartialFunction[String, Unit] =  this.vocabPartial(value)


    /**
     * If it has "value" property"
     * @param el
     * @return
     */
    protected def elementHasValue(el:HTMLElement) =  el.tagName.toLowerCase match {
      case "input" | "textarea" | "option" =>true
      case _ =>false
    }


    def setTitle(el:HTMLElement,tlt:String) = {
      if(this.elementHasValue(el)) {
        el.dyn.value = tlt

      } else {
        el.textContent = tlt
      }
    }*/
}
