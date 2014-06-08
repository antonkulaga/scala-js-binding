package org.denigma.binding.picklers

import org.scalajs.spickling._
import scala.collection.immutable._
/**
 * There are some problems with pickling maps and sets in an ordinary way, that is why we have to make our own pickle registry
 */
trait MapRegistry extends PicklerRegistry {


  override def pickle[P](data: Any)(implicit builder: PBuilder[P], registry: PicklerRegistry = this): P = data match  {
    case obj:Map[_,_]=>
      val array =  obj.map{case (key,value)=> builder.makeObject("key"->registry.pickle(key),"value"->registry.pickle(value))     }
      builder.makeObject("map"->builder.makeArray(array.toSeq:_*)  )

    case obj:Set[_]=>    builder.makeObject("set"->builder.makeArray(obj.map(v=>registry.pickle(v)).toSeq: _*)  )

    //    case seq: Seq[_] =>    builder.makeObject("seq" -> builder.makeArray(     seq.map(v=>registry.pickle(v)): _*))


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
        this.unpickleSeq(pickle).right.getOrElse{
          this.unpickleTuple2(pickle).right.getOrElse{
            PicklerRegistry.unpickle(pickle)(reader,registry)
          }
        }
      }
    }
  }
}