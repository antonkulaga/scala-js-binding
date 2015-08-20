package org.denigma.binding.macroses

import com.github.marklister.collections.io.CSVReader

import scala.io.Source
import scala.language.experimental.macros
import scala.reflect.macros._
import scala.util.{Failure, Success, Try}

trait DataFrame[T] {
  def rows:Vector[Mapped[T]]
  def cols:Map[String,Vector[T]]
}


trait Mapped[T] {
  def toMap:Map[String,T]
}

object CSV
{
  /**
   * Note: use only literal, otherwise Macro will fail!
   * @param from
   * @return
   */
    def toVectorMap(from:String) = macro CSVImpl.toVectorMap

    def toDataFrame(from:String) = macro CSVImpl.toDataFrame
}


class CSVImpl(val c:whitebox.Context) {

  protected def textFrom(where:String) = Try(Source.fromURL(getClass.getResource(where)).getLines().reduce(_+"\n"+_))
    .recover{  case any=> Source.fromFile(where).getLines().reduce(_+"\n"+_)  }
    .recover{  case any=> Source.fromURL(where).getLines().reduce(_+"\n"+_)  }

  protected def params2Quazi(params:Seq[String]) = params.map{case n=>
    import c.universe._
    val term:c.TermName = n
    q"val $term:String"
  }

  protected def pairs2Quazi(params:Seq[(String,String)]): Seq[c.universe.Tree] = params.map { case (key, value) =>
    import c.universe._
    val term: c.TermName = key
    q"val $term:String = $value"
  }

  protected def data2rows(data:Seq[Seq[String]]) = {
    import c.universe._
    val (head, body) = (data.head,data.tail)
    val pairs = body.map(head.zip(_))
    val result = pairs.map{
      case p=>
        val values = pairs2Quazi(p)
        val mp = p.toMap
        q"""
              new Mapped[String] {
              ..$values

              lazy val toMap:Map[String,String] = $mp
              }
         """
    }
    c.Expr[Vector[Mapped[String]]](q"""
            Vector(..$result)
        """)
  }

  def toDataFrame(from: c.Expr[String]): c.Expr[DataFrame[String]] = {
    import c.universe._
    val Literal(Constant(where: String)) = from.tree
    val result = textFrom(where)

    result match {
      case Success(string)=>
        import c.universe._
        val data = new CSVReader(string).toList.map(_.toSeq)
        val rows = this.data2rows(data)
        val (head, body) = (data.head.toVector,data.tail.toVector)
        val res: Map[String, Vector[String]] = (for{ i <-head.indices } yield head(i)->body.map(_(i))).toMap
        val named = res.map{ case (key,value)=>
            val term:c.TermName = key
            q"val $term:Vector[String] = $value"
        }

        c.Expr[DataFrame[String]](
        q"""
           new DataFrame[String]{
              lazy val rows = $rows
              lazy val cols = $res
              lazy val headers = $head
              ..$named
           }
         """)

      case Failure(error)=>
        println("writeCSV macro does not work!")
        throw error
    }
  }




    def toVectorMap(from: c.Expr[String]): c.Expr[Vector[Mapped[String]]] = {
    import c.universe._

      val Literal(Constant(where: String)) = from.tree
      val result = textFrom(where)

      result match {
        case Success(string)=>
          import c.universe._
          val data = new CSVReader(string).toList.map(_.toSeq)
          data2rows(data)

          case Failure(error)=>
            println("writeCSV macro does not work!")
            throw error
        }
    }

}



/*

  implicit def materializeCSV[T] = macro writeCSV[T]


  def writeCSV[T: c.WeakTypeTag](c: whitebox.Context): c.Expr[Mapped] = {
    import c.universe._
    val tpe =c.weakTypeOf[T]
    val cte= c.weakTypeOf[fromCSV]

    def extract(anno:Annotation):String =  {
      val ch = anno.tree.children
      if(ch.size>1){
        val args = ch.tail.head.children
        c.eval(c.Expr[String](args.tail.head))
      }  else ""
    }

    val files = for{
      v <- tpe.typeSymbol.asClass.annotations
      if v.tree.tpe =:= cte
    } yield v
  }
*/


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
      qdef $n = $body;"""
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

