package controllers.genes

import framian.csv.{Csv, LabeledCsv}
import org.denigma.endpoints.{UserRequestHeader, UserAction}
import org.openrdf.model.Statement
import org.scalax.semweb.shex._
import play.api.Play
import play.api.mvc.{RequestHeader, Controller}
import play.twirl.api.Html
import play.api.Play.current
import scala.collection.{mutable, GenSet}
import scala.io.Source
import framian._
import framian.csv.Csv
import spire.implicits._
import org.scalax.semweb.rdf.vocabulary.{WI, RDFS, RDF, XSD}
import org.scalax.semweb.rdf._

import scala.util.Try
import controllers._
import framian.{Cols, Frame}
import org.scalax.semweb.rdf.{Trip, BasicTriplet, IRI, Quad}
import org.scalax.semweb.sesame._
import org.scalax.semweb.shex.{PropertyModel, Shape}
import org.w3.banana.io.{RDFReader, RDFWriter, Turtle}
import org.w3.banana.sesame.Sesame
import org.w3.banana.{FOAFPrefix, RDF, RDFOps}
import play.api.mvc.{RequestHeader, Result}
import play.twirl.api.Html

import scala.io.Source
import scala.util.{Failure, Success, Try}
import scalaz.{Comonad, Monad}

/**
 * Literature controller
 */
object Genes extends PJaxPlatformWith("literature") with LoadGenAge{

  override def page(implicit request:UserRequestHeader,html:Option[Html] = None,into:String = "main"): Result = {
    Ok(views.html.index(request,html,into,false))
  }


  def reports() = UserAction{implicit request=>
    this.pj(views.html.genes.datagrid()(request))
  }


  def testGenes() = UserAction {
    implicit request =>
      //val fileName = "resources/data_from_geneage.csv"
      val fileName = "resources/annotations.tsv"
      val str = readFrom(fileName)(request)

      val indexed: List[PropertyModel] = testGenesTable(str)
      val trips = (for{
        mod <- indexed
        id = mod.id
        (prop,values) <- mod.properties
        obj <-values
      } yield Trip(id,prop,obj)).toSet

      Ok(this.writeTurtle(trips,this.prefixes))

    //    Ok(trips.toString)
  }

  /**
   * Writes turtle to string
   * @param trips
   * @param prefs
   * @tparam T
   * @return
   */
  def writeTurtle[T<:BasicTriplet](trips:Set[T],prefs:Seq[(String,String)]): String =  TurtleMaster.write(trips,prefs:_*).get



  def ontology() = UserAction {
    implicit request=>
      import org.scalax.semweb.sesame._
      import scala.collection.JavaConversions._

      val statements: List[Statement] = Ontology.allFacts.flatMap(g=>g.graph)

      val sts = statements.map(st=>Trip(st.getSubject,st.getPredicate,st.getObject))
      val facts:Set[Trip] = Set(sts:_*)

        //.map(st:Statement=>Trip(st.getSubject,st.getPredicate,st.getObject))
      Ok(this.writeTurtle[Trip](facts,this.prefixes))

  }


  def testSchemaWriting() = UserAction {
    val quads: Set[Quad] = this.evidenceShape.asQuads(IRI("http://denigma.org/resource/"))
    val str = this.writeTurtle(quads,this.prefixes)//TurtleMaster.simpleWrite(quads)
    Ok(str)
  }


  def readFrom(path:String)(implicit request:RequestHeader): String = {
    val url: String = controllers.routes.Assets.at(path).absoluteURL(secure = false)(request)
    Source.fromURL(url).getLines().reduce(_+"\n"+_)
  }



}
