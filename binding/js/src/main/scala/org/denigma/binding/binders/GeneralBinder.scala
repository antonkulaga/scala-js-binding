package org.denigma.binding.binders

import org.denigma.binding.binders.extractors._
import org.denigma.binding.macroses._
import org.denigma.binding.views.{BindableView, IDGenerator}
import org.scalajs.dom
import org.scalajs.dom._
import rx._

import scala.collection.immutable.Map
import scalatags.Text._


/**
 * Binders that extracts most of reaactive variables
 * DO NO BE SCARED BY NUMBER OF IMPLICITS, THEY ARE RESOLVED AUTOMATICALLY!
 * @param view
 * @tparam View
 */
class GeneralBinder[View <: BindableView](val view: View, recover: => Option[ReactiveBinder] = None)
(implicit
  mpMap: MapRxMap[View], mpTag: TagRxMap[View],
  mpString: StringRxMap[View],  mpBool: BooleanRxMap[View],
  mpDouble: DoubleRxMap[View],mpInt: IntRxMap[View],
  mpEvent: EventMap[View],  mpMouse: MouseEventMap[View],
  mpText: TextEventMap[View], mpKey: KeyEventMap[View],
  mpUI: UIEventMap[View], mpWheel: WheelEventMap[View],
  mpFocus: FocusEventMap[View], mpDrag: DragEventMap[View]
) extends ReactiveBinder
  with IDGenerator
  with VisibilityBinder
  with ClassBinder
  with PropertyBinder
  with EventBinder
  with UpDownBinder[View]
{
  lazy val bools: Map[String, Rx[Boolean]] = mpBool.asBooleanRxMap(view)

  lazy val strings: Map[String, Rx[String]] = mpString.asStringRxMap(view)

  lazy val doubles: Map[String, Rx[Double]] = mpDouble.asDoubleRxMap(view)

  lazy val ints: Map[String, Rx[Int]] = mpInt.asIntRxMap(view)

  lazy val tags: Map[String, Rx[Tag]] = mpTag.asTagRxMap(view)

  lazy val mouseEvents: Map[String, rx.Var[MouseEvent]] = mpMouse.asMouseEventMap(view)

  lazy val wheelEvents: Map[String, Var[WheelEvent]] = mpWheel.asWheelEventMap(view)

  lazy val keyboardEvents: Map[String, Var[KeyboardEvent]] = mpKey.asKeyEventMap(view)

  lazy val events: Map[String, rx.Var[Event]] = mpEvent.asEventMap(view)

  lazy val dragEvents: Map[String, Var[DragEvent]] = mpDrag.asDragEventMap(view)

  lazy val focusEvents: Map[String, Var[FocusEvent]] = mpFocus.asFocusEventMap(view)


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

  protected val SET = "set-"
  protected val ON = "-on-"

  /**
    * This function can set
    *
    * @param el Element
    * @return
    */
  def setOnPartial(el: Element): PartialFunction[(String, String), Unit] = {
    case (key, value) if key.startsWith(SET) && key.contains(ON) =>
      mouseEventFromKey.orElse(keyboardEventFromKey).lift(key) match {
        case Some(event) =>
          val (from: Int, to: Int)  = (key.indexOf(SET)+SET.length, key.indexOf(ON))
          if(from > -1 && to > from) {
            val where = key.substring(from, to)
            strings.get(where) match {
              case Some(vstr: Var[String]) =>
                //println(s"event is $event and str is $where")
                el.addEventListener[Event](event, {
                  ev: Event =>
                    println(s"${where}(${vstr.now}) = $value")
                    vstr()= value
                })
              case _ => dom.console.error(s"cannot find $where variable")
            }
          }
          else dom.console.error(s"settings expression is wrong: $key")
        case None => dom.console.error(s"cannot find event in key =  $key with value =  $value")
      }
  }


  def elementPartial(el: Element, ats: Map[String, String]): PartialFunction[(String, String), Unit] =
    upPartial(el, ats)
      .orElse(downPartial(el, ats))
      .orElse(visibilityPartial(el))
      .orElse(classPartial(el))
      .orElse(propertyPartial(el))
      .orElse(setOnPartial(el))
      .orElse(eventsPartial(el))

}
