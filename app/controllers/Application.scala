package controllers

import java.io.File

import play.api._
import play.api.mvc._


object Application extends Controller {

def scalajsFile(file: String) = Action {    Ok.sendFile(new File(s"scalajs/$file")) }

}
