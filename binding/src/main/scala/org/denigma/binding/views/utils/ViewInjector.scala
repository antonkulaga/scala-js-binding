package org.denigma.binding.views.utils

import org.denigma.binding.views.{OrganizedView, Injector, BasicView}
import org.scalajs.dom.HTMLElement

import scala.util.Try

/**
 * Injects views that are registered with this class
 *  Registration looks like:
 *  ViewInjector.register("menu", (el, params) =>Try(new MenuView(el,params)))
 *  After registration if there will be menu view somwhere it will apply this functions to initiate it
 */
object ViewInjector extends ViewFactory with Injector[OrganizedView]{

  override type ChildView = OrganizedView

}