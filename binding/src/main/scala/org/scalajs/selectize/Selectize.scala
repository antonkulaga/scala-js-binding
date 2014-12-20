package org.scalajs.selectize

import org.denigma.semantic.binders.SelectOption

import scala.collection.immutable.Map
import scala.scalajs.js
import scala.scalajs.js.{ThisFunction2, ThisFunction1}

object Selectize extends js.Object {

  def define(pluginName:String, fun:ThisFunction1[js.Dynamic,js.Dynamic,Unit]):Unit = ???
}


/**
 * Object to define selectize plugins
 */
object SelectizePlugin {
  var plugins:Map[String,ThisFunction1[js.Dynamic,js.Dynamic,Unit]] = Map.empty

  def define(name:String)(init:ThisFunction1[js.Dynamic,js.Dynamic,Unit]):Unit = if(!plugins.contains(name)){
    Selectize.define(name,init)
  }

  def apply(name:String)(fun:(js.Dynamic,js.Dynamic)=>Unit):Unit = define(name)(fun:ThisFunction1[js.Dynamic,js.Dynamic,Unit])

}

trait Selectize extends js.Object {

  def addOption(option:js.Any):js.Any = ???

  def updateOption(value:js.Any,data:js.Any):js.Any = ???

  def clearOptions():Unit = ???

  def createItem():Any = ???

  def addItem(value:Any):Unit = ???

  def addItems(values:js.Array[Any]) = ??? //add many items

  def removeItem(value:Any):Unit = ???

    //def createItem(value:js.Any):Unit = ???

  def refreshItems():Unit = ???

  def clear():Unit = ???

  //var options:js.Dictionary[js.Dictionary[js.Object]] = ???

  var options:js.Dictionary[js.Object] = ???


  var items:js.Array[String] = ???

  //  var settings_element:js.Any = ???



}