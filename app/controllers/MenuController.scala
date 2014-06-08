package controllers

import org.denigma.binding.models._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc._
import org.scalax.semweb.rdf.{Res, IRI}
import play.api.libs.json.{Json, JsValue}
import org.scalajs.spickling.PicklerRegistry
import org.scalajs.spickling.playjson._
import org.scalax.semweb.sparql._
import org.scalajs.spickling.PicklerRegistry
import org.denigma.binding.models._

import org.scalax.semweb.rdf.IRI
import org.denigma.binding.play.ItemsController


object TopMenu  extends Controller  with ItemsController{

  type ModelType = MenuItem


  val dom =  IRI(s"http://domain")

  var items:List[ModelType] =  List(
    "slides/bind"->"About ScalaJS Binding",

    "slides/scalajs"->"About benefits of ScalaJS"

  ) map{ case (url,title)=> MenuItem(dom / url,title)}


}
