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
  /**
   * created if we do not know the view at all
   * @param name of the view
   * @param elem dom element inside
   */
  class JustView(override val name:String,val elem:dom.HTMLElement) extends BindableView
  {


    override def params: Map[String, Any] = Map.empty

    override def activateMacro(): Unit = { 
      this.extractors.foreach(_.extractEverything(this))
    }

    override protected def attachBinders(): Unit = binders = BindableView.defaultBinders(this)
  }

  implicit def defaultBinders(view:BindableView) = new GeneralBinder(view)::new NavigationBinding(view)::Nil

  def apply(name:String,elem:dom.HTMLElement) = new JustView(name,elem)

}

trait BindableView extends OrganizedView
{
  type Binder = BasicBinding

  protected def attachBinders():Unit

  var binders:List[BasicBinding] = List.empty
  
  def extractors = binders.view.collect{case b:Extractor=>b}


  override def makeDefault(name:String,el:HTMLElement) = {
    //debug(s"NAME IS $name")
    BindableView(name:String,el)
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






