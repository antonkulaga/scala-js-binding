package controllers

import java.io.{InputStreamReader, File}

import controllers.genes.{GeneSchema, LoadGenAge, routes}
import framian.csv.{Csv, LabeledCsv}
import org.denigma.endpoints.UserAction
import org.scalajs.spickling.playjson._
import play.api.Play
import play.api.mvc.{RequestHeader, Controller}
import org.scalax.semweb.shex.{Shape, PropertyModel}
import org.scalax.semweb.rdf.{StringLiteral, IRI}
import org.denigma.binding.picklers.rp
import play.twirl.api.Html
import play.api.Play.current
import prickle._
import scala.collection.mutable
import scala.io.Source
import play.api.libs.json
import json._

import scala.util.{Failure, Success, Try}


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


  def tuple2()= UserAction{
    implicit request=>
      //val value = Seq("one"->"1","two"->"2","three"->"3")
      val value =  ("10","20")
      val pickle = rp.pickle(value)
      Ok(pickle).as("application/json")

  }

  def map()= UserAction{
    implicit request=>      //val value = Seq("one"->"1","two"->"2","three"->"3")
      val mp =   Map("one"->"first value","two"->"second value","three"->"third value")

      val pickle = rp.pickle(mp)

      Ok(pickle).as("application/json")

  }

  def model()= UserAction{
    implicit request=>

      val value = PropertyModel(IRI("http://test"),properties = Map(IRI("http://hello.world.com")->Set(StringLiteral("HELLO WORLD")),IRI("http://text.com")->Set(StringLiteral("TEXT"))))

      val pickle = rp.pickle(value)

      Ok(pickle).as("application/json")

  }

  def macroses() = UserAction {
    implicit request =>
      val mac = views.html.tests.macroses(request)
      Ok(views.html.tests.suite(mac)(request))
  }
}
