package org.denigma.controls.models

import boopickle.Default._
import boopickle.{CompositePickler, Pickler}

import scala.reflect._

trait WebPicklers {
  implicit val testOptionPickler = generatePickler[TextOption]
  implicit val webSocketPickler = compositePickler[WebMessage]
    .addConcreteType[Suggest]
    .addConcreteType[Suggestion]
}
