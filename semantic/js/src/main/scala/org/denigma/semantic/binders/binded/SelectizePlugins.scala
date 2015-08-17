package org.denigma.semantic.binders.binded


import org.denigma.selectize._
import org.denigma.binding.extensions._
import org.denigma.semantic.extensions.GraphUpdate
import org.querki.jquery.JQuery
import org.scalajs.dom
import org.scalajs.dom.raw.HTMLElement
import org.w3.banana._
import rx.Rx
import rx.core.Var

import scala.concurrent.Future
import scala.scalajs.js
import scala.scalajs.js._



object SelectPlugin extends BetterCreatePlugin("select_plugin")

/**
 *
 * @param pluginName
 */
class BetterCreatePlugin(pluginName:String) extends BetterDropdownPlugin(pluginName){

  override def pluginHandler(self:js.Dynamic,settings:js.Dynamic):Unit =
  {
    val dropDown:ThisFunction0[js.Dynamic,Unit] = positionDropdown _
    self.positionDropdown = dropDown
    val createItemHandler:js.Function1[Any,Boolean] = this.createItem(self.asInstanceOf[Selectize]) _
    self.createItem = createItemHandler
    //self.onKeyPress = onKeyPress(self) _
  }

  def createItem(self:Selectize)(triggerDropdown:Any)=  {
    val input: String =  if(self.$control_input==null) "" else self.$control_input.`val`().toString
    if(self.canCreate(input)){
      val caret = self.caretPos
      self.lock()
      val data:SelectOption =  if( !js.isUndefined(self.settings.createItem) &&   self.settings.createItem!=null
      ){
        val r = self.settings.createItem(input)
        //dom.console.error("OUTPUT = "+r)
        val fun = self.settings.createItem.asInstanceOf[js.Function1[String,SelectOption]]
        fun(input)
      }
      else SelectOption(input,input)

      self.setTextboxValue("")
      self.addOption(data)
      self.setCaret(caret)
      self.addItem(data.id)
      //dom.console.log("CREATE works: "+data.toString)

      self.refreshOptions(/*triggerDropdown &&*/ self.settings.mode.asInstanceOf[String] != "single")
      self.unlock()
      true
    } else false

  }

}
object BetterDropdownPlugin extends BetterCreatePlugin("dropdown_plugin")


class BetterDropdownPlugin(val pluginName:String)
{

  def pluginHandler(self:js.Dynamic,settings:js.Dynamic):Unit =
  {
    val dropDown:ThisFunction0[js.Dynamic,Unit] = positionDropdown _
    self.positionDropdown = dropDown
  }

  def positionDropdown(self:js.Dynamic):Unit = {
    val control = self.$control.asInstanceOf[JQuery]
    val offset = control.asInstanceOf[js.Dynamic].position()
    if(isUndefined(offset.top)) offset.top = 0
    offset.top = offset.top.asInstanceOf[Double] + control.outerHeight(true)
    self.$dropdown.css(js.Dynamic.literal(
      //width = control.outerWidth(),
      top   = offset.top,
      left  = offset.left
    ))

  }

  def pluginFun:ThisFunction1[js.Dynamic,js.Dynamic,Unit] = pluginHandler _

  def activatePlugin() =     SelectizePlugin(pluginName)(pluginFun)

}
