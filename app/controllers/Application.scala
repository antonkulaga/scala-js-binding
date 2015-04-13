package controllers

import java.io.File

import org.denigma.endpoints.UserAction
import play.api._
import play.api.mvc._

import scala.reflect.ClassTag
import scalacache._
import memoization._
import concurrent.duration._
import language.postfixOps


import scalacache.lrumap.LruMapCache

object Application extends Controller {

  def bindingFile(file: String) = Action {    Ok.sendFile(new File(s"binding/$file")) }
  def modelsFile(file: String) = Action {    Ok.sendFile(new File(s"models/$file")) }
  def frontendFile(file: String) = Action {    Ok.sendFile(new File(s"frontend/$file")) }

  implicit val scalaCache = ScalaCache(LruMapCache(1000))


/*  protected def mainStyles:String = memoize(60 seconds) {
    styles.MyStyles.render

  }


  def myStyles() = UserAction { implicit request=>
    Ok(mainStyles).as("text/css")
  }*/
}