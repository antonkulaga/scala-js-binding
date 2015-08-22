package org.denigma.binding.binders

import org.denigma.binding.macroses._
import org.denigma.binding.views.BindableView
import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.dom.ext._
import org.scalajs.dom.raw.HTMLElement
import rx.Var

import scala.collection.immutable._


class MapItemsBinder[View<:BindableView](view:View, reactiveMap:Map[String,Var[String]])
                                        (implicit
                                         mpMap:MapRxMap[View], mpTag:TagRxMap[View],
                                         mpString:StringRxMap[View],  mpBool:BooleanRxMap[View],
                                         mpEvent:EventMap[View],  mpMouse:MouseEventMap[View],
                                         mpText:TextEventMap[View], mpKey:KeyEventMap[View],
                                         mpUI:UIEventMap[View], mpWheel:WheelEventMap[View], mpFocus:FocusEventMap[View]
                                          )
  extends GeneralBinder[View](view)(mpMap,mpTag,mpString,mpBool,mpEvent,mpMouse,mpText,mpKey,mpUI,mpWheel,mpFocus) {

  //TODO: rewrite props
  override def bindProperties(el: HTMLElement, ats: Map[String, String]) = for {
    (key, value) <- ats
  } {
    this.visibilityPartial(el, value)
      .orElse(this.classPartial(el, value))
      .orElse(this.itemPartial(el, key.toString, value))
      .orElse(this.propertyPartial(el, key.toString, value))
      .orElse(this.otherPartial)(key.toString)
  }

  /**
   * Binds property value to attribute
   * @param el Element
   * @param key name of the binding key
   * @param att binding attribute
   */
  def bindItemProperty(el:HTMLElement,key:String,att:String) = (key.toString.replace("item-",""),el.tagName.toLowerCase) match {
    case ("bind","input")=>
      el.attributes.get("type").map(_.value.toString) match {
        case Some("checkbox") => //skip
        case _ => this.reactiveMap.get(att).foreach{str=>
          el.onkeyup =this.makePropHandler[KeyboardEvent](el,str,"value")
          this.bindInput(el,key,str)
        }
      }

    case ("bind","textarea")=>
      this.reactiveMap.get(att).foreach{str=>
        el.onkeyup = this.makePropHandler(el,str,"value")
        this.bindText(el,key,str)
      }

    case ("bind",other)=> this.reactiveMap.get(att).foreach{str=>
      el.onkeyup = this.makePropHandler(el,str,"value")
      this.bindText(el,key,str)
    }

    case other => dom.console.error(s"unknown binding of ${other._1} to ${other._2}")

  }


  protected def itemPartial(el:HTMLElement,key:String,value:String):PartialFunction[String,Unit] = {
    case "item-bind" => this.bindItemProperty(el, key, value)
    case bname if bname.startsWith("item-bind-") => this.bindAttribute(el, key.replace("item-bind-", ""), value, this.reactiveMap)
  }


}
