package controllers

import org.denigma.endpoints.UserAction
import play.api.libs.json.Json
import play.api.mvc._
import play.twirl.api.Html

import scala.collection.immutable



trait PjaxController extends Controller  {

  def index(): Action[AnyContent] =  UserAction {implicit request=>
    this.page(request)
  }


  def page(implicit request:UserRequestHeader,html:Option[Html] = None,into:String = "main"): Result = {
    Ok(content = views.html.index(request, html, into, true))
  }


  def pj[T<:UserRequestHeader](html:Html)(implicit request:T): Result = 
    request.pjax.fold(page(request,Some(html),"main"))(tr=>Ok(html))

  def tellBad(message:String) = BadRequest(Json.obj("status" ->"KO","message"->message)).as("application/json")

}