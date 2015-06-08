package org.denigma.binding

import play.api.GlobalSettings
import play.api.mvc.{Handler, _}

class TestGlobal(val routes:PartialFunction[(String,String), Handler]) extends GlobalSettings{
  override def onRouteRequest(request: RequestHeader): Option[Handler] = {
    if(routes.isDefinedAt((request.method,request.path)))
      Some(routes(request.method->request.path))
    else
      super.onRouteRequest(request)
  }
}
