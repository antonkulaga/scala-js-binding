package controllers.genes

import org.scalax.semweb.rdf.BasicTriplet
import org.w3.banana.io.{RDFReader, RDFWriter, Syntax, Turtle}
import org.w3.banana.sesame.Sesame
import org.w3.banana._
import framian.csv.{Csv, LabeledCsv}
import org.denigma.binding.play.UserAction
import org.scalax.semweb.shex._
import play.api.Play
import play.api.mvc.{RequestHeader, Controller}
import play.twirl.api.Html
import play.api.Play.current
import scala.io.Source
import framian._
import framian.csv.Csv
import spire.implicits._
import org.scalax.semweb.rdf.vocabulary.{RDFS, RDF, XSD}
import org.scalax.semweb.rdf._
import org.scalax.semweb.sesame._

import scala.util.{Failure, Success, Try}


abstract class Store[Rdf <: RDF, M[+_] , Sin, Sout](implicit
                                                    val ops: RDFOps[Rdf],
                                                    val reader: RDFReader[Rdf, M, Sin],
                                                    val readerSyn: Syntax[Sin],
                                                    val writer: RDFWriter[Rdf, M, Sout],
                                                    val writerSyn: Syntax[Sout]
                                                     )
{
  def write[T <:BasicTriplet](trips: Set[T]): Try[String]

  def simpleWrite[T <:BasicTriplet](trips: Set[T]) = this.write(trips) match {
    case Success(str)=> str
    case Failure(th)=> th.toString
  }
}
object TurtleMaster extends TurtleStore
class TurtleStore extends Store[Sesame,Try,Turtle,Turtle]
{



  override def write[T <:BasicTriplet](trips: Set[T]): Try[String] = {
    import ops._

    val triplets = for{t <- trips}  yield Triple(t.sub,  t.pred,  t.obj)
    val g = Graph(triplets)
    writer.asString(g,"http://webintelligence.eu/")
  }

}
abstract class OntologyClasses[Rdf <: RDF, M[+_] , Sin, Sout](implicit
                                                              o: RDFOps[Rdf],
                                                              r: RDFReader[Rdf, M, Sin],
                                                              rSyn: Syntax[Sin],
                                                              wr: RDFWriter[Rdf, M, Sout],
                                                              wrSyn: Syntax[Sout]
                                                               ) extends Store[Rdf,M,Sin,Sout](

)
{
  import ops._
  import org.w3.banana.diesel._

  object gero extends PrefixBuilder("gero", "http://denigma.org/resource/")(ops)
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
  val cellSenescenceGene = gero("Cell_Senescence_Gene")
  val antiOxidantEnzyme = gero("Anti-oxidant_Enzyme")
  val houseKeepingGene = gero("House_Keeping_Gene")
  val unfoldedProteinResponse = gero("Unfolded_Protein_Response_Gene")
  val autophagyGene = gero("Autophagy_Gene")
  val longProtease = gero("LON_Protease")
  val detoxification = gero("Detoxification")
  val detoxificationPhaseI = gero("Detoxification_phase_I")
  val detoxificationPhaseII = gero("Detoxification_phase_II")
  val proteasome = gero("DNA_Repair_Gene")
  val innateImmunityGene = gero("Innate_Immunity_Gene")
  val heatShockProtein = gero("Heat_Shocke_Protein")

  val classes = Set(
    ageRelatedGene,agingBiomarker,longevityGene,agingRegulator,circadianGene,cytokine,growthFactor,
    microRNA,decetulases,transcriptionFactor,longevityEffector,apoptosisGene,cellSenescenceGene,
  antiOxidantEnzyme, houseKeepingGene,unfoldedProteinResponse,autophagyGene,longProtease,
    detoxificationPhaseI , detoxificationPhaseII, proteasome,innateImmunityGene, heatShockProtein
  )




  val foaf = FOAFPrefix[Rdf]



}

object Ontology extends OntologyClasses[Sesame,Try,Turtle,Turtle]{


  override def write[T <:BasicTriplet](trips: Set[T]): Try[String] = {
    import ops._

    val triplets = for{t <- trips}  yield Triple(t.sub,  t.pred,  t.obj)
    val g = Graph(triplets)
    writer.asString(g,"http://webintelligence.eu/")
  }

}