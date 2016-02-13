package org.denigma.binding.binders

import org.denigma.binding.views.BindableView
import org.scalajs.dom
import org.scalajs.dom.raw.Element

import scala.collection.immutable.Map
import scala.scalajs.js

/**
  * Binders that binds either to parent or to child
  */
trait UpDownBinder[View<:BindableView]{

  self: ReactiveBinder =>

  import scala.concurrent.duration._

  lazy val defaultDelay = 300  millis

  def view: View


  def downPartial(el: Element, ats: Map[String, String]): PartialFunction[(String, String), Unit]  = {
    case (bname, rxName) if rxName.contains(".") =>
      val fun: js.Function0[Any] = ()=>{
        downPartialDelayed(el, bname, rxName, ats)
      }
      dom.window.setTimeout( fun, defaultDelay.toMillis: Double)
  }

  protected def downPartialDelayed(el: Element, bname: String, rxName: String, ats: Map[String, String]) = {
    val ind = rxName.indexOf(".")
    val childName = rxName.substring(0, ind)
    val childRxName = rxName.substring(ind + 1)
    view.subviews.get(childName) match {
      case Some(child)=>
        child.binders.foreach{
          case b: ReactiveBinder =>
            //println(s"DOWN PARTIAL IS ${bname} -> ${childRxName}")
            b.elementPartial(el, ats.updated(bname, childRxName))(bname, childRxName)
          case other: child.type#ViewBinder=> // do nothing
        }

      case None => dom.console.error(s"cannot bind to child view's Rx with Name $childName and RxName ${rxName}\n " +
        s"delay is ${defaultDelay.toMillis}" +
        s"all child views are: [${view.subviews.keySet.toList.mkString(", ")}]" +
        s"")
    }
  }

  //note: BAD CODE!!!
  protected def upPartial(el: Element, atribs: Map[String, String]): PartialFunction[(String, String), Unit] = {
    case (bname, rxName) if bname.startsWith("up-")=>
      val tup = (bname.replace("up-", ""), rxName)
      for(p <- this.view.parent) {
        //println("BINDERS = " +p.binders)
        p.binders.collectFirst{case b: GeneralBinder[_] => b.elementPartial(el, atribs)(tup)}
      }
  }

  }
