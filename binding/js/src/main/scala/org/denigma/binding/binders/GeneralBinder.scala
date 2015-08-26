package org.denigma.binding.binders

import org.denigma.binding.binders.extractors._
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
class GeneralBinder[View,Binder<:BasicBinder](view:View, recover:Option[Binder] = None)
                                       (implicit
  mpMap:MapRxMap[View], mpTag:TagRxMap[View],
  mpString:StringRxMap[View],  mpBool:BooleanRxMap[View],
  mpEvent:EventMap[View],  mpMouse:MouseEventMap[View],
  mpText:TextEventMap[View], mpKey:KeyEventMap[View],
  mpUI:UIEventMap[View], mpWheel:WheelEventMap[View], mpFocus:FocusEventMap[View]
) extends BasicBinder
  with VisibilityBinder
  with ClassBinder
  with PropertyBinder
  with ScalaTagsBinder
  with EventBinder //with Extractor
{

  val bools: Map[String, Rx[Boolean]] = mpBool.asBooleanRxMap(view)

  val strings: Map[String, Rx[String]] = mpString.asStringRxMap(view)

  val tags: Map[String, Rx[Tag]] = mpTag.asTagRxMap(view)

  val mouseEvents: Map[String, rx.Var[MouseEvent]] = mpMouse.asMouseEventMap(view)

  val keyboardEvents:Map[String,Var[KeyboardEvent]] = mpKey.asKeyEventMap(view)

  override def bindAttributes(el: HTMLElement, atribs: Map[String, String]): Unit = {
    val ats = this.dataAttributesOnly(atribs)
    this.bindHTML(el,ats)
    val fun: PartialFunction[(String, String), Unit] =  recover match {
      case Some(binder)=>
        val fallback = binder.elementPartial(el,ats)
        elementPartial(el,ats).orElse(fallback)

      case None=> elementPartial(el,ats).orElse{case other=>}
    }
    ats.foreach(fun)
  }

  def elementPartial(el: HTMLElement,ats:Map[String, String]): PartialFunction[(String,String),Unit] =
    this.visibilityPartial(el)
      .orElse(this.classPartial(el))
      .orElse(this.propertyPartial(el))
      .orElse(this.eventsPartial(el))


}
