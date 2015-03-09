package org.denigma.binding.composites

import org.denigma.binding.messages.{Filters, ExploreMessages, ModelMessages}
import org.denigma.binding.messages.ModelMessages.{ModelMessage, Suggest}
import org.scalax.semweb.composites.{MessagesComposites, ShapePicklers}
import org.scalax.semweb.rdf
import org.scalax.semweb.rdf.{RDFValue, Lit}
import prickle._
import scala.reflect.classTag
import scala.util.Try

class BindingMessageComposites extends ShapePicklers with MessagesComposites
{

  implicit lazy val updatePickler = Pickler.materializePickler[ModelMessages.Update]
  implicit lazy val updateUnpickler = Unpickler.materializeUnpickler[ModelMessages.Update]


  implicit lazy val readPickler = Pickler.materializePickler[ModelMessages.Read]
  implicit lazy val readUnpickler = Unpickler.materializeUnpickler[ModelMessages.Read]


  implicit lazy val createPickler = Pickler.materializePickler[ModelMessages.Create]
  implicit lazy val createUnpickler = Unpickler.materializeUnpickler[ModelMessages.Create]

  implicit lazy val deletePickler: Pickler[ModelMessages.Delete] = Pickler.materializePickler[ModelMessages.Delete]
  implicit lazy val deleteUnpickler: Unpickler[ModelMessages.Delete] = Unpickler.materializeUnpickler[ModelMessages.Delete]

  implicit lazy val suggestPickler: Pickler[Suggest] = Pickler.materializePickler[ModelMessages.Suggest]
  implicit lazy val suggestUnpickler: Unpickler[Suggest] = Unpickler.materializeUnpickler[ModelMessages.Suggest]


  implicit lazy val modelsMessages: PicklerPair[ModelMessage] = CompositePickler[ModelMessages.ModelMessage]
    .concreteType[ModelMessages.Read].concreteType[ModelMessages.Create]
    .concreteType[ModelMessages.Suggest].concreteType[ModelMessages.Update]
    .concreteType[ModelMessages.Delete]


  implicit lazy val filters = CompositePickler[Filters.Filter]
    .concreteType[Filters.StringFilter].concreteType[Filters.ValueFilter].concreteType[Filters.NumFilter]
    .concreteType[Filters.ContainsFilter]


  implicit lazy val explorePickler = Pickler.materializePickler[ExploreMessages.Explore]
  implicit lazy val exploreUnpickler = Unpickler.materializeUnpickler[ExploreMessages.Explore]

  implicit val explorationPickler = Pickler.materializePickler[ExploreMessages.Exploration]
  implicit val explorationUnpickler = Unpickler.materializeUnpickler[ExploreMessages.Exploration]

  implicit lazy val exploreMessages = CompositePickler[ExploreMessages.ExploreMessage]
    .concreteType[ExploreMessages.Explore].concreteType[ExploreMessages.ExploreSuggest].concreteType[ExploreMessages.Exploration]
      .concreteType[ExploreMessages.ExploreSuggestion].concreteType[ExploreMessages.SelectQuery]





}

object BindingComposites extends BindingMessageComposites with ShapePicklers with MessagesComposites
