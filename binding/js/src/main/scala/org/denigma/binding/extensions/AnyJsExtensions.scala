package org.denigma.binding.extensions

import scala.scalajs.js
/**
 * Is mixed in to be used in extensions
 */
trait AnyJsExtensions {

  /**
   * Implicit class that adds some useful methods for any ScalaJS object
   * @param obj
   */
  implicit class AnyJs(obj: js.Any) {

    def isNullOrUndef: Boolean = obj == null || js.isUndefined(obj)

    /**
     * Just a shorter conversion to dynamic object
     * @return self as Dynamic
     */
    def dyn = obj.asInstanceOf[js.Dynamic]

    def onExists(prop: String)(fun: js.Dynamic => Unit) = obj.asInstanceOf[js.Dynamic].selectDynamic(prop) match {
      case  p if js.isUndefined(p) || p==null=> //do nothing
      case other=> fun(other)
    }

    /**
     * provides dynamic results as options
     * @param key name of the property
     * @return Option[js.Dynamic]
     */
    def \(key: String): Option[js.Dynamic] = dyn.selectDynamic(key) match {

      case value if js.isUndefined(value) | value==null =>    None

      case validValue => Some(validValue)
    }

  }

  /**
   * Useful for complicated traversals, like
   * grandfather \ "mother" \ "daughter"
   * @param opt option with Dynamic object
   */
  implicit class OptionPath(opt: Option[js.Dynamic]) {
    def \(key: String): Option[js.Dynamic] = opt.flatMap(_ \ key)
  }

  /*
  implicit class AnyObj(obj: scalajs.js.Object) {

    def updateIfExist(key: String, value: String) = if (obj.hasOwnProperty(key) && obj.dyn.selectDynamic(key).toString != value)
      obj.dyn.updateDynamic(key)(value)
  }
  */





}
