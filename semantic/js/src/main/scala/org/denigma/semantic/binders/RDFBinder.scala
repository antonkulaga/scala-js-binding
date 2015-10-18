package org.denigma.semantic.binders

import org.denigma.binding.binders.ReactiveBinder
import org.denigma.binding.extensions._
import org.scalajs.dom
import org.scalajs.dom.raw.Element
import org.w3.banana._
import rx.core.Var

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
class RDFBinder[Rdf<:RDF](resolver:Resolver[Rdf]) extends ReactiveBinder
{

  import resolver.ops


  implicit val context = ops.makeUri("http://"+dom.location.hostname) //TODO: deprecate

  def elementPartial(el: Element,ats:Map[String, String]): PartialFunction[(String,String),Unit] = rdfPartial(el,ats)

  protected def vocabPartial: PartialFunction[(String,String), Unit] ={

    case ("vocab",value) if value.contains(":") => resolver.addVocab(value)
    //dom.alert("VOCAB=>"+prefixes.toString())

    case ("prefix",value) if value.contains(":")=> resolver.addPrefix(value)
  }


  protected def rdfPartial(el: Element, ats:Map[String,String]): PartialFunction[(String,String), Unit] =  this.vocabPartial
}
