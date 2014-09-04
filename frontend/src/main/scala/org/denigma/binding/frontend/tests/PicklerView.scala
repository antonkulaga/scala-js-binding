package org.denigma.binding.frontend.tests

import org.denigma.binding.extensions.sq
import org.denigma.binding.picklers.rp
import org.denigma.binding.views.OrdinaryView
import org.scalajs.dom.{HTMLElement, MouseEvent}
import rx._

import scala.collection.immutable.Map
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.util.{Failure, Success}
import scalatags.Text.Tag

/**
 * Class for testing purposes that makes a long list out of test element
 */
class PicklerView(val elem:HTMLElement, val params:Map[String,Any]) extends OrdinaryView{
  self=>

  implicit def registry = rp
  //RegisterPicklers.registerPicklers()

  val path: String = params.get("path").fold("test/map"){case (acc,v)=>v.toString}

  sq.get[Map[String,String]](sq.withHost(path)).onComplete{
    case Success(res)=> value() = res.foldLeft(""){case (acc,(key,v))=>acc+s"$key -> $v ||"}
    case Failure(th)=> value() = th.getMessage.toString
  }

  val value = Var("")


    override def activateMacro(): Unit = { extractors.foreach(_.extractEverything(this))}


}
