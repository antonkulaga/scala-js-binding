package org.denigma.binding.binders

import org.denigma.binding.binders.extractors._
import org.denigma.binding.macroses._
import org.denigma.binding.views.{BindableView, IDGenerator}
import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.dom.raw.{Element, KeyboardEvent, MouseEvent}
import rx._
import rx.core.Var

import scala.collection.immutable.Map
import scalatags.Text._


/**
 * Binders that extracts most of reaactive variables
 * DO NO BE SCARED BY NUMBER OF IMPLICITS, THEY ARE RESOLVED AUTOMATICALLY!
 * @param view
 * @tparam View
 */
class GeneralBinder[View <: BindableView](view: View, recover: => Option[ReactiveBinder] = None)
(implicit
  mpMap: MapRxMap[View], mpTag: TagRxMap[View],
  mpString: StringRxMap[View],  mpBool: BooleanRxMap[View],
  mpDouble: DoubleRxMap[View],mpInt: IntRxMap[View],
  mpEvent: EventMap[View],  mpMouse: MouseEventMap[View],
  mpText: TextEventMap[View], mpKey: KeyEventMap[View],
  mpUI: UIEventMap[View], mpWheel: WheelEventMap[View], mpFocus: FocusEventMap[View]
) extends ReactiveBinder
  with IDGenerator
  with VisibilityBinder
  with ClassBinder
  with PropertyBinder
  with EventBinder //with Extractor
{

  val bools: Map[String, Rx[Boolean]] = mpBool.asBooleanRxMap(view)

  val strings: Map[String, Rx[String]] = mpString.asStringRxMap(view)

  val doubles: Map[String, Rx[Double]] = mpDouble.asDoubleRxMap(view)

  val ints: Map[String, Rx[Int]] = mpInt.asIntRxMap(view)

  val tags: Map[String, Rx[Tag]] = mpTag.asTagRxMap(view)

  val mouseEvents: Map[String, rx.Var[MouseEvent]] = mpMouse.asMouseEventMap(view)

  val keyboardEvents:Map[String,Var[KeyboardEvent]] = mpKey.asKeyEventMap(view)

  override def bindAttributes(el: Element, atribs: Map[String, String]): Boolean = {
    val ats: Map[String, String] = this.dataAttributesOnly(atribs)
    ifNoIDOption(el, ats.headOption.map{case (key, value) => key + "_" + value} )
    val fun: PartialFunction[(String, String), Unit] =  recover match {
      case Some(binder) =>
        val fallback = binder.elementPartial(el, ats)
        elementPartial(el, ats).orElse(fallback)

      case None => elementPartial(el, ats).orElse{case other=>}
    }
    ats.foreach(fun)
    true
  }

  //note: BAD CODE!!!
  def upPartial(el: Element, atribs: Map[String, String]): PartialFunction[(String, String), Unit] = {
    case (bname, rxName) if bname.startsWith("up-")=>
      val tup = (bname.replace("up-", ""), rxName)
      for(p <- this.view.parent) {
        //println("BINDERS = " +p.binders)
        p.binders.collectFirst{case b: GeneralBinder[_] => b.elementPartial(el, atribs)(tup)}
      }
  }

  protected val SET = "set-"
  protected val ON = "-on-"

  def setOnPartial(el: Element): PartialFunction[(String, String), Unit] = {
    case (key, value) if key.startsWith(SET) && key.contains(ON) =>
      //println(s"KEY works: $key")
      mouseEventFromKey.orElse(keyboardEventFromKey).lift(key) match {
        case Some(event) =>
          val (from: Int, to: Int)  = (key.indexOf(SET), key.indexOf(ON))
          if(from > -1 && to > from) {
            val where = key.substring(from, to)
            strings.get(where) match {
              case Some(vstr: Var[String]) => el.addEventListener[Event](event,{
                ev: Event =>
                  //println(s"${vstr.now} = $value")
                  vstr()= value
              })
              case _ => //el.addEventListener[MouseEvent](event,{ev => v()=ev})
            }
          }
          else dom.console.error(s"settings expression is wrong: $key")
        case None => dom.console.error(s"cannot find event in key =  $key with value =  $value")
      }
  }


  def elementPartial(el: Element, ats: Map[String, String]): PartialFunction[(String, String), Unit] =
    upPartial(el, ats)
      .orElse(visibilityPartial(el))
      .orElse(this.classPartial(el))
      .orElse(this.propertyPartial(el))
      .orElse(this.setOnPartial(el))
      .orElse(this.eventsPartial(el))
}
