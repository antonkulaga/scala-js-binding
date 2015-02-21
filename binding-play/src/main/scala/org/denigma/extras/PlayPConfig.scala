package org.denigma.extras


import play.api.libs.json
import play.api.libs.json._
import prickle._

import scala.collection.mutable
import scala.util.{Failure, Success, Try}

/**
 * NOTE: not yet working and should be moved to prickle in future*
 * @param prefix
 * @param areSharedObjectsSupported
 */
case class PlayPConfig(prefix: String = "#", areSharedObjectsSupported: Boolean = true)
  extends PConfig[json.JsValue] with PlayBuilder with PlayReader {

  def onUnpickle(id: String, value: Any, state: mutable.Map[String, Any]) = {
    state += (id -> value)
  }

}


trait PlayBuilder  extends PBuilder[json.JsValue] {
  def makeNull(): json.JsValue = json.JsNull
  def makeBoolean(b: Boolean): json.JsValue = json.JsBoolean(b)
  def makeNumber(x: Double): json.JsValue = json.JsNumber(x)

  def makeString(s: String): json.JsValue = json.JsString(s)
  def makeArray(elems: json.JsValue*): json.JsValue = json.JsArray(elems)
  def makeObject(fields: Seq[(String, json.JsValue)]): json.JsValue = json.JsObject(fields)

}
trait PlayReader extends PReader[JsValue] {
  def isNull(x: JsValue): Boolean = x match {
    case JsNull => true
    case _ => false
  }
  def readBoolean(x: JsValue): Try[Boolean] = x match {
    case JsBoolean(true) => Success(true)
    case JsBoolean(false) => Success(false)
    case other => error("boolean", s"$other")
  }
  def readNumber(x: JsValue): Try[Double] = x match {
    case x: JsNumber => Try(x.value.toDouble)
    case other => error("number", s"$other")
  }
  def readString(x: JsValue): Try[String] = x match {
    case s: JsString => Success(s.value)
    case other => error("string", s"$other")
  }
  def readArrayLength(x: JsValue): Try[Int] = x match {
    case x: JsArray => Success(x.value.length)
    case other => error("array length", s"$other")
  }
  def readArrayElem(x: JsValue, index: Int): Try[JsValue] = x match {
    case x: JsArray if index < x.value.length => Success(x.value(index))
    case other => error(s"array($index)", s"$other")
  }
  def readObjectField(x: JsValue, field: String): Try[JsValue] = x match {
    case x: JsObject => Try(x.value(field))
    case other =>  error(s"field \'$field\'", s"$other")
  }

  def error(exp: String, actual: String) = Failure(new RuntimeException(s"Expected: $exp  Actual: $actual"))

}

object PicklePlay{
  
  def intoJson[A](value: A, state: PickleState = PickleState())(implicit p: Pickler[A], config: PConfig[JsValue]) = {
    p.pickle(value, state)(config)
  }

}
