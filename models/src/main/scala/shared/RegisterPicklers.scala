package shared

import org.scalajs.spickling._
import org.scalajs.spickling.PicklerRegistry._
import org.scalax.semweb.rdf._
import org.denigma.binding.models.Menu
import org.denigma.binding.models.MenuItem
import shared.chat._
import org.scalax.semweb.shex.PropertyModel
import org.scalax.semweb.sparql.Pat
import org.scalax.semweb.rdf.IRI
import org.denigma.binding.models.Menu
import shared.chat.User
import org.scalax.semweb.rdf.BlankNode
import shared.chat.JoinedRoom
import org.scalax.semweb.rdf.Trip
import shared.chat.Join
import shared.chat.RoomListChanged
import shared.chat.Connect
import shared.chat.Message
import shared.chat.SendMessage
import shared.chat.RequestPrivateChat
import shared.chat.Room
import shared.chat.ReceiveMessage
import org.scalax.semweb.sparql.Pat
import shared.chat.UserLeft
import org.denigma.binding.models.MenuItem
import shared.chat.UserJoined
import org.scalax.semweb.rdf.Quad

/**
 * Registers picklers
 */
object RegisterPicklers {


  def registerCommon() = {
    import PicklerRegistry.register
    //
    // Utils
    register(Nil)
    register[::[Any]]

  }


  def registerRdf() = {


    //Semantic
    register[IRI]
    register[BlankNode]
    register[StringLiteral]
    register[Pat]
    register[Trip]
    register[Quad]

    register[MenuItem]
    register[Menu]

    //register[Map[IRI,RDFValue]]
    register[PropertyModel]

  }





  def registerChat() = {

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
  }

  this.registerCommon()
  this.registerChat()
  this.registerRdf()



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


  implicit object ModelConsPickler extends  GenericConsPickler[PropertyModel]
  implicit object ModelConsUnpickler extends GenericConsUnpickler[PropertyModel]
  register[::[PropertyModel]]

  implicit object MapPickler extends Pickler[Map[String,String]]{
    override def pickle[P](obj: MapPickler.Picklee)(implicit registry: PicklerRegistry, builder: PBuilder[P]): P = {
      builder.makeObject()
    }
  }


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
