package org.denigma.binding.views

import org.denigma.binding.binders.{ScalaTagsBinder, GeneralBinding, EventBinding}
import org.scalajs.dom
import org.scalajs.dom.{HTMLElement, MouseEvent}
import rx.Rx
import rx.core.{Obs, Var}

import scala.collection.immutable.Map
import scalatags.Text.Tag

object OrdinaryView {
  /**
   * created if we do not know the view at all
   * @param name of the view
   * @param elem dom element inside
   */
  class JustView(val name:String,val elem:dom.HTMLElement) extends OrdinaryView
  {

    override def tags: Map[String, Rx[Tag]] = this.extractTagRx(this)

    override def strings: Map[String, Rx[String]] = this.extractStringRx(this)

    override def bools: Map[String, Rx[Boolean]] = this.extractBooleanRx(this)

    override def mouseEvents: Map[String, Var[MouseEvent]] = this.extractMouseEvents(this)


  }

  def apply(name:String,elem:dom.HTMLElement) = new JustView(name,elem)

}


trait OrdinaryView extends OrganizedView with GeneralBinding
  with ScalaTagsBinder
  with EventBinding
{


  override def makeDefault(name:String,el:HTMLElement) = OrdinaryView(name:String,el)

  override def bindDataAttributes(el:HTMLElement,ats:Map[String, String]) = {
    this.bindHTML(el,ats)
    this.bindProperties(el,ats)
    this.bindEvents(el,ats)
  }

  //TODO: rewrite
  override def bindProperties(el:HTMLElement,ats:Map[String, String]): Unit = for {
    (key, value) <- ats
  }{
    this.visibilityPartial(el,value)
      .orElse(this.classPartial(el,value))
      .orElse(this.propertyPartial(el,key.toString,value))
      .orElse(this.upPartial(el,key.toString,value))
      .orElse(this.loadIntoPartial(el,value))
      .orElse(this.otherPartial)(key.toString)//key.toString is the most important!
  }

  protected def upPartial(el:HTMLElement,key:String,value:String):PartialFunction[String,Unit] = {
    case bname if bname.startsWith("up-bind-")=>
      val my = key.replace("up-bind-","")
      this.strings.get(my) match {
        case Some(str: rx.Var[String])=> this.searchUp[OrdinaryView](p=>p.strings.contains(value)) match {
          case Some(p)=>

            val rs: rx.Rx[String] = p.strings(value)
            str() = rs.now
            Obs(rs){ str()=rs.now }

          case Some(other)=>dom.console.error(s"$my is not a Var")

          case None=>dom.console.log("failed to find upper binding")
        }
        case None=>dom.console.error(s"binding to unkown variable $my")

      }
  }
}
