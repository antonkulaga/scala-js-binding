package controllers.genes

import java.io.{StringWriter, ByteArrayOutputStream}

import org.scalax.semweb.rdf.BasicTriplet
import org.scalax.semweb.sesame._
import org.w3.banana._
import org.w3.banana.io.{RDFReader, RDFWriter, Syntax, Turtle}
import org.w3.banana.sesame.Sesame
import spire.implicits._

import scala.collection.parallel.immutable
import scala.util.{Failure, Success, Try}


abstract class Store[Rdf <: RDF, M[+_] , Sin, Sout](implicit
                                                    val ops: RDFOps[Rdf],
                                                    val reader: RDFReader[Rdf, M, Sin],
                                                    val readerSyn: Syntax[Sin],
                                                    val writer: RDFWriter[Rdf, M, Sout],
                                                    val writerSyn: Syntax[Sout]
                                                     )
{
  def write[T <:BasicTriplet](trips: Set[T],namespaces:(String,String)*): Try[String]


  def simpleWrite[T <:BasicTriplet](trips: Set[T]) = this.write(trips) match {
    case Success(str)=> str
    case Failure(th)=> th.toString
  }
}
object TurtleMaster extends Store[Sesame,Try,Turtle,Turtle] with TurtleStore
trait TurtleStore{
  self:Store[Sesame,Try,Turtle,Turtle]=>

/*  def complexWrite[T<:BasicTriplet](trips:Set[T],namespaces:(String,String)*): String = {
    import org.openrdf.rio.turtle._

    /*val stream = new ByteArrayOutputStream()
    val wr = new TurtleWriter(stream)
    wr.startRDF()
    for((pr,n)<-namespaces) wr.handleNamespace(pr,n)
    val sorted = trips.toSeq.sortWith( (a,b)=> {
      val (as,bs) = (a.sub.stringValue,b.sub.stringValue)
      val (aps,bps) = (a.pred.stringValue,b.pred.stringValue)
      if(as==bs) aps>bps  else  as > bs
    }).foreach(st=>wr.handleStatement(this.ops.makeTriple(st.sub,st.pred,st.obj)))
    wr.endRDF()
    stream.toString*/

  }*/



  override def write[T <:BasicTriplet](trips: Set[T],namespaces:(String,String)*): Try[String] = {
    import ops._
    val triplets = for{t <- trips}  yield Triple(t.sub,  t.pred,  t.obj)
    val g = Graph(triplets)
    val str = new StringWriter()
    for(n<-namespaces)g.setNamespace(n._1,n._2)
    //writer.write(g,str,"http://longevityalliance.org/resource/")
    writer.asString(g,"http://longevityalliance.org/resource/")
  }

}
abstract class OntologyClasses[Rdf <: RDF, M[+_] , Sin, Sout](implicit
                                                              o: RDFOps[Rdf],
                                                              r: RDFReader[Rdf, M, Sin],
                                                              rSyn: Syntax[Sin],
                                                              wr: RDFWriter[Rdf, M, Sout],
                                                              wrSyn: Syntax[Sout]
                                                               ) extends Store[Rdf,M,Sin,Sout](

) {

  object gero extends PrefixBuilder("gero", "http://gero.longevityalliance.org/")(ops)

  object go extends PrefixBuilder("gero", "http://gero.longevityalliance.org/")(ops)

  val owl = OWLPrefix[Rdf]
  val rdf = RDFPrefix[Rdf]
  val rdfs = RDFSPrefix[Rdf]

  val agingGene = gero("Aging_Associated_Gene")
  val ageRelatedGene = gero("Age_Related_Gene")

  val agingBiomarker = gero("Aging_Biomarker")

  val longevityGene = gero("Longevity_Gene")

  val agingRegulator = gero("Longevity_Regulator")
  val circadianGene = gero("Circadian_Gene")
  val cytokine = gero("Cytokine")
  val growthFactor = gero("Grows_Factor")

  val mediator = gero("Longevity_Mediator")
  val microRNA = gero("microRNA")
  val decetulases = gero("Deacetualases")
  val transcriptionFactor = gero("Transcription_Factor")

  val longevityEffector = gero("Longevity_Effector")
  val apoptosisGene = gero("Apoptosis_Gene")
  val cellSenescenceGene = gero("Cellular_Senescence_Gene")
  val antiOxidantEnzyme = gero("Anti-oxidant_Enzyme")
  val houseKeepingGene = gero("House_Keeping_Gene")
  val unfoldedProteinResponse = gero("Unfolded_Protein_Response_Gene")
  val autophagyGene = gero("Autophagy_Gene")
  val longProtease = gero("Protease")
  val detoxification = gero("Detoxification")
  val detoxificationPhaseI = gero("Detoxification_phase_I")
  val detoxificationPhaseII = gero("Detoxification_phase_II")
  val proteasome = gero("DNA_Repair_Gene")
  val innateImmunityGene = gero("Innate_Immunity_Gene")
  val heatShockProtein = gero("Heat_Shocke_Protein")

  val longevityInfluence = gero("has_influence")
  val (anti, pro) = (gero("Pro-Longevity"), gero("Anti-Longevity"))

  val longevityRestriction = ops.makeBNode()
  val geneRestriction = ops.makeBNode()


  val tissue = gero("in_tissue")

  val classes = Set(
    ageRelatedGene, agingBiomarker, longevityGene, agingRegulator, circadianGene, cytokine, growthFactor,
    microRNA, decetulases, transcriptionFactor, longevityEffector, apoptosisGene, cellSenescenceGene,
    antiOxidantEnzyme, houseKeepingGene, unfoldedProteinResponse, autophagyGene, longProtease,
    detoxificationPhaseI, detoxificationPhaseII, proteasome, innateImmunityGene, heatShockProtein
  )

  //обратите внимание что мы заменяем LON protease на Protease, Cell senescence на Cellulra senescence


  val gene = gero("Gene")

  val foaf = FOAFPrefix[Rdf]

  lazy val allFacts: List[PointedGraph[Rdf]] = {
    import org.w3.banana.diesel._

    val g: List[PointedGraph[Rdf]] = List (
        agingGene.a(gene)   -- rdfs.subClassOf ->- gene  ,

        ageRelatedGene.a(gene)  -- rdfs.subClassOf ->- agingGene,

        agingBiomarker -- rdfs.subClassOf ->- ageRelatedGene,

      longevityInfluence.a(rdf.Property)
        -- rdfs.domain ->- longevityGene ,

      longevityRestriction.a(owl.Restriction)
        -- owl.onProperty ->- longevityInfluence
        -- owl.someValuesFrom ->-(anti, pro)  ,

      longevityGene.a(gene) -- rdfs.subClassOf ->-(agingGene, longevityRestriction),

      agingRegulator -- rdfs.subClassOf ->- longevityGene,
      circadianGene.a(gene) -- rdfs.subClassOf ->- agingRegulator,
      cytokine -- rdfs.subClassOf ->- agingRegulator,
      growthFactor -- rdfs.subClassOf ->- agingRegulator,

      mediator -- rdfs.subClassOf ->- longevityGene,

      microRNA -- rdfs.subClassOf ->- mediator,
      decetulases -- rdfs.subClassOf ->- mediator,
      transcriptionFactor -- rdfs.subClassOf ->- mediator,
      longevityEffector -- rdfs.subClassOf ->- longevityGene,

      apoptosisGene -- rdfs.subClassOf ->- longevityEffector,
      cellSenescenceGene -- rdfs.subClassOf ->- longevityEffector,
      antiOxidantEnzyme -- rdfs.subClassOf ->- longevityEffector,
      houseKeepingGene -- rdfs.subClassOf ->- longevityEffector,
      unfoldedProteinResponse -- rdfs.subClassOf ->- longevityEffector,
      autophagyGene -- rdfs.subClassOf ->- longevityEffector,
      longProtease -- rdfs.subClassOf ->- longevityEffector,
      detoxification -- rdfs.subClassOf ->- longevityEffector,
      detoxificationPhaseI -- rdfs.subClassOf ->- longevityEffector,
      detoxificationPhaseII -- rdfs.subClassOf ->- longevityEffector,
      proteasome -- rdfs.subClassOf ->- longevityEffector,
      innateImmunityGene -- rdfs.subClassOf ->- longevityEffector,
      heatShockProtein -- rdfs.subClassOf ->- longevityEffector

      )

    g
  }
}
object Ontology extends OntologyClasses[Sesame,Try,Turtle,Turtle] with TurtleStore {


}