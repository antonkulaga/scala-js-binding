package controllers

import controllers.genes.GeneSchema
import org.denigma.endpoints.UserAction
import org.scalax.semweb.rdf.{IRI, StringLiteral}
import org.scalax.semweb.shex.PropertyModel
import play.api.mvc.Controller
import prickle._


object Test extends Controller with GeneSchema{


  
  
  
  def prickle() = UserAction{ implicit request=>
    import org.scalax.semweb.composites.SemanticComposites._
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
