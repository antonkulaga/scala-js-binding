package org.denigma.semantic.storages

import org.denigma.binding.extensions.sq
import org.denigma.binding.messages.ModelMessages
import org.denigma.binding.messages.Suggestion
import org.denigma.binding.picklers.rp
import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.spickling.PicklerRegistry
import org.scalax.semweb.rdf.{IRI, Res}
import org.scalax.semweb.shex.PropertyModel

import scala.concurrent.Future
import scala.scalajs.js




/**
 * ModelAjax Storage
 * @param path
 */
class AjaxModelStorage(path:String)(implicit registry:PicklerRegistry = rp) extends ModelStorage{

  def channel:String = path


  override def create(shapeId: Res)(models: PropertyModel*): Future[Boolean] = {
    val data = ModelMessages.Create(shapeId,models.toSet, genId(), channel = channel)
    sq.post(path,data):Future[Boolean]
  }

  override def update(shapeId: Res, overWrite: Boolean)(models: PropertyModel*): Future[Boolean] = {
    val data = ModelMessages.Create(shapeId,models.toSet,  genId(), channel = channel)
    sq.post(path,data):Future[Boolean]
  }

  override def delete(shape:Res)(res: Res*): Future[Boolean] = {
    val data = ModelMessages.Delete(shape,res.toSet, genId(),channel = channel)
    sq.post(path,data):Future[Boolean]

  }

  override def read(shapeId: Res)(modelIds: Res*): Future[List[PropertyModel]] = {
    val data = ModelMessages.Read(shapeId, modelIds.toSet, genId(), channel = channel)
    sq.post(channel,data):Future[List[PropertyModel]]
  }

  override def suggest(shape:Res,modelRes:Res,prop:IRI,typed:String): Future[Suggestion] = {
    val data = ModelMessages.Suggest(shape,modelRes,prop:IRI, typed, this.genId(),channel = this.channel)
    sq.post(channel,data):Future[Suggestion]
  }
}

class WebsocketModelStorage(val channel:String) extends ModelStorage {

  override def suggest(shape:Res,modelRes:Res,prop:IRI,typed:String): Future[Suggestion] = ???

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

trait ModelStorage extends ReadOnlyModelStorage {

  def suggest(shape:Res,modelRes:Res,prop:IRI,typed:String): Future[Suggestion]

  def create(shapeId: Res)(models: PropertyModel*): Future[Boolean]

  def update(shape: Res, overWrite: Boolean = true)(models: PropertyModel*): Future[Boolean]

  def delete(shape: Res)(res: Res*): Future[Boolean]

}


trait ReadOnlyModelStorage extends Storage{

  def read(shapeId: Res)(modelIds: Res*): Future[List[PropertyModel]]

}

trait Storage {
  def genId(): String = js.eval(""" 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {var r = Math.random()*16|0,v=c=='x'?r:r&0x3|0x8;return v.toString(16);}); """).toString

  def channel:String

}