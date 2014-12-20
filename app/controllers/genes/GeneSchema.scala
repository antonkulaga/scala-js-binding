package controllers.genes

import java.util.Date

import controllers.endpoints.{Items, ItemsMock}
import org.scalax.semweb.rdf._
import org.scalax.semweb.rdf.vocabulary.{RDFS, RDF, XSD}
import org.scalax.semweb.shex
import org.scalax.semweb.shex._

trait GeneSchema extends ItemsMock{

  val entrez = IRI("http://www.ncbi.nlm.nih.gov/gene/?term=")

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

  lazy val form = new ShapeBuilder(gero / "Evidence_Shape")

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

  import org.scalax.semweb.sesame._
  val cls = Ontology.classes.map(cl=>cl:IRI).toSeq

  import Codes._
  form has entrezId isCalled "ENTREZID" startsWith entrez occurs ExactlyOne hasPriority 1
  form has db isCalled "DB" occurs ExactlyOne hasPriority 2
  form has objId isCalled "DB Object ID" occurs ExactlyOne hasPriority 3
  form has symbol isCalled "DB Object Symbol" occurs ExactlyOne  hasPriority 4
  //form has qualifier isCalled "Qualifier" occurs Star hasPriority 5
  //form has go isCalled "GO ID" occurs ExactlyOne
  form has ref isCalled "Publication" startsWith pmid occurs Plus hasPriority 5
  form has code isCalled "Evidence Code" occurs ExactlyOne hasPriority 6  from(IMP code, IGI code, IPI code, IDA code, IEP code, TAS code,  NAS code,  IC code, ND code)
  //form has from isCalled "With (or) From" occurs Star
  //form has aspect isCalled "Aspect" occurs ExactlyOne hasPriority 8
  form has dbObjectName startsWith gero isCalled "DB object Name" occurs Opt hasPriority 9
  //form has synonym isCalled "synonim" occurs Star hasPriority 10
  //form has tp isCalled "DB Object Type" occurs ExactlyOne hasPriority 11
  form has tp isCalled "Gene product type" startsWith gero occurs ExactlyOne hasPriority 11 from(
    gero / "protein_complex", gero / "protein", gero / "transcript",
      gero / "ncRNA", gero / "rRNA", gero / "tRNA", gero / "snRNA", gero / "snoRNA", gero / "microRNA", gero / "gene_product")
  form has taxon isCalled "Organism" startsWith gero occurs Cardinality(1,2) hasPriority 12
  form has date isCalled "Date" of XSD.Date occurs ExactlyOne hasPriority 13
  //form has assigned isCalled "Assigned by" occurs ExactlyOne hasPriority 14
  //form has extension isCalled "Annotation Ext/ension" occurs Star
  form has product isCalled "Gene Product Form ID" occurs Opt hasPriority 15
  form has clazz isCalled "Class" startsWith gero occurs ExactlyOne hasPriority 16 from(cls:_*)
  // form has tissue isCalled "Tissue" occurs Plus hasPriority 2
  form has influence isCalled "Influence" occurs ExactlyOne hasPriority 17 from(gero / "pro", gero / "anti")

  lazy val formShape = form.result

}
