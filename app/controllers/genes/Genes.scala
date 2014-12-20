package controllers.genes

import controllers._
import org.denigma.binding.play.UserAction
import org.scalax.semweb.rdf.{Trip, BasicTriplet, IRI, Quad}
import org.scalax.semweb.sesame._
import org.scalax.semweb.shex.{PropertyModel, Shape}
import org.w3.banana.io.{RDFReader, RDFWriter, Syntax, Turtle}
import org.w3.banana.sesame.Sesame
import org.w3.banana.{RDF, RDFOps}
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
    this.pj(views.html.genes.evidence(request))
  }


  def testGenes() = UserAction {
    implicit request =>
      val fileName = "resources/data_from_geneage.csv"
      val str = readFrom(fileName)(request)

      val indexed: List[PropertyModel] = testGenesTable(str)
      val trips = (for{
        mod <- indexed
        id = mod.id
        (prop,values) <- mod.properties
        obj <-values
      } yield Trip(id,prop,obj)).toSet


      Ok(TurtleMaster.simpleWrite(trips))
    //    Ok(trips.toString)
  }

  def testSchemaWriting() = UserAction {
    val quads: Set[Quad] = this.formShape.asQuads(IRI("http://denigma.org/resource/"))
    val str = TurtleMaster.simpleWrite(quads)
    Ok(str)
  }


  def readFrom(path:String)(implicit request:RequestHeader): String = {
    val url: String = controllers.routes.Assets.at(path).absoluteURL(secure = false)(request)
    Source.fromURL(url).getLines().reduce(_+"\n"+_)
  }



}
