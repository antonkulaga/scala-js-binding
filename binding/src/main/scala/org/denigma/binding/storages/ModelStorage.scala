package org.denigma.binding.storages

import org.denigma.binding.models.ModelMessages.SelectQuery
import org.denigma.binding.models.{Channeled, ModelMessages}
import org.denigma.binding.picklers.rp
import org.denigma.binding.extensions.sq
import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.spickling.PicklerRegistry
import org.scalax.semweb.rdf.Res
import org.scalax.semweb.shex.PropertyModel

import scala.concurrent.Future
import scala.scalajs.js


trait ReadOnlyModelStorage extends Channeled{

  def read(shapeId: Res)(modelIds: Res*): Future[List[PropertyModel]]

}

trait ModelStorage extends ReadOnlyModelStorage {

  def create(shapeId: Res)(models: PropertyModel*): Future[Boolean]

  def update(shape: Res, overWrite: Boolean = true)(models: PropertyModel*): Future[Boolean]

  def delete(shape: Res)(res: Res*): Future[Boolean]

}

class AjaxModelQueryStorage(path:String)(implicit registry:PicklerRegistry) extends AjaxModelStorage(path)(registry){

  /**
   * Select query
   * @param query
   * @param shape
   * @return
   */
  def select(query:Res,shape:Res): Future[List[PropertyModel]] = {
    val data: SelectQuery = ModelMessages.SelectQuery(path,shape,query, id = genId(), time = js.Date.now())
    sq.post(path,data):Future[List[PropertyModel]]
  }
}


/**
 * ModelAjax Storage
 * @param path
 */
class AjaxModelStorage(path:String)(implicit registry:PicklerRegistry = rp) extends ModelStorage{

  def genId(): String = js.eval(""" 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {var r = Math.random()*16|0,v=c=='x'?r:r&0x3|0x8;return v.toString(16);}); """).toString

  def channel:String = path

  override def create(shapeId: Res)(models: PropertyModel*): Future[Boolean] = {
    val data = ModelMessages.Create(channel,shapeId,models.toSet, id = genId(), time = js.Date.now())
    sq.post(path,data):Future[Boolean]
  }

  override def update(shapeId: Res, overWrite: Boolean)(models: PropertyModel*): Future[Boolean] = {
    val data = ModelMessages.Create(channel, shapeId,models.toSet, id = genId(), time = js.Date.now())
    sq.post(path,data):Future[Boolean]
  }

  override def delete(shape:Res)(res: Res*): Future[Boolean] = {
    val data = ModelMessages.Delete(channel,shape,res.toSet, id = genId(), time = js.Date.now())
    sq.post(path,data):Future[Boolean]

  }

  override def read(shapeId: Res)(modelIds: Res*): Future[List[PropertyModel]] = {
    val data = ModelMessages.Read(channel,shapeId, modelIds.toSet, id = genId(), time = js.Date.now())
    sq.post(channel,data):Future[List[PropertyModel]]
  }
}

class WebsocketModelStorage(val channel:String) extends ModelStorage {

  override def create(shapeId: Res)(models: PropertyModel*): Future[Boolean] = ???

  override def update(shape: Res, overWrite: Boolean)(models: PropertyModel*): Future[Boolean] = ???

  override def delete(shape: Res)(res: Res*): Future[Boolean] = ???

  override def read(shapeId: Res)(modelIds: Res*): Future[List[PropertyModel]] = ???

}

object WebSocketConnector {

}

class WebSocketConnector(wsUrl:String) {

  def onMessage(event:MessageEvent) = {
    dom.console.log("onmessage")

  }
  def onError(event:ErrorEvent) = {
    dom.console.log("onerror")

  }


  def onOpen(event:Event) = {
    dom.console.log("onopen")
  }

  def onClose(even:CloseEvent) = {

    dom.console.log("onclose")
  }

  var webSocket:WebSocket = null

  def connect(wsUrl:String) = {
    webSocket = new WebSocket(wsUrl)
    webSocket.onopen = onOpen _
    webSocket.onmessage = onMessage _
    webSocket.onerror = onError _
    webSocket.onclose = onClose _


  }

  def send(pickle: js.Any): Unit = {
    webSocket.send(js.JSON.stringify(pickle))
  }


}