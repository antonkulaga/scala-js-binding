package org.denigma.binding.views

import org.denigma.binding.binders.BasicBinding


trait BindableView extends OrganizedView
{

  type Binder = BasicBinding

  var binders:List[Binder]

  protected def attachBinders():Unit

}
