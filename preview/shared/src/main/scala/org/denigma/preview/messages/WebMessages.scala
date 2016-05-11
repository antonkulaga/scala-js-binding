package org.denigma.preview.messages

import boopickle.CompositePickler
import boopickle.Default._
import org.denigma.controls.models.WebMessage

object WebMessages {

  object Message {
    implicit val messagePickler: CompositePickler[WebMessages.Message] = compositePickler[WebMessages.Message].
      addConcreteType[WebMessages.Post].
      addConcreteType[WebMessages.ServerErrors].
      addConcreteType[WebMessages.Data].
      addConcreteType[WebMessages.DataMessage]

  }

  sealed trait Message

  case class DataMessage(data: Array[Byte]) extends Message

  case class Post(message: String, username: String) extends Message

  case class ServerErrors(errors: List[String]) extends Message

  case class Data(message: WebMessage) extends Message

}
