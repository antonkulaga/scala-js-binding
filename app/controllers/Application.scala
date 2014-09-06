package controllers

import java.io.File

import play.api._
import play.api.mvc._


object Application extends Controller {

  def bindingFile(file: String) = Action {    Ok.sendFile(new File(s"binding/$file")) }
  def modelsFile(file: String) = Action {    Ok.sendFile(new File(s"models/$file")) }
  def frontendFile(file: String) = Action {    Ok.sendFile(new File(s"frontend/$file")) }


}
