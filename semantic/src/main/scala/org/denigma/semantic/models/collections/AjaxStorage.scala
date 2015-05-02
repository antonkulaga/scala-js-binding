package org.denigma.semantic.models.collections

import org.denigma.binding.extensions.sq
import org.denigma.semantic.storages.{AjaxModelStorage, ModelStorage, AjaxExploreStorage, ExploreStorage}


trait WithAjaxStorage extends WithPath {
  self:AjaxModelCollection=>

  lazy val exploreStorage: ExploreStorage = new AjaxExploreStorage(path)
  lazy val crudStorage: ModelStorage = new AjaxModelStorage(crud)
}


trait WithPath {
  self:AjaxModelCollection=>

  lazy val path:String = self.resolveKey("path"){
    case v=>
      if(v.toString.contains(":")) v.toString else sq.withHost(v.toString)
  }
  lazy val crud:String = self.resolveKey("crud"){
    case v=>if(v.toString.contains(":")) v.toString else sq.withHost(v.toString)
  }

}