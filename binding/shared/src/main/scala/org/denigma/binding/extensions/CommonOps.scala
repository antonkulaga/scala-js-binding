package org.denigma.binding.extensions

import scala.collection.immutable._
//import org.scalajs.dom


trait CommonOps {

  implicit class ErrorOps(e: Throwable){
    def stackString: String = {
      val trace = e.getStackTrace.toList
      trace.foldLeft("STACK TRACE = ") { case (acc, el) => acc + s"\n ${el.toString}" }
    }
  }

  implicit class StringPath(str:String) {
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
     * @param by set to update
     * @return updated Seq, preserving the order of previous elements
     */
    def updatedBy(by:Set[T]): Seq[T] = ImmutableSeqOps(Seq(source:_*)).updatedBy(by)

  }

  implicit class ImmutableSeqOps[T](source: Seq[T]){

    /**
     * Ordered update
     * @param by set to update
     * @return updated Seq, preserving the order of previous elements
     */
    def updatedBy(by:Set[T]): Seq[T] = {
      val w = source.toSet
      if(w==by) source
      else{
        val (minus:Set[T],plus:Set[T]) = w.removeAddToBecome(by)
        source.filterNot(minus.contains)  ++ plus
      }
    }

  }

  implicit class NumberOps(num:Int){



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
