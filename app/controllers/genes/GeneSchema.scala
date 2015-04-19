package controllers.genes

import controllers.endpoints.ItemsMock
import org.denigma.semweb.rdf._
import org.denigma.semweb.rdf.vocabulary.{RDF, RDFS, WI, XSD}
import org.denigma.semweb.shex._

trait GeneSchema extends ItemsMock{

  val entrez = IRI("http://ncbi.nlm.nih.gov/gene/")

  lazy val gero = IRI("http://gero.longevityalliance.org/")


  object Codes {
    abstract class Evidence {
      lazy val code =  gero / this.getClass.getName.split('$').last
    }

    abstract class Experimental extends Evidence
    abstract class Literature extends Evidence
    abstract  class NoEvidence extends Evidence
    case object IMP extends Experimental
    case object IGI extends Experimental
    case object IPI extends Experimental
    case object IDA extends Experimental
    case object IEP extends Experimental
    case object TAS extends Literature
    case object NAS extends Literature
    case object IC extends NoEvidence
    case object ND extends NoEvidence

  }

  lazy val evidenceForm = new ShapeBuilder(gero / "Evidence_Shape")

  lazy val (entrezId:IRI,db:IRI,objId:IRI,symbol:IRI, qualifier:IRI, go:IRI,
  ref:IRI, code:IRI , from:IRI, aspect:IRI,  dbObjectName:IRI, synonym:IRI, tp:IRI,
  taxon:IRI, date:IRI, assigned:IRI, extension:IRI, product:IRI,
  clazz:IRI, tissue:IRI, influence:IRI
    ) =
    (gero / "has_ENTREZID",gero / "from_db" ,  gero / "is_DB_object" ,  gero / "has_symbol", gero / "has_qualifier", gero / "has_GO",
      gero / "has_ref", gero / "has_code" , gero / "is_from",
      gero / "has_aspect", gero / "has_name", gero / "has_synonym", gero / "of_type",
      gero / "from_model_organism", gero / "added_on", gero / "is_assigned_by",
      gero / "has_extension", gero / "is_product",
      RDFS.SUBCLASSOF, gero / "from_tissue", gero / "has_influence"
      )

  import org.denigma.semweb.sesame._
  val cls = Ontology.classes.map(cl=>cl:IRI).toSeq

  val prefixes: Seq[(String, String)] =       Seq("rdf"->RDF.namespace,
  "rdfs"->RDFS.namespace,
  "xsd"->XSD.namespace,
  "owl"->vocabulary.OWL.namespace,
  "dc"->vocabulary.DCElements.namespace,
  "pl"->(WI.PLATFORM.namespace+"/"),
  "shex"-> org.denigma.semweb.shex.rs.stringValue,
  "def"->org.denigma.semweb.shex.se.stringValue,
  "pmid"->"http://ncbi.nlm.nih.gov/pubmed/",
  "gero"->gero.stringValue,
  "entrez"->entrez.stringValue
  )

  import Codes._
  evidenceForm has entrezId isCalled "ENTREZID" startsWith entrez occurs ExactlyOne hasPriority 1
  evidenceForm has clazz isCalled "Class" startsWith gero occurs ExactlyOne hasPriority 2// from(cls:_*)
  evidenceForm has db isCalled "DB" occurs ExactlyOne hasPriority 3
  evidenceForm has objId isCalled "DB Object ID" occurs ExactlyOne hasPriority 4
  evidenceForm has symbol isCalled "DB Object Symbol" occurs ExactlyOne  hasPriority 5
  //form has qualifier isCalled "Qualifier" occurs Star hasPriority 5
  //form has go isCalled "GO ID" occurs ExactlyOne
  evidenceForm has ref isCalled "Publication"  occurs Plus hasPriority 6
  evidenceForm has code isCalled "Evidence Code" occurs ExactlyOne hasPriority 7  from(IMP code, IGI code, IPI code, IDA code, IEP code, TAS code,  NAS code,  IC code, ND code)
  //form has from isCalled "With (or) From" occurs Star
  //form has aspect isCalled "Aspect" occurs ExactlyOne hasPriority 8
  evidenceForm has dbObjectName startsWith gero isCalled "DB object Name" occurs Opt hasPriority 8
 // evidenceForm has synonym isCalled "synonym" occurs Star hasPriority 9
  //form has tp isCalled "DB Object Type" occurs ExactlyOne hasPriority 11
  evidenceForm has tp isCalled "Gene product type" startsWith gero occurs ExactlyOne hasPriority 11 from(
    gero / "protein_complex", gero / "protein", gero / "transcript",
      gero / "ncRNA", gero / "rRNA", gero / "tRNA", gero / "snRNA", gero / "snoRNA", gero / "microRNA", gero / "gene_product")
  evidenceForm has taxon isCalled "Organism" startsWith gero occurs Cardinality(1,2) hasPriority 12
  evidenceForm has date isCalled "Date" of XSD.Date occurs ExactlyOne hasPriority 13
  //evidenceForm has assigned isCalled "Assigned by" occurs ExactlyOne hasPriority 14
  //form has extension isCalled "Annotation Ext/ension" occurs Star
  //evidenceForm has product isCalled "Gene Product Form ID" occurs Opt hasPriority 15
  evidenceForm has tissue isCalled "Tissue" occurs Plus hasPriority 17
  evidenceForm has influence isCalled "Influence" occurs ExactlyOne hasPriority 18 from(gero / "Pro-Longevity", gero / "Anti-Longevity")

  lazy val evidenceShape = evidenceForm.result


  val interventions = gero / "intervention"
  val effect = gero / "intervention_effect"

  lazy val interventionsForm = new ShapeBuilder(gero / "Interventions_Shape")
  interventionsForm has symbol isCalled "Gene Symbol" occurs ExactlyOne hasPriority 1
  interventionsForm has db


}
