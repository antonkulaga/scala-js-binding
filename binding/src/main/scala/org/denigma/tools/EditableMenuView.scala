package org.denigma.tools

import org.scalajs.dom.HTMLElement
import scala.collection.immutable.Map
import org.denigma.views.ListView

abstract class EditableMenuView(name:String,el:HTMLElement, params:Map[String,Any] = Map.empty) extends AjaxMenuView(name,el,params)
{
  self =>

  val editable = params.get("editable").fold(false){
    case b:Boolean=>b
    case _=>false
  }

}
