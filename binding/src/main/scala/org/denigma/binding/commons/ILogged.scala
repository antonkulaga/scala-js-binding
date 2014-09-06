package org.denigma.binding.commons

import org.scalajs.dom


trait ILogged {

  def error(errorText:String) = dom.console.error(errorText)

  def warning(warning:String) = dom.console.warn(warning)

  def info(message:String) = dom.console.info(message:String)

  def debug(message:String) = dom.console.log(message)

}
