package org.denigma.binding.binders

import org.denigma.binding.binders.extractors._
import org.denigma.binding.macroses._
import org.denigma.binding.views.{BindableView, BasicView, IDGenerator}
import org.scalajs.dom.raw.{Element, HTMLElement, KeyboardEvent, MouseEvent}
import rx._

import scala.collection.immutable.Map
import scalatags.Text._


/**
 * Binders that extracts most of reaactive variables
 * DO NO BE SCARED BY NUMBER OF IMPLICITS, THEY ARE RESOLVED AUTOMATICALLY!
 * @param view
 * @tparam View
 */
class GeneralBinder[View<:BindableView](view:View, recover: =>Option[ReactiveBinder] = None)
                                       (implicit
  mpMap:MapRxMap[View], mpTag:TagRxMap[View],
  mpString:StringRxMap[View],  mpBool:BooleanRxMap[View],
  mpDouble:DoubleRxMap[View],mpInt:IntRxMap[View],
  mpEvent:EventMap[View],  mpMouse:MouseEventMap[View],
  mpText:TextEventMap[View], mpKey:KeyEventMap[View],
  mpUI:UIEventMap[View], mpWheel:WheelEventMap[View], mpFocus:FocusEventMap[View]
) extends ReactiveBinder
  with VisibilityBinder
  with ClassBinder
  with PropertyBinder
  with ScalaTagsBinder
  with EventBinder //with Extractor
  with IDGenerator
{

  val bools: Map[String, Rx[Boolean]] = mpBool.asBooleanRxMap(view)

  val strings: Map[String, Rx[String]] = mpString.asStringRxMap(view)

  val doubles: Map[String, Rx[Double]] = mpDouble.asDoubleRxMap(view)

  val ints: Map[String, Rx[Int]] = mpInt.asIntRxMap(view)

  val tags: Map[String, Rx[Tag]] = mpTag.asTagRxMap(view)

  val mouseEvents: Map[String, rx.Var[MouseEvent]] = mpMouse.asMouseEventMap(view)

  val keyboardEvents:Map[String,Var[KeyboardEvent]] = mpKey.asKeyEventMap(view)

  override def bindAttributes(el: Element, atribs: Map[String, String]):Boolean = {
    val ats: Map[String, String] = this.dataAttributesOnly(atribs)
    ifNoIDOption(el,  ats.headOption.map{case (key,value)=>key+"_"+value} )
    this.bindHTML(el,ats)
    val fun: PartialFunction[(String, String), Unit] =  recover match {
      case Some(binder)=>
        val fallback = binder.elementPartial(el,ats)
        elementPartial(el,ats).orElse(fallback)

      case None=> elementPartial(el,ats).orElse{case other=>}
    }
    ats.foreach(fun)
    true
  }

  //note: BAD CODE!!!
  def upPartial(el: Element, atribs: Map[String, String]): PartialFunction[(String,String),Unit] = {
    case (bname,rxName) if bname.startsWith("up-")=>
      val tup = (bname.replace("up-",""),rxName)
      for(p<-this.view.parent) {
        //println("BINDERS = " +p.binders)
        p.binders.collectFirst{case b:GeneralBinder[_]=>b.elementPartial(el,atribs)(tup)}
      }
  }

  def elementPartial(el: Element,ats:Map[String, String]): PartialFunction[(String,String),Unit] =
    upPartial(el,ats)
      .orElse(visibilityPartial(el))
      .orElse(this.classPartial(el))
      .orElse(this.propertyPartial(el))
      .orElse(this.eventsPartial(el))

}
