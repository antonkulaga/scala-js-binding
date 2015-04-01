package controllers

import org.denigma.endpoints.UserAction
import play.api.libs.json.Json
import play.api.mvc._
import play.twirl.api.Html

/**
 * Just a class for P-Jax
 * @param name
 */
class PJaxPlatformWith(val name:String) extends Controller  {

  def index(): Action[AnyContent] =  UserAction {implicit request=>
    this.page(request)
  }

  def page(implicit request:UserRequestHeader,html:Option[Html] = None,into:String = "main"): Result = {
    Ok(views.html.index(request,html,into,true))
  }

//  def index(): Action[AnyContent] =  UserAction {
//    implicit request=>
//      Ok(views.html.test(request))
//  }


//  def pj[T](action:String,html:Html)(implicit req:UserRequestHeader): Html =
//    pj(name,action:String,html:Html)(req)

//  def pj[T](controller:String,action:String,html:Html)(implicit req:UserRequestHeader): Html =
//    if(req.headers.keys("X-PJAX")) html  else views.html.webintelligence.index(controller,action,html)(req)

  def pj[T<:UserRequestHeader](html:Html)(implicit request:T): Result =
  request.pjax.fold(page(request,Some(html),"main"))(tr=>Ok(html))
  //  if(request.pjax.isEmpty) Ok(page(request,Some(html),"main")) else  Ok(html)

  def tellBad(message:String) = BadRequest(Json.obj("status" ->"KO","message"->message)).as("application/json")

}