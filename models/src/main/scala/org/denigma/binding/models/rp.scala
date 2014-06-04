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
import scala.collection.immutable._
import org.scalax.semweb.shex.validation.{Valid, JustFailure}

/**
 * Registers picklers
 */
object rp extends BindingPicklers with MapRegistry


/**
 * There are some problems with pickling maps and sets in an ordinary way, that is why we have to make our own pickle registry
 */
trait MapRegistry extends PicklerRegistry {


  override def pickle[P](data: Any)(implicit builder: PBuilder[P], registry: PicklerRegistry = this): P = data match  {
    case obj:Map[_,_]=>
      val array =  obj.map{case (key,value)=> builder.makeObject("key"->registry.pickle(key),"value"->registry.pickle(value))     }
      builder.makeObject("map"->builder.makeArray(array.toSeq:_*)  )

    case obj:Set[_]=>
      builder.makeObject("set"->builder.makeArray(obj.map(v=>registry.pickle(v)).toSeq: _*)  )

    case seq: Seq[_] =>
      builder.makeObject("seq" -> builder.makeArray(     seq.map(v=>registry.pickle(v)): _*))


    case (key,value)=>
      builder.makeObject {
       "tuple2"-> builder.makeObject("_1" -> registry.pickle(key), "_2" -> registry.pickle(value))
      }

    case other=>

      PicklerRegistry.pickle(other)(builder,registry)

  }

  protected def unpickleMap[P](pickle: P)(implicit reader: PReader[P], registry: PicklerRegistry = this):Either[P,Map[_,_]] =  if (reader.isNull(pickle))  null else
  {
    val mp = reader.readObjectField(pickle, "map")
    if (reader.isUndefined(mp)) Left(pickle)
    else {
      val l = reader.readArrayLength(mp)
      Right((0 until l).map { i =>
        val el = reader.readArrayElem(mp, i)
        val key = registry.unpickle(reader.readObjectField(el, "key"))
        val value = registry.unpickle(reader.readObjectField(el, "value"))
        key -> value
      }.toMap)
    }
  }

  protected def unpickleSet[P](pickle: P)(implicit reader: PReader[P], registry: PicklerRegistry = this):Either[P,Set[_]] = {
    val st = reader.readObjectField(pickle, "set")
    if (reader.isUndefined(st)) Left(pickle)
    else {
      val l = reader.readArrayLength(st)
      Right((0 until l).map { i =>  registry.unpickle(reader.readArrayElem(st, i))   }.toSet)
    }
  }

  protected def unpickleSeq[P](pickle: P)(implicit reader: PReader[P], registry: PicklerRegistry = this):Either[P,Seq[_]] = {
      val seqData = reader.readObjectField(pickle, "seq")
     if (reader.isUndefined(seqData)) Left(pickle)
     else Right( (0 until reader.readArrayLength(seqData)).map( i => registry.unpickle(reader.readArrayElem(seqData, i))).toSeq )

  }



  protected  def unpickleTuple2[P](pickle: P)(implicit reader: PReader[P], registry: PicklerRegistry = this):Either[P,(_,_)] ={
      val tp = reader.readObjectField(pickle, "tuple2")
      if(reader.isUndefined(tp)) Left(pickle) else {
        ( registry.unpickle(reader.readObjectField(tp,"_1")) , registry.unpickle(reader.readObjectField(tp,"_2"))  )
        match {
          case (one,two) => Right((one,two))
          case _=>Right(null)
        }
      }

    }


  override def unpickle[P](pickle: P)(implicit reader: PReader[P], registry: PicklerRegistry = this): Any = {
    this.unpickleMap(pickle).right.getOrElse{
      this.unpickleSet(pickle).right.getOrElse{
        this.unpickleSet(pickle).right.getOrElse{
         this.unpickleTuple2(pickle).right.getOrElse{
           PicklerRegistry.unpickle(pickle)(reader,registry)
         }
        }
      }
    }
  }
}

class BindingPicklers{



  def registerCommon() = {
    import PicklerRegistry.register
    //
    // Utils
    register(Nil)
    register[::[Any]]
    //register[(_,_)]

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

    register(Valid)
    register[JustFailure]

    //register[Map[IRI,RDFValue]]
    register[PropertyModel]

  }


  this.registerCommon()
  this.registerRdf()



  def registerPicklers(): Unit = ()




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
