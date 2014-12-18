package controllers.genes

import java.util.Date

import controllers.endpoints.{Items, ItemsMock}
import org.scalax.semweb.rdf._
import org.scalax.semweb.rdf.vocabulary.{RDF, XSD}
import org.scalax.semweb.shex
import org.scalax.semweb.shex._

trait GeneSchema extends ItemsMock{

  val entrez = IRI("http://www.ncbi.nlm.nih.gov/gene/?term=")
  lazy val evi = de / "evidence"
  lazy val codes = evi / "codes"


  object Codes {
    abstract class Evidence {
      lazy val code =  evi / this.getClass.getName.split('$').last
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

  lazy val form = new ShapeBuilder(de / "Evidence_shape")

  lazy val (entrezId:IRI,db:IRI,objId:IRI,symbol:IRI, qualifier:IRI, go:IRI,
  ref:IRI, code:IRI , from:IRI, aspect:IRI,  dbObjectName:IRI, synonym:IRI, tp:IRI,
  taxon:IRI, date:IRI, assigned:IRI, extension:IRI, product:IRI,
  clazz:IRI, tissue:IRI, influence:IRI
    ) =
    (de /"ENTREZID",evi / "db" ,  evi / "object" ,  evi / "symbol", evi / "qualifier", evi / "GO",
      evi / "ref", evi / "code" , evi / "from",
      evi / "aspect", evi / "name", evi / "synonym", evi / "type",
      evi / "model_organism", evi / "date", evi / "assigned_by",
      evi / "extension", evi / "product",
      de / "class", de / "tissue", de / "influence"
      )


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
  form has dbObjectName startsWith de isCalled "DB object Name" occurs Opt hasPriority 9
  //form has synonym isCalled "synonim" occurs Star hasPriority 10
  //form has tp isCalled "DB Object Type" occurs ExactlyOne hasPriority 11
  form has tp isCalled "Gene product type" startsWith evi occurs ExactlyOne hasPriority 11 from(
    evi / "protein_complex", evi / "protein", evi / "transcript",
      evi / "ncRNA", evi / "rRNA", evi / "tRNA", evi / "snRNA", evi / "snoRNA", evi / "gene_product")
  form has taxon isCalled "Organism" startsWith de occurs Cardinality(1,2) hasPriority 12
  form has date isCalled "Date" occurs ExactlyOne hasPriority 13
  //form has assigned isCalled "Assigned by" occurs ExactlyOne hasPriority 14
  //form has extension isCalled "Annotation Ext/ension" occurs Star
  form has product isCalled "Gene Product Form ID" occurs Opt hasPriority 15
  form has clazz isCalled "Class" startsWith de occurs ExactlyOne hasPriority 16
  // form has tissue isCalled "Tissue" occurs Plus hasPriority 2
  form has influence isCalled "Influence" occurs ExactlyOne hasPriority 17 from(evi / "pro", evi / "anti")


  lazy val formShape = form.result
}
