package org.denigma.binding.macroses

import java.io.File

import scala.io.Source
import scala.language.experimental.macros
import scala.reflect.macros._
import scala.util.{Failure, Success, Try}
/*
@compileTimeOnly("enable macro paradise to expand macro annotations")
class identity extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro CSVImpl.identity
}*/

trait DataFrame[T] {
  def rows: Vector[Mapped[T]]
  def cols: Map[String, Vector[T]]
  def headers: Vector[String]
}


trait Mapped[T] {
  def toMap: Map[String,T]
}