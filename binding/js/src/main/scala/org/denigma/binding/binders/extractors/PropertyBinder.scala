package org.denigma.binding.binders.extractors

import org.denigma.binding.binders.{Events, ReactiveBinder}
import org.denigma.binding.extensions._
import org.scalajs.dom
import org.scalajs.dom.ext._
import org.scalajs.dom.raw.{HTMLInputElement, HTMLTextAreaElement}
import org.scalajs.dom.{Element, Event, KeyboardEvent}
import rx.Ctx.Owner.Unsafe.Unsafe
import rx._

import scala.collection.immutable.Map
import scala.language.implicitConversions
import scala.scalajs.js
import scala.util.{Failure, Success, Try}


/**
 * Does binding for classes
 */
trait PropertyBinder extends ScalaTagsBinder{

  self: ReactiveBinder=>

  import js.Any._

  implicit def any2String(value: js.Any): String = value.toString
  implicit def any2Double(value: js.Any): Double = value.toString.toDouble
  implicit def any2Int(value: js.Any): Int = value.toString.toInt
  implicit def any2Bool(value: js.Any): Boolean = value.toString.toLowerCase == "true"


  def strings: Map[String, Rx[String]]
  def bools: Map[String, Rx[Boolean]]
  def doubles: Map[String, Rx[Double]]
  def ints: Map[String, Rx[Int]]

  def allValues = strings ++ bools ++ doubles ++ ints

  protected lazy val specialAttributes = Map("viewbox"->"viewBox", "preserveaspectratio"-> "preserveAspectRatio")

  protected def setAttribute[T](e: Element, prop: String, value: T)(implicit conv: T => js.Any) = {
    //println(s"set attribute $prop with value $value")
    val property = specialAttributes.getOrElse(prop, prop) //fir for nonLowerCaseAttributes in SVG
    e.setAttribute(property, value.toString)
    Try(e.dyn.updateDynamic(property)(value)) match {
      case Failure(th) =>
        if(warnOnUpdateFailures)
          dom.console.warn(s"cannot set $prop to $value because of ${th.getMessage} with stack ${th.stackString} \nIN: ${e.outerHTML}")
      case _=>
    }
  }

  /**
   * Partial function that is usually added to bindProperties
   * @param el html element
   * @return partial function that will do the binding
   */
  protected def propertyPartial(el: Element): PartialFunction[(String, String), Unit] = {
    case (str, rxName) if str.startsWith("style-") => this.bindStyle(el, rxName, str.replace("style-" , ""))
    case (str, rxName) if str.startsWith("bind-style-") => this.bindStyle(el, rxName, str.replace("bind-style-" , ""))
    case (bname, rxName) if bname.startsWith("bind-") => this.bindProperty(el, rxName, bname.replace("bind-" , ""))
    case ("html" | "innerhtml" | "inner-html", rxName) =>
      strings.get(rxName) match {
        case Some(value)=>
          val prop = "innerHTML"
          value.foreach(s => el.dyn.updateDynamic(prop)(s))
          value.onVar { case v =>
            el.addEventListener(Events.change,(ev: Event) => {
             if(ev.target==ev.currentTarget) el.onExists(prop)(value => v.set(value.toString)) }
          )
            v.set(el.dyn.selectDynamic(prop).toString)
          }
        case None =>
          tags.get(rxName) match {
            case Some(tg) => tg.foreach(t => el.innerHTML = t.render)
            case None => dom.console.error(s"cannot find $rxName for innerHtml")
          }
      }
    case ("bind", rxName) => bind(el,rxName)
  }

  /**
    * binds Rx variable to a style property
    * @param el
    * @param rxName name of Rx variable
    * @param prop style property
    */
  protected def bindStyle(el: Element, rxName: String, prop: String): Unit= {
    if (strings.contains(rxName))
      stylePropertyOnRx(el, prop, strings(rxName))
    else
    if (doubles.contains(rxName))
      stylePropertyOnRx(el, prop, doubles(rxName))
    else
    if (ints.contains(rxName))
      stylePropertyOnRx(el, prop, ints(rxName))
    else
    if (bools.contains(rxName))
      stylePropertyOnRx(el, prop, bools(rxName))
    else
      this.cannotFind(el: Element, rxName, prop, allValues)
  }

  protected def subscribeOnEvent[T, Event <: dom.Event](el: Element, rxName: String, prop: String, event: String, mp: Map[String, Rx[T]])
                                                       (implicit js2var: js.Any => T): Option[Rx[T]] =
    mp.get(rxName) map {
      value =>
        //this.bindProperty(el, rxName, prop)
        varOnEvent[T, Event](el, prop, value, event)(js2var)
        //propertyOnRx(el,prop,value)
        value
    }


  protected def bindInput(inp: HTMLInputElement, rxName: String): Unit =
  {
    inp.attributes.get("type").map(_.value.toString) match {
      case Some("checkbox") =>
        subscribeOnEvent(inp, rxName, "checked", Events.change, bools)(any =>
          any.asInstanceOf[Boolean])

      case Some("radio") =>
        subscribeOnEvent(inp, rxName, "checked", Events.change, bools)(any =>
          any.asInstanceOf[Boolean])

      case Some("number") =>
        subscribeInputValue(inp, rxName, Events.keyup, doubles)
          .orElse(subscribeInputValue(inp, rxName, Events.keyup, ints))
          .orError(s"cannot find ${rxName} in ${allValues}")

      case Some("range") =>
        subscribeInputValue(inp, rxName, Events.keyup, doubles)
          .orElse(subscribeInputValue(inp, rxName, Events.keyup, ints))
          .orError(s"cannot find ${rxName} in ${allValues}")

      case _ =>
        subscribeInputValue(inp, rxName, Events.keyup, strings)
          .orElse(subscribeInputValue(inp, rxName, Events.keyup, doubles))
          .orElse(subscribeInputValue(inp, rxName, Events.keyup, ints))
          .orElse(subscribeInputValue(inp, rxName, Events.keyup, bools))
          .orError(s"cannot find ${rxName} in ${allValues}")
    }
  }


  /**
   * Binds property
   * @param el html element
   * @param rxName name of Rx
   * @return
   */
  def bind(el: Element, rxName: String): Unit =  el match
  {
    case inp: HTMLInputElement=>  bindInput(inp, rxName)
    case area: HTMLTextAreaElement =>
      subscribeInputValue(el, rxName, Events.keyup, strings)
        .orElse(subscribeInputValue(el, rxName, Events.keyup, doubles))
        .orElse(subscribeInputValue(el, rxName, Events.keyup, ints))
        .orElse(subscribeInputValue(el, rxName, Events.keyup, bools))
        .orError(s"cannot find ${rxName} in ${allValues}")

    case _ =>
      val prop = "textContent" // "innerHTML"
      strings.get(rxName) match {
        case Some(value) =>
          propertyOnRx(el, prop, value)
          varOnEvent[String, Event](el, prop, value, Events.change)
        case None => bindProperty(el, rxName, prop)
      }
  }

  protected def cannotFind[T](el: Element, rxName: String, prop: String, mp: Map[String, Rx[T]]) =
    dom.console.error(s"cannot find $rxName reactive variable for prop $prop\n, all values are: \n"+
      mp.mapValues(_.now).toList.mkString(" | ")+s"\n Element is: ${el.outerHTML}"
    )

  protected def subscribeInputValue[T](el: Element, rxName: String, event: String, mp: Map[String, Rx[T]])
                                      (implicit js2var: js.Any => T, var2js: T => js.Any): Option[Rx[T]] =
    mp.get(rxName) map {
      value =>
        val prop = "value"
        el match {
          case inp: HTMLInputElement =>
            value.foreach {
              s =>
                val (start, end) = (inp.selectionStart, inp.selectionEnd)
                inp.value = s.toString
                if (inp.selectionStart != start) inp.selectionStart = start
                if (inp.selectionEnd != end) inp.selectionEnd = end
            }

          case area: HTMLTextAreaElement =>
            value.foreach {
              s =>
                val (start, end) = (area.selectionStart, area.selectionEnd)
                area.value = s.toString
                if (area.selectionStart != start) area.selectionStart = start
                if (area.selectionEnd != end) area.selectionEnd = end
            }

          case other => propertyOnRx(el, prop, value)
        }
        varOnEvent[T, KeyboardEvent](el, prop, value, event)(js2var)
        //propertyOnRx(el,prop,value)
        value
    }

  /**
   * subscribes property to Rx, if Rx is Var then changes Var when specified event fires
   * TODO: write safe version of the function
   */
  protected def varOnEvent[T, TEvent <: dom.Event](el: Element, prop: String, value: Rx[T], event: String)
                                                      (implicit js2var: js.Any => T): Unit =
  {
    value.onVar { v =>
      el.addEventListener[TEvent](event, (ev: TEvent) => {
        if (ev.target == ev.currentTarget) el.onExists(prop) {
          newValue =>
            Try(js2var(newValue)) match {
              case Success(newVal) => v.set(newVal)
              case Failure(th) => dom.console.warn(s"cannot convert ${newValue} to Var , failure: ${th}")
            }
        }
      }
      )
      v.set(js2var(el.dyn.selectDynamic(prop)))
    }
  }


  def warnOnUpdateFailures = false

  /**
   * subscribes property to changes of Rx
   */
  protected def propertyOnRx[T](el: Element, prop: String, value: Rx[T])(implicit conv: T => js.Any): Unit =
  {
    value.foreach{v => setAttribute(el, prop, v.toString)}
  }

  protected def stylePropertyOnRx[T](el: Element, prop: String, value: Rx[T])(implicit conv: T => js.Any): Unit =
  {
    value.foreach{v=> el.style.dyn.updateDynamic(prop)(v)}
  }

  //TODO: fix this ugly piece of code
  protected def bindProperty(el: Element, rxName: String, prop: String): Unit = {
    if (strings.contains(rxName))
    {
      propertyOnRx(el, prop, strings(rxName))(js.Any.fromString)
    }
    else if (doubles.contains(rxName))
    {
      propertyOnRx(el, prop, doubles(rxName))(js.Any.fromDouble)
    }
    else if (ints.contains(rxName))
    {
      propertyOnRx(el, prop, ints(rxName))(js.Any.fromInt)
    }
    else if (bools.contains(rxName))
    {
      propertyOnRx(el, prop, bools(rxName))(js.Any.fromBoolean)
    }
    else cannotFind(el, rxName, prop, allValues)
  }

}