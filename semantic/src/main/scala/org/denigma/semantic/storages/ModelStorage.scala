package org.denigma.semantic.storages

import org.denigma.binding.extensions.sq
import org.denigma.binding.messages.ExploreMessages.Exploration
import org.denigma.binding.messages.{ExploreMessages, ModelMessages, Suggestion}
import org.scalajs.dom
import org.scalajs.dom._
import org.denigma.semweb.rdf.{IRI, Res}
import org.denigma.semweb.shex.PropertyModel
import prickle.{Pickler, PConfig, Pickle, Unpickle}

import scala.concurrent.Future
import scala.scalajs.js

import org.denigma.binding.composites.BindingComposites
import BindingComposites._
import Pickle._
import Pickler._
import prickle._
/**
 * ModelAjax Storage
 * @param path
 */
class AjaxModelStorage(path:String) extends ModelStorage{

  def channel:String = path

  protected def post(data:ModelMessages.ModelMessage) =   sq.tryPost(path,data)
  {  d=>  Pickle.intoString(data)(BindingComposites.modelsMessages.pickler,BindingComposites.config)  }
  {   s=>     Unpickle[Boolean].fromString(s)   }

  protected def postBackModelsList(data:ModelMessages.ModelMessage) = sq.tryPost[ModelMessages.ModelMessage,Seq[PropertyModel]](path,data){
    d=> Pickle.intoString[ModelMessages.ModelMessage](data)(BindingComposites.modelsMessages.pickler,BindingComposites.config)
  }{   s=>   Unpickle[Seq[PropertyModel]].fromString(s) }

  protected def postBackSuggestion(data:ModelMessages.ModelMessage) = sq.tryPost(path,data){
    d=> Pickle.intoString[ModelMessages.ModelMessage](data)(BindingComposites.modelsMessages.pickler,BindingComposites.config)
  }{   s=>   Unpickle[Suggestion].fromString(s) }


  override def create(shapeId: Res)(models: PropertyModel*): Future[Boolean] = {
    val data = ModelMessages.Create(shapeId,models.toSet, genId(), channel = channel)
    this.post(data)
  }

  override def update(shapeId: Res, overWrite: Boolean)(models: PropertyModel*): Future[Boolean] = {
    val data = ModelMessages.Create(shapeId,models.toSet,  genId(), channel = channel)
    this.post(data)
  }

  override def delete(shape:Res)(res: Res*): Future[Boolean] = {
    val data = ModelMessages.Delete(shape,res.toSet, genId(),channel = channel)
    this.post(data)
  }

  override def read(shapeId: Res)(modelIds: Res*): Future[Seq[PropertyModel]] = {
    val data = ModelMessages.Read(shapeId, modelIds.toSet, genId(), channel = channel)
    //sq.post(channel,data):Future[List[PropertyModel]]
    this.postBackModelsList(data)
  }

  override def suggest(shape:Res,modelRes:Res,prop:IRI,typed:String): Future[Suggestion] = {
    val data = ModelMessages.Suggest(shape,modelRes,prop:IRI, typed, this.genId(),channel = this.channel)
    //sq.post(channel,data):Future[Suggestion]
    this.postBackSuggestion(data)
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

  def read(shapeId: Res)(modelIds: Res*): Future[Seq[PropertyModel]]

}

trait Storage {
  def genId(): String = js.eval(""" 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {var r = Math.random()*16|0,v=c=='x'?r:r&0x3|0x8;return v.toString(16);}); """).toString

  def channel:String

}

abstract class ExploreStorage(val channel:String) extends Storage
{
  def explore(explore:ExploreMessages.Explore): Future[Exploration]
  def select(query:Res,shape:Res): Future[Seq[PropertyModel]]
  def suggest(typed:String,prop:IRI,explore:ExploreMessages.Explore):Future[ExploreMessages.ExploreSuggestion]
}
