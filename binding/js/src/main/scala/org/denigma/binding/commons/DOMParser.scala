package org.denigma.binding.commons

import org.scalajs.dom.raw.HTMLDocument

import scala.scalajs.js

@js.native
class  DOMParser extends js.Object {

  def parseFromString(string:String, tp:String):HTMLDocument= js.native

}
