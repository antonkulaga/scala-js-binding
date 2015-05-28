package controllers

import controllers.TopMenu._
import org.denigma.endpoints.UserAction
import org.denigma.semweb.rdf.IRI
import org.denigma.binding.models._
import play.api.mvc.Controller
import prickle.Pickle

import scala.concurrent.Future


object Slides extends PjaxController {


  def slide(slide:String) = UserAction {implicit request=>
    val res = slide match {
      case "bind"=>views.html.slides.bind("It can bind")(request)
      case "collection"=>views.html.slides.collection("It can bind to collections")(request)
      case "editing"=>views.html.slides.editing("It provides some views for better text editing")
      case "remotes"=>views.html.slides.rdf("It can bind rdf shapes")(request)
      case "parse"=>views.html.slides.parse("It can parse")(request)
      case "code"=>views.html.slides.code("The code will tell you")(request)
      case "scalajs"=>views.html.slides.scalajs("Benefits of scalajs")(request)
      case "rdf"=>views.html.slides.rdf("It can bind views to rdf models")(request)
      case "data"=>  views.html.slides.data("Data editing")(request)
      case "feed"=>   views.html.papers.reports(request)
        //views.html.slides.data("Provides some controls for working with data")(request)
      case "sparql"=>views.html.slides.sparql("It can do sparql parsing")(request)
      case "globe"=>views.html.slides.globe("It can do sparql parsing")(request)

      case _=>views.html.slides.code("The code will tell you")(request)

    }
    this.pj(res)(request)
  }

}

object SlidesMenu  extends Controller  {

  type ModelType = MenuItem
  import org.denigma.binding.composites.BindingComposites
  import BindingComposites._


  val dom =  IRI(s"http://domain")

  var items:List[ModelType] =     List(
    "slides/bind"->"Basic binding example",
    "slides/collection"->"Collection binding",
    "slides/editing"->"Page editing",
    //"slides/data"->"Data editing",
    "slides/feed" -> "Feed",
    "slides/rdf"->"RDF views"
  //"slides/parse"->"Parsing example"
  ) map{ case (url,title)=> MenuItem(dom / url,title)}

  def all() = UserAction.async { implicit request=>
    Future.successful(pack(Pickle.intoString(items)))
  }

}
