package org.scalajs.selectize

import org.denigma.semantic.binders.SelectOption
import org.scalajs.jquery.JQuery

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




  var settings:js.Dynamic = ???
  var $input:JQuery = ???
  var tagType:String = ???

  var $wrapper:JQuery = ???
  var $control:JQuery = ???
  var $control_input:JQuery = ???
  var $dropdown:JQuery = ???
  var $dropdown_content:JQuery = ???

  //var $dropdown_parent:JQuery = ???

  var inputMode:Any = ???
  var classes:String = ???


  var isOpen     :Boolean = ???
  var isDisabled :Boolean = ???
  var isInvalid  :Boolean = ???
  var isLocked   :Boolean = ???
  var isFocused  :Boolean = ???
  var isInputHidden:Boolean   = ???
  var isSetup    :Boolean = ???
  var isShiftDown:Boolean = ???
  var isCmdDown  :Boolean = ???
  var isCtrlDown :Boolean = ???
  var ignoreFocus:Boolean = ???
  var ignoreBlur :Boolean = ???
  var ignoreHover:Boolean = ???
  var hasOptions :Boolean = ???
  var caretPos:Int = ???
  var lastValue:String = ???
  var loading:Double = ??? //TODO: check if works
  var $activaOption:JQuery = ???
  var $activeItems:js.Array[JQuery] = ???


  def lock():Unit = ???
  
  def unlock():Unit = ???

  def setupTemplates() = ???


  /**
   * Searches through available options and returns
   * a sorted array of matches.
   *
   * Returns an object containing:
   *
   *   - query {string}
   *   - tokens {array}
   *   - total {int}
   *   - items {array}
   *
   * @param {string} query
   * @returns {object}
   */
  def search(query:String):js.Dynamic = ???

  /**
   * Adds an available option. If it already exists,
   * nothing will happen. Note: this does not refresh
   * the options list dropdown (use `refreshOptions`
   * for that).
   *
   * Usage:
   *
   *   this.addOption(data)
   *
   * @param {object} data
   */
  def addOption(option:js.Any):js.Any = ???

  /**
   * Updates an option available for selection. If
   * it is visible in the selected items or options
   * dropdown, it will be re-rendered automatically.
   *
   * @param {string} value
   * @param {object} data
   */
  def updateOption(value:String,data:Any):Unit = ???

  /**
   * Refreshes the list of available options shown
   * in the autocomplete dropdown menu.
   *
   * @param {boolean} triggerDropdown
   */
  def refreshOptions(triggerDropDown:Boolean) = ???


  /**
   * Clears all options.
   */
  def clearOptions():Unit = ???

  /**
   * Sets the input field of the control to the specified value.
   *
   * @param {string} value
   */
  def setTextboxValue(value:String):Unit = ???


  /**
   * Removes a single option.
   *
   * @param {string} value
   */
  def removeOption(value:String):Unit = ???


  /**
   * Invokes the provided method that provides
   * results to a callback---which are then added
   * as options to the control.
   *
   * @param {function} fn
   */
  def load(fn:js.Function) = ???


  /**
   * Returns the jQuery element of the item
   * matching the given value.
   *
   * @param {string} value
   * @returns {object}
   */
  def getItem(value:String):JQuery = ???

  /**
   * "Selects" an item. Adds it to the list
   * at the current caret position.
   *
   * @param {string} value
   */
  def addItem(value:Any):Unit = ???

  /**
   * "Selects" multiple items at once. Adds them to the list
   * at the current caret position.
   *
   * @param {string} value
   */
  def addItems(values:js.Array[Any]) = ??? //add many items

  /**
   * Removes the selected item matching
   * the provided value.
   *
   * @param {string} value
   */
  def removeItem(value:String):Unit = ???

  /**
   * Invokes the `create` method provided in the
   * selectize options that should provide the data
   * for the new item, given the user input.
   *
   * Once this completes, it will be added
   * to the item list.
   *
   * @return {boolean}
   */
  def createItem(triggerDropdown:Any):Boolean = ???

  /**
   * Re-renders the selected item lists.
   */
  def refreshItems():Unit = ???

  /**
   * Updates all state-dependent attributes
   * and CSS classes.
   */
  def refreshState():Unit = ???

  /**
   * Updates all state-dependent CSS classes.
   */
  def refreshClasses():Unit = ???

  /**
   * Determines whether or not more items can be added
   * to the control without exceeding the user-defined maximum.
   *
   * @returns {boolean}
   */
  def isFull():Boolean = ???


  var options:js.Dictionary[js.Object] = ???


  var items:js.Array[String] = ???

  //  var settings_element:js.Any = ???



  /**
   * Refreshes the original <select> or <input>
   * element to reflect the current state.
   */
  def updateOriginalInput():Unit = ???

  /**
   * Returns the jQuery element of the option
   * matching the given value.
   *
   * @param {string} value
   * @returns {object}
   */
  def getOption(value:String):JQuery = ???

  /**
   * Returns the jQuery element of the next or
   * previous selectable option.
   *
   * @param {object} $option
   * @param {int} direction  can be 1 for next or -1 for previous
   * @return {object}
   */
  def getAdjacentOption(option:JQuery, direction:Int):JQuery = ???

  /**
   * Shows/hide the input placeholder depending
   * on if there items in the list already.
   */
  def updatePlaceholder:Unit = ???
  /**
   * Shows the autocomplete dropdown containing
   * the available options.
   */
  def open:Unit = ???

  /**
   * Closes the autocomplete dropdown menu.
   */
  def close:Unit = ???



  /**
   * Finds the first element with a "data-value" attribute
   * that matches the given value.
   *
   * @param {mixed} value
   * @param {object} $els
   * @return {object}
   */
  def getElementWithValue(value:Any, els:JQuery):js.Dynamic = ???

  /**
   * Calculates and applies the appropriate
   * position of the dropdown.
   */
  def positionDropdown():Unit = ???

  /**
   * Resets / clears all selected items
   * from the control.
   */
  def clear():Unit = ???

  /**
   * A helper method for inserting an element
   * at the current caret position.
   *
   * @param {object} $el
   */
  def insertAtCaret($el:JQuery) = ???

  /**
   * Removes the current selected item(s).
   *
   * @param {object} e (optional)
   * @returns {boolean}
   */
  def deleteSelection(e:Any) = ???

  /**
   * Selects the previous / next item (depending
   * on the `direction` argument).
   *
   * > 0 - right
   * < 0 - left
   *
   * @param {int} direction
   * @param {object} e (optional)
   */
  def advanceSelection(direction:Int, e:js.Dynamic):Unit = ???

  /**
   * Moves the caret left / right.
   *
   * @param {int} direction
   * @param {object} e (optional)
   */
  def advanceCaret(direction:Int,e:js.Any):Unit = ???

  /**
   * Moves the caret to the specified index.
   *
   * @param {int} i
   */
  def setCaret(i:Int):Unit = ???

  /**
   * Disables user input on the control completely.
   * While disabled, it cannot receive focus.
   */
  def disable():Unit = ???

  /**
   * Enables the control so that it can respond
   * to focus and user input.
   */
  def enable():Unit = ???


  /**
   * Completely destroys the control and
   * unbinds all event listeners so that it can
   * be garbage collected.
   */
  def destroy():Unit = ???

  /**
   * A helper method for rendering "item" and
   * "option" templates, given the data.
   *
   * @param {string} templateName
   * @param {object} data
   * @returns {string}
   */
  def render(templateName:String, data:Any):String = ???

  /**
   * Clears the render cache for a template. If
   * no template is given, clears all render
   * caches.
   *
   * @param {string} templateName
   */
  def clearCache(templateName:String):Unit = ???

  /**
   * Determines whether or not to display the
   * create item prompt, given a user input.
   *
   * @param {string} input
   * @return {boolean}
   */
  def canCreate(input:String):Boolean = ???



}