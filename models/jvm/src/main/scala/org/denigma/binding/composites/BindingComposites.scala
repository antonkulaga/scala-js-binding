package org.denigma.binding.composites

import org.denigma.binding.messages.{Filters, ExploreMessages, ModelMessages}
import org.denigma.binding.messages.ModelMessages.Suggest
import org.scalax.semweb.composites.{MessagesComposites, ShapePicklers}
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


  implicit lazy val modelsMessages = CompositePickler[ModelMessages.ModelMessage]
    .concreteType[ModelMessages.Read].concreteType[ModelMessages.Create]
    .concreteType[ModelMessages.Suggest].concreteType[ModelMessages.Update]
    .concreteType[ModelMessages.Delete]

  implicit def listPickler[T](implicit pickler: Pickler[T]):Pickler[List[T]] = new Pickler[List[T]] {
    def pickle[P](value: List[T], state: PickleState)(implicit config: PConfig[P]): P = {
      Pickler.resolvingSharingCollection[P](value, value.map(e => Pickle(e, state)), state, config)
    }
  }

  implicit def listUnpickler[T](implicit unpickler: Unpickler[T]): Unpickler[List[T]] =  new Unpickler[List[T]] {
    def unpickle[P](pickle: P, state: collection.mutable.Map[String, Any])(implicit config: PConfig[P]): Try[List[T]] = {
      unpickleSeqish[T, List[T], P](x => x.toList, pickle, state)
    }
  }

  def unpickleSeqish[T, S, P](f: Seq[T] => S, pickle: P, state: collection.mutable.Map[String, Any])
                                     (implicit config: PConfig[P],
                                      u: Unpickler[T]): Try[S] = {

    import config._
    readObjectField(pickle, prefix + "ref").transform(
      (p: P) => {
        readString(p).flatMap(ref => Try(state(ref).asInstanceOf[S]))
      },
      _ => readObjectField(pickle, prefix + "elems").flatMap(p => {
        readArrayLength(p).flatMap(len => {
          val seq = (0 until len).map(index => u.unpickle(readArrayElem(p, index).get, state).get)
          val result = f(seq)
          Unpickler.resolvingSharing(result, pickle, state, config)
          Try(result)
        })
      }
      ))
  }

  implicit lazy val filters = CompositePickler[Filters.Filter]
    .concreteType[Filters.StringFilter].concreteType[Filters.ValueFilter].concreteType[Filters.NumFilter]
    .concreteType[Filters.ContainsFilter]



  implicit lazy val exploreMessages = CompositePickler[ExploreMessages.ExploreMessage]
    .concreteType[ExploreMessages.Explore].concreteType[ExploreMessages.ExploreSuggest].concreteType[ExploreMessages.Exploration]
      .concreteType[ExploreMessages.ExploreSuggestion].concreteType[ExploreMessages.SelectQuery]





}

object BindingComposites extends BindingMessageComposites// with ShapePicklers with MessagesComposites
