package org.denigma.binding.frontend.tests

import org.denigma.binding.extensions.sq
import org.denigma.binding.views.BindableView
import org.scalajs.dom.HTMLElement
import rx._

import scala.collection.immutable.Map
import scala.util.{Failure, Success}

/**
 * Class for testing purposes that makes a long list out of test element
 */
/*
class PicklerView(val elem:HTMLElement, val params:Map[String,Any]) extends BindableView{
  self=>

  //RegisterPicklers.registerPicklers()

  val path: String = params.get("path").fold("test/map"){case (acc,v)=>v.toString}

  sq.get[Map[String,String]](sq.withHost(path)).onComplete{
    case Success(res)=> value() = res.foldLeft(""){case (acc,(key,v))=>acc+s"$key -> $v ||"}
    case Failure(th)=> value() = th.getMessage.toString
  }

  val value = Var("")


    override def activateMacro(): Unit = { extractors.foreach(_.extractEverything(this))}

  override protected def attachBinders(): Unit = binders = BindableView.defaultBinders(this)

}
*/
