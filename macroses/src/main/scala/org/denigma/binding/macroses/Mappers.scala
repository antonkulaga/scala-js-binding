package org.denigma.binding.macroses

import java.io.File

import scala.language.experimental.macros
import scala.reflect.macros.whitebox
import scala.reflect.macros.whitebox.Context



trait Prefix{

}

/*object Helper{  def test[T] = macro Macros.impl ;}
trait Prefix
object Macros{
  def impl(c: whitebox.Context):c.Expr[Prefix]= {
    import c.universe._
    import scala.io._
    val path = "/home/antonkulaga/denigma/test.txt"
    val list = Source.fromFile(new File(path))
      .getLines().map(_.split(';'))
      .toList.collect{ case arr if arr.length==2=>
        arr.head->arr.tail.head
    }
    val elements: List[c.universe.Tree] = list.map{case (name,body)=>
      val n:TermName = name
      q"""def $n = $body;"""
    }
      c.Expr[Prefix](q"""new Prefix{
         ..$elements
         }
       """)}
}*/

/*

trait Mappable[T] {
  def toMap(t: T): Map[String, Any]
  def fromMap(map: Map[String, Any]): T
}

object Mappable {
  implicit def materializeMappable[T]: Mappable[T] = macro materializeMappableImpl[T]

  def materializeMappableImpl[T: c.WeakTypeTag](c: whitebox.Context): c.Expr[Mappable[T]] = {
    import c.universe._
    val tpe = weakTypeOf[T]
    val companion = tpe.typeSymbol.companion

    val fields = tpe.decls.collectFirst {
      case m: MethodSymbol if m.isPrimaryConstructor ⇒ m
    }.get.paramLists.head

    val (toMapParams: List[c.universe.Tree], fromMapParams: List[c.universe.Tree]) = fields.map { field ⇒
      val name: field.NameType = field.name
      val decoded: c.universe.Name = name.decodedName
      val returnType = tpe.decl(name).typeSignature

      (q"$decoded → t.$name", q"map($decoded).asInstanceOf[$returnType]")
    }.unzip

    c.Expr[Mappable[T]] { q"""
      new Mappable[$tpe] {
        def toMap(t: $tpe): Map[String, Any] = Map(..$toMapParams)
        def fromMap(map: Map[String, Any]): $tpe = $companion(..$fromMapParams)
      }
    """ }
  }
}
*/

