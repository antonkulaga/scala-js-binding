package models

import org.scalajs.spickling._
import org.scalax.semweb.rdf.IRI

/**
 * Registers picklers
 */
object RegisterPicklers {
  import PicklerRegistry.register
//
  // Utils
  register(Nil)
  register[::[Any]]

//
  // Models
  register[User]
  register[Room]
  register[Message]

  // Actions from users
  register[Connect]
  register[Join]
  register(Leave)
  register[SendMessage]

//  // Requests
  register[RequestPrivateChat]
  register(AcceptPrivateChat)
  register(RejectPrivateChat)
  register(UserDoesNotExist)

// Notifications from server
  register[RoomListChanged]
  register[JoinedRoom]
  register[UserJoined]
  register[UserLeft]
  register[ReceiveMessage]


  //Semantic
  register[IRI]
  register[MenuItem]
  register[Menu]



  def registerPicklers(): Unit = ()

  implicit object ConsPickler extends Pickler[::[Any]] {
    def pickle[P](value: ::[Any])(implicit registry: PicklerRegistry,
                                  builder: PBuilder[P]): P = {
      builder.makeArray(value.map(registry.pickle(_)): _*)
    }
  }

  implicit object ConsUnpickler extends Unpickler[::[Any]] {
    def unpickle[P](pickle: P)(implicit registry: PicklerRegistry,
                               reader: PReader[P]): ::[Any] = {
      val len = reader.readArrayLength(pickle)
      assert(len > 0)
      ((0 until len).toList map { index =>
        registry.unpickle(reader.readArrayElem(pickle, index))
      }).asInstanceOf[::[Any]]
    }
  }


  implicit object StrConsPickler extends  GenericConsPickler[String]
  implicit object StrConsUnpickler extends GenericConsUnpickler[String]
  register[::[String]]


  implicit object IriItemPickler extends  GenericConsPickler[IRI]
  implicit object IriItemUnpickler extends GenericConsUnpickler[IRI]
  register[::[IRI]]

  implicit object MenuItemPickler extends  GenericConsPickler[MenuItem]
  implicit object MenuItemUnpickler extends GenericConsUnpickler[MenuItem]
  register[::[MenuItem]]


  implicit object MenuConsPickler extends  GenericConsPickler[Menu]
  implicit object MenuConsUnpickler extends GenericConsUnpickler[Menu]
  register[::[Menu]]

}

class GenericConsPickler[T] extends Pickler[::[T]] {
  def pickle[P](value: ::[T])(implicit registry: PicklerRegistry,
                              builder: PBuilder[P]): P = {
    builder.makeArray(value.map(registry.pickle(_)): _*)
  }
}

class GenericConsUnpickler[T] extends Unpickler[::[T]] {
  def unpickle[P](pickle: P)(implicit registry: PicklerRegistry,
                             reader: PReader[P]): ::[T] = {
    val len = reader.readArrayLength(pickle)
    assert(len > 0)
    ((0 until len).toList map { index =>
      registry.unpickle(reader.readArrayElem(pickle, index))
    }).asInstanceOf[::[T]]
  }
}
