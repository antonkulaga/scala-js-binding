package org.denigma.binding.views

import org.denigma.binding.binders._
import org.denigma.binding.binders.extractors.Extractor
import org.denigma.binding.macroses._
import org.scalajs.dom
import org.scalajs.dom._
import rx._
import rx.core.{Obs, Var}

import scala.Predef
import scala.collection.immutable.Map
import scalatags.Text.Tag

object BindableView {

  class JustView(val elem:dom.HTMLElement, val params:Map[String,Any]) extends BindableView
  {
    override def activateMacro(): Unit = {
      this.extractors.foreach(_.extractEverything(this))
    }

    override protected def attachBinders(): Unit = this.withBinders( BindableView.defaultBinders(this))
  }

  implicit def defaultBinders(view:BindableView) = new GeneralBinder(view)::new NavigationBinding(view)::Nil

  def apply(elem:dom.HTMLElement,params:Map[String,Any] = Map.empty) = new JustView(elem,params)

}

trait BindableView extends ReactiveView
{
  type Binder = BasicBinding

  protected def attachBinders():Unit

  var binders:List[Binder] = List.empty

  def withBinders(bns:List[Binder]):this.type = {
    this.binders = this.binders ++ bns
    this
  }

  def withBinders(binder:Binder*):this.type  = withBinders(binder.toList)


  def extractors = binders.view.collect{case b:Extractor=>b}


  def makeDefault(el:HTMLElement,props:Map[String,Any] = Map.empty):ChildView = {
    //debug(s"NAME IS $name")
    BindableView(el,props)
  }

  override def bindAttributes(el:HTMLElement,ats:Map[String, String]) = {
    binders.foreach(b=>b.bindAttributes(el,ats))
  }

  /**
   * is used to fill in all variables extracted by macro
   * usually it is just
   * this.extractEverything(this)
   */
  def activateMacro():Unit


  override def bindView(el:HTMLElement) = {
    this.attachBinders()
    activateMacro()
    this.bind(el)
  }

}






