package org.denigma.binding.extensions

import org.scalajs.dom.raw.{KeyboardEvent, HTMLElement}

import scala.scalajs.js

/**
 * Extensions for HTMLElement
 */
trait ElementOps {

  implicit class Element(el:HTMLElement) {
    def updateIfExist(key:String,value:js.Any) = if(el.hasOwnProperty(key) && el.dyn.selectDynamic(key)!=value)
      el.dyn.updateDynamic(key)(value)
  }

}

