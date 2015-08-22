package org.denigma.binding.binders

import org.denigma.binding.binders.extractors.{EventBinder, ScalaTagsBinder}
import org.denigma.binding.macroses._
import org.denigma.binding.views.{BindableView, OrganizedView}
import org.scalajs.dom
import org.scalajs.dom.raw.{KeyboardEvent, MouseEvent, HTMLElement}
import rx._
import rx.core.Obs

import scala.collection.immutable.Map
import scalatags.Text._


/**
 * Binders that extracts most of reaactive variables
 * DO NO BE SCARED BY NUMBER OF IMPLICITS, THEY ARE RESOLVED AUTOMATICALLY!
 * @param view
 * @tparam View
 */
class GeneralBinder[View<:BindableView](view:View)
                                       (implicit
  mpMap:MapRxMap[View], mpTag:TagRxMap[View],
  mpString:StringRxMap[View],  mpBool:BooleanRxMap[View],
  mpEvent:EventMap[View],  mpMouse:MouseEventMap[View],
  mpText:TextEventMap[View], mpKey:KeyEventMap[View],
  mpUI:UIEventMap[View], mpWheel:WheelEventMap[View], mpFocus:FocusEventMap[View]
)
  extends PrimitivesBinder with ScalaTagsBinder with EventBinder //with Extractor
{

  var bools: Map[String, Rx[Boolean]] = mpBool.asBooleanRxMap(view)

  var strings: Map[String, Rx[String]] = mpString.asStringRxMap(view)

  var tags: Map[String, Rx[Tag]] = mpTag.asTagRxMap(view)

  var mouseEvents: Map[String, rx.Var[MouseEvent]] = mpMouse.asMouseEventMap(view)

  var keyboardEvents:Map[String,Var[KeyboardEvent]] = mpKey.asKeyEventMap(view)


  def bindDataAttributes(el:HTMLElement,ats:Map[String, String]) = {
    this.bindHTML(el,ats)
    this.bindProperties(el,ats)
    this.bindEvents(el,ats)
  }

  def bindHTML(el:HTMLElement,key:String,value:String,ats:Map[String, String]):PartialFunction[String,Unit] = {
    this.visibilityPartial(el,value)
      .orElse(this.classPartial(el,value))
      .orElse(this.propertyPartial(el,key.toString,value))
      .orElse(this.upPartial(el,key.toString,value))
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

  //TODO: rewrite
  protected def hasGeneralBinding(view:OrganizedView) = view match {
    case v:BindableView=> v.binders.exists(b=>b.isInstanceOf[PrimitivesBinder])
    case _=> false
  }

  //TODO: rewrite
  protected def parentGeneralBinder =  view.nearestParentOf{
    case view:BindableView if view.binders.exists(b=>b.isInstanceOf[GeneralBinder[_]]) =>
      view.binders.collectFirst{case b:GeneralBinder[_]=>b}.get}


  protected def upPartial(el:HTMLElement,key:String,value:String):PartialFunction[String,Unit] = {//TODO:rewrite
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
            case _=>dom.console.error(s"binding to unkown variable $my")

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
              case Some(ev)=>b.bindClick(el,ev)
              case _ =>
                dom.console.error(s"cannot bind click event of ${view.id} to $value")
                dom.console.log("current events =" + this.mouseEvents.keys.toString())

            }
          }

      }
  }

  def bindAttributes(el: HTMLElement, ats: Map[String, String]): Unit = {
    this.bindDataAttributes(el,this.dataAttributesOnly(ats))
  }
}
