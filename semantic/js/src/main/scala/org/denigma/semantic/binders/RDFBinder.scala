package org.denigma.semantic.binders

import org.denigma.binding.binders.BasicBinder
import org.denigma.binding.extensions._
import org.denigma.binding.views.BindableView
import org.scalajs.dom
import org.scalajs.dom.raw.HTMLElement
import org.w3.banana._
import rx.Rx
import rx.core.{Obs, Var}

import scala.collection.immutable.Map


//TODO:rewrite
class PrefixResolver[Rdf<:RDF](
                                prefixes:Var[Map[String,Rdf#URI]])
                              (implicit val ops:RDFOps[Rdf])
  extends Resolver[Rdf]
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

  def prefixed(str:String) = if(str.last==':') str else str+":"

  def addPrefix(value:String) = this.prefixes.set(prefixes.now + (value.substring(0,value.indexOf(":"))-> ops.makeUri(value)))

  def addVocab(value:String) = this.prefixes.set(prefixes.now + (":"-> ops.makeUri(value)))

}


trait Resolver[Rdf<:RDF] {
  val ops:RDFOps[Rdf]
  def resolve(property:String):Option[Rdf#URI]
  def addPrefix(value:String):Unit
  def addVocab(value:String):Unit
}

/**
 * View that can do RDFa binding (binding to semantic properties)
 */
class RDFBinder[Rdf<:RDF](view:BindableView, resolver:Resolver[Rdf]) extends BasicBinder
{

  import resolver.ops
  import ops._


  implicit val context = ops.makeUri("http://"+dom.location.hostname) //TODO: deprecate

  def bindAttributes(el: HTMLElement, ats: Map[String, String]): Unit = {
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

    case "vocab" if value.contains(":") => resolver.addVocab(value)
    //dom.alert("VOCAB=>"+prefixes.toString())

    case "prefix" if value.contains(":")=> resolver.addPrefix(value)
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
}
