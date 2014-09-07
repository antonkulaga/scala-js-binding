package org.scalajs.selectize

import scala.scalajs.js


trait Selectize extends js.Object {

  def addOption(option:js.Any):js.Any = ???

  def updateOption(value:js.Any,data:js.Any):js.Any = ???

  def clearOptions():Unit = ???

  def addItem(value:Any):Unit = ???

  def removeItem(value:Any):Unit = ???

  //def createItem(value:js.Any):Unit = ???

  def refreshItems():Unit = ???

  def clear():Unit = ???

  //var options:js.Dictionary[js.Dictionary[js.Object]] = ???

  var options:js.Dictionary[js.Object] = ???


  var items:js.Array[String] = ???

  //  var settings_element:js.Any = ???



}