package org.denigma.binding.composites

import org.denigma.binding.messages.ModelMessages.ModelMessage
import org.denigma.binding.messages.{ExploreMessages, Filters, ModelMessages}
import org.denigma.semweb.composites.{MessagesComposites, ShapePicklers}
import prickle._

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

  implicit lazy val suggestFactPickler: Pickler[ModelMessages.SuggestFact] = Pickler.materializePickler[ModelMessages.SuggestFact]
  implicit lazy val suggestFactUnpickler: Unpickler[ModelMessages.SuggestFact] = Unpickler.materializeUnpickler[ModelMessages.SuggestFact]

  implicit lazy val suggestObjectPickler: Pickler[ModelMessages.SuggestObject] = Pickler.materializePickler[ModelMessages.SuggestObject]
  implicit lazy val suggestObjectUnpickler: Unpickler[ModelMessages.SuggestObject] = Unpickler.materializeUnpickler[ModelMessages.SuggestObject]



  implicit lazy val modelsMessages: PicklerPair[ModelMessage] = CompositePickler[ModelMessages.ModelMessage]
    .concreteType[ModelMessages.Read].concreteType[ModelMessages.Create]
    .concreteType[ModelMessages.SuggestFact].concreteType[ModelMessages.SuggestObject].concreteType[ModelMessages.Update]
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
