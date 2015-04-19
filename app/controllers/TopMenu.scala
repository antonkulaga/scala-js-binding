package controllers

import org.denigma.binding.composites.BindingComposites
import org.denigma.binding.composites.BindingComposites._
import org.denigma.binding.models._
import org.denigma.endpoints.{PrickleController, UserAction}
import org.denigma.semweb.rdf.IRI
import play.api.mvc._
import prickle.{Pickle, Unpickle}

import scala.concurrent.Future

object TopMenu  extends Controller with PrickleController{

  type ModelType = MenuItem


  val dom =  IRI(s"http://domain")

  var items:List[ModelType] =  List(
    "slides/bind"->"About ScalaJS Binding",

    "slides/scalajs"->"About benefits of ScalaJS"

  ) map{ case (url,title)=> MenuItem(dom / url,title)}

  def add() = UserAction.async(this.unpickleWith{
    st => Unpickle[MenuItem].fromString(st)
  }){ implicit request=>

    items = request.body::items
    Future.successful(this.pTRUE)
  }

  def delete() = UserAction.async(this.unpickleWith{
    st => Unpickle[MenuItem].fromString(st)
  }){ implicit request=>

    items = items.filterNot(i=>i==request.body)
    Future.successful(this.pTRUE)
  }


  def all() = UserAction.async { implicit request=>
    Future.successful(pack(Pickle.intoString(items)))
  }


}
