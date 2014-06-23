package org.denigma.binding.controls

import org.denigma.binding.picklers.rp
import org.denigma.binding.extensions._
import org.denigma.binding.semantic.ModelCollection
import org.denigma.binding.storages.AjaxModelQueryStorage
import org.denigma.binding.views.CollectionView
import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.dom.extensions._
import org.scalax.semweb.rdf.IRI
import org.scalax.semweb.shex.{Shape, PropertyModel}
import rx.{Rx, Var}

import scala.collection.immutable._
import scala.util.{Failure, Success}
import scalatags.Text.Tag
import scalajs.concurrent.JSExecutionContext.Implicits.queue

abstract class ShapeView(val name:String = "ShapeView", val elem:HTMLElement,val params:Map[String,Any]) extends ModelCollection
{

}
