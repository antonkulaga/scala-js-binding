package org.denigma.binding.binders

import org.denigma.binding.binders.extractors.{EventBinding, ScalaTagsBinder, Extractor}
import org.denigma.binding.macroses._
import org.denigma.binding.views.{BindableView, OrganizedView}
import org.scalajs.dom
import org.scalajs.dom.{HTMLElement, MouseEvent}
import rx._
import rx.core.Obs

import scala.collection.immutable.Map
import scalatags.Text._


class GeneralBinder(view:BindableView) extends PrimitivesBinder with ScalaTagsBinder with EventBinding with Extractor
{


  var bools: Map[String, Rx[Boolean]] = Map.empty

  var strings: Map[String, Rx[String]] = Map.empty

  var tags: Map[String, Rx[Tag]] = Map.empty

  var mouseEvents: Map[String, rx.Var[MouseEvent]] = Map.empty


  def extractEverything[T
  :MapRxMap
  :TagRxMap
  :StringRxMap :BooleanRxMap
  :EventMap :MouseEventMap
  :TextEventMap :KeyEventMap
  :UIEventMap :WheelEventMap :FocusEventMap](value:T) =
  {
    strings = strings ++ this.extractStringRx(value)
    tags = tags ++ this.extractTagRx(value)
    bools = bools ++ this.extractBooleanRx(value)
    mouseEvents = mouseEvents ++ this.extractMouseEvents(value)
  }


  override def id: String = view.id


  def bindDataAttributes(el:HTMLElement,ats:Map[String, String]) = {
    this.bindHTML(el,ats)
    this.bindProperties(el,ats)
    this.bindEvents(el,ats)
  }

  //TODO: rewrite
  /**
   * Binds
   * @param el
   * @param ats
   */
  override def bindProperties(el:HTMLElement,ats:Map[String, String]): Unit = for {
    (key, value) <- ats
  }{
    this.visibilityPartial(el,value)
      .orElse(this.classPartial(el,value))
      .orElse(this.propertyPartial(el,key.toString,value))
      .orElse(this.upPartial(el,key.toString,value))
      .orElse(this.otherPartial)(key.toString)//key.toString is the most important!
  }

  protected def hasGeneralBinding(view:OrganizedView) = view match {
    case v:BindableView=> v.binders.exists(b=>b.isInstanceOf[PrimitivesBinder])
    case _=> false
  }

  protected def parentGeneralBinder =  view.nearestParentOf{
    case view:BindableView if view.binders.exists(b=>b.isInstanceOf[GeneralBinder]) =>
      view.binders.collectFirst{case b:GeneralBinder=>b}.get}


  protected def upPartial(el:HTMLElement,key:String,value:String):PartialFunction[String,Unit] = {
    case prop if prop.startsWith("up-")=>
      prop.replace("up-","") match {
        case bname if bname.startsWith("bind-")=>
          //debug(key+" | string matches | "+value)
          val my = key.replace("bind-","")
          this.strings.get(my) match {
            case Some(str: rx.Var[String])=>
              this.parentGeneralBinder match {
                case Some(binder)=>
                  val rs: rx.Rx[String] = binder.strings(value)
                  str() = rs.now
                  Obs(rs){ str()=rs.now }
                case None =>dom.console.log("failed to find upper binding")
              }
            case None=>dom.console.error(s"binding to unkown variable $my")

          }

        case "class" =>
          //this.bindClass(el,value)
          this.parentGeneralBinder.foreach{b=>b.bindClass(el,key)} //TODO REWRITE COMPLETELY
        case str if str.startsWith("class-")=>
          str.replace("class-","") match {
          case cl if cl.endsWith("-if")=>
            //this.classIf(el,cl.replace("-if",""),value)
            this.parentGeneralBinder.foreach{b=>b.classIf(el,cl.replace("-if",""),value)} //TODO REWRITE COMPLETELY
          case cl if cl.endsWith("-unless")=>
            //this.classUnless(el,cl.replace("-unless",""),value)
            this.parentGeneralBinder.foreach{b=>b.classUnless(el,cl.replace("-unless",""),value)} //TODO REWRITE COMPLETELY
        }
        case "event-click"=>
          this.parentGeneralBinder.foreach{case b=>
            //b.eventsPartial(el,value)(prop)
            b.mouseEvents.get(value) match {
              case Some(ev)=>b.bindClick(el,key,ev)
              case _ =>
                dom.console.error(s"cannot bind click event of ${this.id} to $value")
                dom.console.log("current events =" + this.mouseEvents.keys.toString())

            }
          }

      }
  }

  def bindAttributes(el: HTMLElement, ats: Map[String, String]): Unit = {
    this.bindDataAttributes(el,this.dataAttributesOnly(ats))
  }
}
