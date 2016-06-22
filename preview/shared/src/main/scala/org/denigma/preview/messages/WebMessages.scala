package org.denigma.preview.messages

import boopickle.CompositePickler
import boopickle.DefaultBasic._

import org.denigma.controls.models.WebMessage

object WebMessages {

  object Message {
    implicit val messagePickler: CompositePickler[WebMessages.Message] = compositePickler[WebMessages.Message].
      addConcreteType[WebMessages.Post].
      addConcreteType[WebMessages.ServerErrors].
      addConcreteType[WebMessages.Data].
      addConcreteType[WebMessages.DataMessage].
      addConcreteType[WebMessages.Load].
      addConcreteType[WebMessages.Connected].
      addConcreteType[WebMessages.Disconnected]
  }

  trait Message

  object DataMessage {
    implicit val classPickler: Pickler[DataMessage] = boopickle.Default.generatePickler[DataMessage]
  }

  case class DataMessage(source: WebMessages.Message, data: Array[Byte]) extends Message

  object Post {
    implicit val classPickler: Pickler[Post] = boopickle.Default.generatePickler[Post]
  }

  case class Post(message: String, username: String) extends Message

  object ServerErrors {
    implicit val classPickler: Pickler[ServerErrors] = boopickle.Default.generatePickler[ServerErrors]
  }

  case class ServerErrors(errors: List[String]) extends Message

  object Data {
    implicit val classPickler: Pickler[Data] = boopickle.Default.generatePickler[Data]
  }

  case class Data(message: WebMessage) extends Message

  object Load {
    implicit val classPickler: Pickler[Load] = boopickle.Default.generatePickler[Load]
  }

  case class Load(path: String) extends Message

  case object EmptyMessage extends Message

  object Connected {
    implicit val classPickler: Pickler[Connected] = boopickle.Default.generatePickler[Connected]
  }

  case class Connected(username: String, channel: String, users: List[String]) extends Message

  object Disconnected {
    implicit val classPickler: Pickler[Disconnected] = boopickle.Default.generatePickler[Disconnected]
  }

  case class Disconnected(username: String, channel: String, users: List[String]) extends Message


}
