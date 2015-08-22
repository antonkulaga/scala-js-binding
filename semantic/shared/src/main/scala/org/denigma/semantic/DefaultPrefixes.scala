package org.denigma.semantic

import org.w3.banana._

/**
 * Denigma prefix
 */
object Denigma {
  def apply[Rdf <: RDF](implicit ops: RDFOps[Rdf]) = new Denigma(ops)
}

class Denigma[Rdf<:RDF](ops:RDFOps[Rdf]) extends PrefixBuilder[Rdf]("de","http://denigma.org/")(ops){
  lazy val resource = apply("resource")
}


object WebPlatform{
  def apply[Rdf <: RDF](implicit ops: RDFOps[Rdf]) = new WebPlatform(ops)

  def random[Rdf <: RDF](implicit ops: RDFOps[Rdf]): Rdf#URI = this.apply[Rdf](ops)(s"random/${Math.random().toString}")
}

class WebPlatform[Rdf<:RDF](ops:RDFOps[Rdf]) extends PrefixBuilder[Rdf]("wi","http://webintelligence.eu/platform/")(ops)
{
  def random = apply(Math.random().toString)
}


class DefaultPrefixes[Rdf<:RDF](implicit ops:RDFOps[Rdf]) {
   def withPrefix(prefix:Prefix[Rdf]): (String, Rdf#URI) = prefix.prefixName -> ops.makeUri(prefix.prefixIri)

   lazy val prefixes = Map(
     withPrefix(ops.rdf),
     withPrefix(ops.xsd),
     withPrefix(OWLPrefix(ops)),
     withPrefix(DCPrefix(ops)),
     withPrefix(LDPPrefix(ops))
   )
 }
