package org.denigma.binding.models

import org.scalajs.spickling._
import org.scalajs.spickling.PicklerRegistry._
import org.scalax.semweb.rdf._
import org.scalax.semweb.shex.PropertyModel
import org.scalax.semweb.rdf.IRI
import org.scalax.semweb.rdf.BlankNode
import org.scalax.semweb.rdf.Trip
import org.scalax.semweb.sparql.Pat
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

  def registerWorkAround{
    register[Property]
    register[PropertyModel]

  }

  this.registerCommon()
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

  implicit object ModelConsPickler extends  GenericConsPickler[Property]
  implicit object ModelConsUnpickler extends GenericConsUnpickler[Property]
  register[::[Property]]



  implicit object TransferModelConsPickler extends  GenericConsPickler[TransferModel]
  implicit object TransferModelConsUnpickler extends GenericConsUnpickler[TransferModel]
  register[::[TransferModel]]



//  implicit object ModelConsPickler extends  GenericConsPickler[PropertyModel]
//  implicit object ModelConsUnpickler extends GenericConsUnpickler[PropertyModel]
//  register[::[PropertyModel]]



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
