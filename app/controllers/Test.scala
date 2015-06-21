package controllers

import org.denigma.endpoints.UserAction
import org.denigma.schemas.genes.GeneSchema
import org.denigma.semweb.rdf.{IRI, StringLiteral}
import org.denigma.semweb.shex.PropertyModel
import play.api.mvc.Controller
import prickle._


object Test extends Controller with GeneSchema{


  
  
  
  def prickle() = UserAction{ implicit request=>
    request.session
    import org.denigma.semweb.composites.SemanticComposites._
    Ok(Pickle.intoString(this.evidenceShape))
    
  }
  
  
  /**
   * Test html
   * @return
   */
  def html() = UserAction {
    implicit request =>

      val v = views.html.tests.test(request,"main")
      Ok(v)
  }

  def macroses() = UserAction {
    implicit request =>
      val mac = views.html.tests.macroses(request)
      Ok(views.html.tests.suite(mac)(request))
  }
}
