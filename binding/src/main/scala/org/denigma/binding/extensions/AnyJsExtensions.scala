package org.denigma.binding.extensions

import scala.scalajs.js
import org.scalajs.dom
import scala.scalajs.js.prim.Undefined

/**
 * Is mixed in to be used in extensions
 */
trait AnyJsExtensions {

  /**
   * Implicit class that adds some useful methods for any ScalaJS object
   * @param obj
   */
  implicit class AnyJs(obj: scalajs.js.Any) {

    //def ===(other:Any): Boolean = if(other==null) obj==null || obj.isInstanceOf[Undefined] || obj=="" else obj==other

    def isNullOrUndef: Boolean = obj == null || obj.isInstanceOf[js.Undefined] || obj == ""

    /**
     * Just a shorter conversion to dynamic object
     * @return self as Dynamic
     */
    def dyn = obj.asInstanceOf[js.Dynamic]

    /**
     * provides dynamic results as options
     * @param key name of the property
     * @return Option[js.Dynamic]
     */
    def \(key: String): Option[js.Dynamic] = dyn.selectDynamic(key) match {
      case null =>
        None

      case v if v.isInstanceOf[Undefined] => None

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

  implicit class AnyObj(obj: scalajs.js.Object) {

    def updateIfExist(key: String, value: String) = if (obj.hasOwnProperty(key) && obj.dyn.selectDynamic(key).toString != value)
      obj.dyn.updateDynamic(key)(value)
  }





}
