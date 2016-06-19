package org.denigma.binding.macroses

import java.io.File

import com.github.marklister.collections.io.CSVReader

import scala.io.Source
import scala.language.experimental.macros
import scala.reflect.macros._
import scala.util.{Failure, Success, Try}
object CSV
{
  /**
    * Note: use only literal, otherwise Macro will fail!
    * @param from
    * @return
    */
  def toVectorMap(from: String) = macro CSVImpl.toVectorMap

  def toDataFrame(from: String) = macro CSVImpl.toDataFrame
}


class CSVImpl(val c: whitebox.Context) {


  protected def textFrom(where: String) = Try{
    c.classPath.collect{
      case url if !url.getPath.endsWith(".jar")=>
        new File(url.toURI.resolve(where).getPath)
    }.collectFirst{  case f if f.exists()=>
      Source.fromFile(f).getLines().reduce(_+"\n"+_)
    }.getOrElse{
      val base = new java.io.File( "." ).getCanonicalFile
      Source.fromURI(base.toURI.resolve(where)).getLines().reduce(_+"\n"+_)
    }
  }
    .recover{  case any=> Source.fromFile(where).getLines().reduce(_+"\n"+_)  }
    .recover{  case any=> Source.fromURL(where).getLines().reduce(_+"\n"+_)  }

  protected def params2Quazi(params: Seq[String]) = params.map{case n=>
    import c.universe._
    val term:c.TermName = n
    q"val $term:String"
  }

  protected def pairs2Quazi(params: Seq[(String, String)]): Seq[c.universe.Tree] = params.map { case (key, value) =>
    import c.universe._
    val term: c.TermName = key
    q"val $term:String = $value"
  }

  protected def data2rows(data: Seq[Seq[String]]) = {
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

  def identity(annottees: c.Expr[Any]*): c.Expr[Any] = {
    import c.universe._
    val inputs = annottees.map(_.tree).toList
    val (annottee, expandees) = inputs match {
      case (param: ValDef) :: (rest @ (_ :: _)) => (param, rest)
      case (param: TypeDef) :: (rest @ (_ :: _)) => (param, rest)
      case _ => (EmptyTree, inputs)
    }
    println((annottee, expandees))
    val outputs = expandees
    c.Expr[Any](Block(outputs, Literal(Constant(()))))
  }

  def toDataFrame(from: c.Expr[String]): c.Expr[DataFrame[String]] = {
    import c.universe._
    val Literal(Constant(where: String)) = from.tree
    val result = textFrom(where)

    result match {
      case Success(string) =>
        import c.universe._
        val data = new CSVReader(string).toList.map(_.toSeq)
        val rows = this.data2rows(data)
        val (head: Vector[String], body) = (data.head.toVector,data.tail.toVector)
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
      case Success(string) =>
        val data = new CSVReader(string).toList.map(_.toSeq)
        data2rows(data)

      case Failure(error) =>
        println("writeCSV macro does not work!")
        throw error
    }
  }

}
