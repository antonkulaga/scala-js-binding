package org.denigma.binding.frontend.controls

import org.denigma.binding.extensions.sq
import org.denigma.binding.views.BindableView
import org.denigma.semantic.grids.{DataGrid, ExplorableCollection}
import org.denigma.semantic.models.collections.{WithAjaxStorage, AjaxModelCollection}
import org.denigma.semantic.storages.{AjaxModelStorage, AjaxExploreStorage, ExploreStorage, ModelStorage}
import org.scalajs.dom
import org.scalajs.dom.raw.HTMLElement

class DemoGrid(el:HTMLElement,params:Map[String,Any]) extends DataGrid(el,params) with WithAjaxStorage{
  override def activateMacro(): Unit = { extractors.foreach(_.extractEverything(this))}

  override protected def attachBinders(): Unit = binders = BindableView.defaultBinders(this)


}