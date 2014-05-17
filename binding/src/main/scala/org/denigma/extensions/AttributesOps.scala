package org.denigma.extensions

import org.scalajs.dom.{Attr, NamedNodeMap}
import scala.collection.mutable
import org.scalajs.dom
import scala.scalajs.js

/**
 * Attribues
 */
trait AttributesOps {

  /**
   * Creates and attribute
   * @param tuple
   */
  implicit class AttrFactory(tuple:(String,String))  {
    
    def toAtt: Attr = {
      val at = dom.document.createAttribute(tuple._1)
      at.value = tuple._2
      at
    }
  }




}
