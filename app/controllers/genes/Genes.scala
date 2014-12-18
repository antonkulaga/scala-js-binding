package controllers.genes

import controllers._
import controllers.endpoints.MainEndpoint
import org.denigma.binding.play.UserAction
import play.api.mvc.{Result, AnyContent, Action, RequestHeader}
import play.twirl.api.Html

import scala.io.Source
import scala.util.{Failure, Try}

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

      val indexed = testGenesTable(str)
      Ok(indexed.toString)
    //new play.core.StaticApplication(new java.io.File("."))
  }
  def readFrom(path:String)(implicit request:RequestHeader): String = {
    val url: String = controllers.routes.Assets.at(path).absoluteURL(secure = false)(request)
    Source.fromURL(url).getLines().reduce(_+"\n"+_)
  }


}
