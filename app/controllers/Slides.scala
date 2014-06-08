package controllers

import org.scalax.semweb.rdf.IRI
import org.denigma.binding.models._
import play.api.mvc.Controller


object Slides extends PJaxPlatformWith("index") {


  def slide(slide:String) = UserAction {implicit request=>
    val res = slide match {
      case "bind"=>views.html.slides.bind("It can bind")(request)
      case "collection"=>views.html.slides.collection("It can bind to collections")(request)
      case "remotes"=>views.html.slides.remote("It can make remote requests")(request)
      case "remotes"=>views.html.slides.rdf("It can bind rdf shapes")(request)
      case "parse"=>views.html.slides.parse("It can parse")(request)
      case "code"=>views.html.slides.code("The code will tell you")(request)
      case "scalajs"=>views.html.slides.scalajs("Benefits of scalajs")(request)
      case "rdf"=>views.html.slides.rdf("It can bind views to rdf models")(request)
      case _=>views.html.slides.code("The code will tell you")(request)

    }
    this.pj(res)(request)
  }

}

object SlidesMenu  extends Controller  with ItemsController{

  type ModelType = MenuItem


  val dom =  IRI(s"http://domain")

  var items:List[ModelType] =     List(
    "slides/bind"->"Basic binding example",
    "slides/collection"->"Collection binding",
    //"slides/remotes"->"Remove views",
    "slides/rdf"->"RDF views"

  //"slides/parse"->"Parsing example"
  ) map{ case (url,title)=> MenuItem(dom / url,title)}


}
