package org.denigma.controls.models

import boopickle.Default._
import boopickle.{CompositePickler, Pickler}

import scala.reflect._

trait WebPicklers {

  implicit val testOptionPickler = generatePickler[TextOption]
  //implicit val textOptionPickler = compositePickler[TextOption]//.addConcreteType[TextOption]
/*
  def add[A<:AnyRef,B](comp: boopickle.CompositePickler[A])(implicit p: Pickler[B], tag: ClassTag[B]) = {
    comp.picklers =  comp.picklers :+ (tag.runtimeClass.getName -> p)
  }*/

  implicit val webSocketPickler = compositePickler[WebMessage]
    .addConcreteType[Suggest]
    .addConcreteType[Suggestion]
    //.addConcreteType[TextOption]
  //add[WebMessage,TextOption](webSocketPickler)(textOptionPickler,classTag[TextOption])
}
