package org.denigma.binding.picklers
import org.scalajs.spickling.PicklerRegistry._

import org.scalajs.spickling._
import scala.collection.immutable.Nil
import org.scalajs.spickling.PicklerRegistry
/**
 * Picklers for some common classes like list
 */
trait CommonPicklers {

  self:PicklerRegistry=>


  def registerCommon() = {
    //
    // Utils
    register(Nil)
    register[::[Any]]
    //register[(_,_)]

  }


  implicit object ConsPickler extends Pickler[::[_]] {
    def pickle[P](value: ::[_])(implicit registry: PicklerRegistry,
                                builder: PBuilder[P]): P = {
      builder.makeArray(value.map(v=>registry.pickle(v)): _*)
    }
  }

  implicit object ConsUnpickler extends Unpickler[::[_]] {
    def unpickle[P](pickle: P)(implicit registry: PicklerRegistry,
                               reader: PReader[P]): ::[_] = {
      val len = reader.readArrayLength(pickle)
      assert(len > 0)
      ((0 until len).toList map { index =>
        registry.unpickle(reader.readArrayElem(pickle, index))
      }).asInstanceOf[::[Any]]
    }
  }
}
