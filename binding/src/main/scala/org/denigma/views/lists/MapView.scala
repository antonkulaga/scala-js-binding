package org.denigma.views.lists

import org.scalajs.dom._
import org.scalajs.dom
import rx.Var
import dom.extensions._
import org.denigma.binding.EventBinding
import scala.collection.immutable._
import scala.Some
import org.denigma.views.core.OrdinaryView


/**
 * View that binds to map, usually is used as items of ListViews
 * @param name name of the view
 * @param element htmlelement to bind to
 * @param props properties to bind to
 */
abstract class MapView(name:String,element:HTMLElement,props:Map[String,Any]) extends OrdinaryView(name,element) {
  val reactiveMap: Map[String, Var[String]] = props.map(kv => (kv._1, Var(kv._2.toString)))

  //TODO: rewrite props
  override def bindProperties(el: HTMLElement, ats: Map[String, String]) = for {
    (key, value) <- ats
  } {
    this.visibilityPartial(el, value)
      .orElse(this.classPartial(el, value))
      .orElse(this.itemPartial(el, key.toString, value))
      .orElse(this.propertyPartial(el, key.toString, value))
      .orElse(this.loadIntoPartial(el, value))
      .orElse(this.otherPartial)(key.toString)
  }

  protected def itemPartial(el:HTMLElement,key:String,value:String):PartialFunction[String,Unit] = {
    case "item-bind" => this.bindItemProperty(el, key, value)
    case bname if bname.startsWith("item-bind-") => this.bindAttribute(el, key.replace("item-bind-", ""), value, this.reactiveMap)
  }


  override def bindDataAttributes(el:HTMLElement,ats:Map[String,String]) ={
    super.bindDataAttributes(el,ats)

  }

  /**
   * Binds property value to attribute
   * @param el Element
   * @param key name of the binding key
   * @param att binding attribute
   */
  def bindItemProperty(el:HTMLElement,key:String,att:String) = (key.toString.replace("item-",""),el.tagName.toLowerCase().toString) match {
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

    case _=> dom.console.error(s"unknown binding")

  }

}
