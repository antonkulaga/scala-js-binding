package org.denigma.binding.extensions

import rx.Var

import scala.collection.immutable._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.matching.Regex
import scala.util.matching.Regex.Match

trait CommonOps {

  implicit class FutureOps[T](source: Future[T]) {

    def toVarOption(implicit context: ExecutionContext): Var[Option[T]] = {
      val v = Var[Option[T]](None)
      source.onSuccess{ case value => v() = Some(value)}
      v
    }
  }

  implicit class ImmutableMapOps[Key, Value](mp: scala.collection.immutable.Map[Key, Value]) {

    //WARNING: UNLIKE MUTABLE getFuture, does not mutate anyting!
    def getFuture(key: Key)(computeFuture: => Future[Value])(implicit context: ExecutionContext): Future[Value] = mp.get(key) match {
      case Some(value) => Future.successful(value)
      case None => computeFuture
    }

  }


    implicit class MutableMapOps[Key, Value](mp: scala.collection.mutable.Map[Key, Value]) {

    /**
      * Gets some value or runs a future to get it
      *
      * @param key key
      * @param computeFuture
      * @return
      */
    def getFuture(key: Key)(computeFuture: => Future[Value])(implicit context: ExecutionContext) = mp.get(key) match {
      case Some(value) => Future.successful(value)
      case None => updateFuture(key)(computeFuture)
    }

    /**
      * Updates the key with future value
      *
      * @param key
      * @param future
      * @return
      */
    def updateFuture(key: Key)(future: Future[Value])(implicit context: ExecutionContext) = {
        future.onSuccess{ case v => mp.update(key, v)}
        future
    }

  }

  implicit class ErrorOps(e: Throwable){
    def stackString: String = {
      val trace = e.getStackTrace.toList
      trace.foldLeft("STACK TRACE = ") { case (acc, el) => acc + s"\n ${el.toString}" }
    }
  }

  implicit class StringPath(str: String) {
    def  isPartOfUrl = str.startsWith("/") || str.startsWith("#") || str.startsWith("?")

    def /(child:String): String = if(str.endsWith("/") || str.endsWith("#") || str.endsWith("?")) str+child else str+ "/" +child


  }

  implicit class SetOps[T](source: Set[T]){

    //tells the things to delete and to update to become newValue
    //returns (minus,plus)
    def removeAddToBecome(newValue:Set[T]) = (source.diff(newValue),newValue.diff(source))

    def removeAddKeepToBecome(newValue: Set[T]) = {
      val (minus,plus) = (source.diff(newValue),newValue.diff(source))
      (minus,plus,source.diff(minus))
    }

  }

  implicit class SeqOps[T](source: collection.Seq[T]) {
    /**
     * Ordered update
      *
      * @param by set to update
     * @return updated Seq, preserving the order of previous elements
     */
    def updatedBy(by: Set[T]): Seq[T] = ImmutableSeqOps(Seq(source:_*)).updatedBy(by)

  }

  implicit class ImmutableSeqOps[T](source: Seq[T]){

    /**
     * Ordered update
      *
      * @param by set to update
     * @return updated Seq, preserving the order of previous elements
     */
    def updatedBy(by: Set[T]): Seq[T] = {
      val w = source.toSet
      if(w==by) source
      else{
        val (minus:Set[T], plus: Set[T]) = w.removeAddToBecome(by)
        source.filterNot(minus.contains)  ++ plus
      }
    }

  }

  implicit class StringOps(text: String)
  {

    def keyPartition[T](characterize: String=> T): scala.List[(T, String)] = text.foldLeft(List.empty[(T, String)]){
      case (Nil, el) =>
        val est = el +""
        (characterize(est) -> est)::Nil
      case ((key, value)::tail, el) =>
        val join = value + el
        val k = characterize(join)
        val est = el +""
        if(k==key) key->join::tail else (characterize(est)->est)::(key, value)::tail
    }.reverse

    //def lastMatch(regex: String): Option[Match] = lastMatch(text, regex)

    def keyMovePartition[Key](characterize: String=> (Key, Int)) = text.foldLeft(List.empty[(Key, String)]){
      case (Nil, el) =>
        val est = el +""
        (characterize(est)._1 -> est)::Nil

      case ((key, value)::tail, el) =>
        val join = value + el
        val (k, move) = characterize(join)
        val (keep, give) = join.splitAt(join.length - move)
        if(k==key) key->join::tail else {
          (k->give)::(key, keep)::tail
        }
    }.reverse

    def matched(str: String, regex: String): (Int, Int) = {
      regex.r.findAllMatchIn(str).foldLeft((-1, 0)){
        case ( (-1, to), m)=>
          (m.start, Math.max(to, m.end))

        case ((from, to), m)=>
          (from, Math.max(to, m.end))
      }
    }

    def regexPartition(reg: String): scala.List[(Boolean, String)] = keyMovePartition{
      case str =>
        matched(str, reg) match {
          case (from, to) if from >= 0 && to==str.length =>
            val diff = to - from
            true -> diff

          case _ => false -> 1
        }
    }
  }

  implicit class NumberOps(num: Int){

    def toWords =  num match { //TODO rewrite

      case 1=>"one"
      case 2=>"two"
      case 3=>"three"
      case 4=>"four"
      case 5=>"five"
      case 6=>"six"
      case 7=>"seven"
      case 8=>"eight"
      case 9=>"nine"
      case 10=>"ten"
      case 11=>"eleven"
      case 12=>"twelve"
      case 13=>"thirteen"
      case 14=>"fourteen"
      case 15=>"fifteen"
      case 16=>"sixteen"
      case 17=>"seventeen"
      case 18=>"eighteen"
      case 19=>"nineteen"
      case 20=>"twenty"
      case 30=>"thirty"
      case 40=>"forty"
      case 50=>"fifty"
      case 60=>"sixty"
      case 70=>"seventy"
      case 80=>"eighty"
      case 90=>"ninety"
      case 100=>"hundred"
      case 1000=>"thousand"
    }

  }

}
