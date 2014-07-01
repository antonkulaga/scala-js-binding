package org.denigma.binding.controls

import org.denigma.binding.semantic.ModelCollection
import org.scalajs.dom._

import scala.collection.immutable._

abstract class ShapeView(val name:String = "ShapeView", val elem:HTMLElement,val params:Map[String,Any]) extends ModelCollection
{


}
