package org.denigma.binding.frontend.tests

import org.scalajs.dom.{MouseEvent, HTMLElement}
import scala.collection.immutable.Map
import org.denigma.views.{OrdinaryView, ListView}
import rx._
import scalatags._
import org.scalajs.spickling.playjson._
import org.denigma.binding.models.{ rp}
import org.scalajs.dom
import org.denigma.extensions.sq
import scala.util.{Failure, Success}

import scalajs.concurrent.JSExecutionContext.Implicits.queue
/**
 * Class for testing purposes that makes a long list out of test element
 */
class PicklerView(element:HTMLElement, params:Map[String,Any]) extends OrdinaryView("PicklerView",element){
  self=>

  //RegisterPicklers.registerPicklers()

  val path: String = params.get("path").fold("test/map"){case (acc,v)=>v.toString}

  sq.get[Map[String,String]](sq.withHost(path)).onComplete{
    case Success(res)=> value() = res.foldLeft(""){case (acc,(key,v))=>acc+s"$key -> $v ||"}
    case Failure(th)=> value() = th.getMessage.toString
  }

  val value = Var("????????????????")


  override lazy val tags: Map[String, Rx[HtmlTag]] = this.extractTagRx(this)

  override lazy val strings: Map[String, Rx[String]] = this.extractStringRx(this)

  override lazy val bools: Map[String, Rx[Boolean]] = this.extractBooleanRx(this)

  //override lazy val textEvents: Map[String, Var[TextEvent]] = this.extractTextEvents(this)

  override lazy val mouseEvents: Map[String, Var[MouseEvent]] = this.extractMouseEvens(this)
}
