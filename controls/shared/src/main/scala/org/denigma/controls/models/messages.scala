package org.denigma.controls.models

import java.util.Date

import boopickle.DefaultBasic._

import boopickle.{CompositePickler, Pickler}

import scala.collection.immutable.Seq

object WebMessage {
  implicit val messagePickler: CompositePickler[WebMessage] = compositePickler[WebMessage].
    addConcreteType[Suggest].
    addConcreteType[Suggestion]
}

trait WebMessage
{
  val channel: String
  val time: Date = new Date()
}

object Suggest {
  implicit val classPickler: Pickler[Suggest] = boopickle.Default.generatePickler[Suggest]
}

case class Suggest(input: String, channel: String) extends WebMessage

object Suggestion {
  implicit val classPickler: Pickler[Suggestion] = boopickle.Default.generatePickler[Suggestion]
}

case class Suggestion(input: String, channel: String, suggestions: Seq[TextOption]) extends WebMessage


import rx.Var

object TextOption{

    implicit val varOrdering: Ordering[rx.Var[TextOption]] = new Ordering[Var[TextOption]] {

     override def compare(x: Var[TextOption], y: Var[TextOption]): Int = selectionOrdering.compare(x.now, y.now)

    }

    implicit val selectionOrdering: Ordering[TextOption] = new Ordering[TextOption]
    {
      override def compare(x: TextOption, y: TextOption): Int = if(x.position < y.position)
        -1 else if(x.position>y.position) 1 else if(x==y) 0 else 1
    }

  implicit val classPickler: Pickler[TextOption] = boopickle.Default.generatePickler[TextOption]
}


case class TextOption(value: String, label: String, position: Int = -1, preselected: Boolean = false)