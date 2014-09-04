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

object OrdinaryView {
  /**
   * created if we do not know the view at all
   * @param name of the view
   * @param elem dom element inside
   */
  class JustView(override val name:String,val elem:dom.HTMLElement) extends OrdinaryView
  {


    override def params: Map[String, Any] = Map.empty

    override def activateMacro(): Unit = { 
      this.extractors.foreach(_.extractEverything(this))
    }


  }

  def apply(name:String,elem:dom.HTMLElement) = new JustView(name,elem)

}

trait OrdinaryView extends BindableView
{
  
  
  var binders:List[BasicBinding] = List.empty
  
  def extractors = binders.view.collect{case b:Extractor=>b}


  override def makeDefault(name:String,el:HTMLElement) = {
    //debug(s"NAME IS $name")
    OrdinaryView(name:String,el)
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

  protected def attachBinders() = {
    binders = new GeneralBinder(this)::new NavigationBinding(this)::binders
  }

  override def bindView(el:HTMLElement) = {
    this.attachBinders()
    activateMacro()
    this.bind(el)
  }

}






