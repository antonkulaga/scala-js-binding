package org.denigma.binding.binders

import org.denigma.binding.views.CollectionView
import org.scalajs.dom.raw.{Element, HTMLElement}

class TemplateBinder[View<:CollectionView](view:View) extends Binder{
  override def bindAttributes(el: Element, attributes: Predef.Map[String, String]): Boolean = {
    if(attributes.contains("data-template"))
    {
      el.removeAttribute("data-template")
      view.template = el
      false
    } else true
  }
}
