package org.denigma.views

import org.scalajs.dom

import scala.collection.mutable
import org.scalajs.dom.{Attr, HTMLElement}
import org.denigma.binding.{EventBinding, ScalaTagsBinder, GeneralBinding}
import scala.collection.immutable.Map
import rx.core.Obs


/**
 * Just a class that can bind either properties of HTML rxes
 * @param name
 * @param elem
 */
abstract class OrdinaryView(name:String,elem:dom.HTMLElement) extends OrganizedView(name,elem) with GeneralBinding
  with ScalaTagsBinder
  with EventBinding
{


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
