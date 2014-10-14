package org.denigma.graphs.visual

import org.scalajs.dom.HTMLElement


trait SpriteMaker
{

  def nodeTagFromTitle(title:String,colorName:String): HTMLElement

  def edgeTagFromTitle(title:String,colorName:String): HTMLElement

}