package org.denigma.binding.frontend.slides

import org.denigma.binding.picklers.rp
import org.denigma.binding.controls.ShapeView
import org.denigma.binding.extensions._
import org.denigma.binding.storages.AjaxModelQueryStorage
import org.denigma.binding.views.CollectionView
import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.dom.extensions._
import org.scalax.semweb.rdf.IRI
import org.scalax.semweb.shex.PropertyModel
import rx.{Rx, Var}

import scala.Predef
import scala.collection.immutable._
import scala.util.{Failure, Success}
import scalatags.Text.Tag
import scalajs.concurrent.JSExecutionContext.Implicits.queue
class ShapeEditor (element:HTMLElement,params:Map[String,Any]) extends ShapeView("ShapeView",element,params){
  override def tags: Map[String, Rx[Tag]] = Map.empty

  override def mouseEvents: Predef.Map[String, Var[MouseEvent]] = Map.empty

  override def strings: Map[String, Rx[String]] = Map.empty

  override def bools: Map[String, Rx[Boolean]] = Map.empty
}
