package org.denigma.semantic.controls.general

import org.scalajs.dom.raw.HTMLElement


abstract class EditableMenuView(name:String,el:HTMLElement, params:Map[String,Any] = Map.empty) extends AjaxMenuView(name,el,params)
 {
   self =>

   val editable = params.get("editable").fold(false){
     case b:Boolean=>b
     case _=>false
   }

 }
