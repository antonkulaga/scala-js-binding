package org.denigma.binding.frontend.tests

import org.denigma.binding.views.OrdinaryView
import org.scalajs.dom.{MouseEvent, HTMLElement}
import scala.collection.immutable.Map
import rx._
import scalatags._
import org.denigma.binding.extensions.sq
import scala.util.{Failure, Success}

import scalajs.concurrent.JSExecutionContext.Implicits.queue
import scalatags.Text.Tag
import org.denigma.binding.picklers.rp

/**
 * Class for testing purposes that makes a long list out of test element
 */
class PicklerView(val elem:HTMLElement, params:Map[String,Any]) extends OrdinaryView{
  self=>

  val name = "PicklerView"

  implicit def registry = rp
  //RegisterPicklers.registerPicklers()

  val path: String = params.get("path").fold("test/map"){case (acc,v)=>v.toString}

  sq.get[Map[String,String]](sq.withHost(path)).onComplete{
    case Success(res)=> value() = res.foldLeft(""){case (acc,(key,v))=>acc+s"$key -> $v ||"}
    case Failure(th)=> value() = th.getMessage.toString
  }

  val value = Var("")


  override lazy val tags: Map[String, Rx[Tag]] = this.extractTagRx(this)

  override lazy val strings: Map[String, Rx[String]] = this.extractStringRx(this)

  override lazy val bools: Map[String, Rx[Boolean]] = this.extractBooleanRx(this)

  //override lazy val textEvents: Map[String, Var[TextEvent]] = this.extractTextEvents(this)

  override lazy val mouseEvents: Map[String, Var[MouseEvent]] = this.extractMouseEvents(this)
}
