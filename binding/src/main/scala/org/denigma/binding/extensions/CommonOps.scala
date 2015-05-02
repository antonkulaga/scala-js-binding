package org.denigma.binding.extensions

import scala.collection.immutable._
import org.scalajs.dom


trait CommonOps {

  implicit class OptionOpt[T](source:Option[T]){

    def orError(str:String) = if(source.isEmpty) dom.console.error(str)

  }

  implicit class ThrowableOpt(th:Throwable) {
    def stackString = th.getStackTrace.foldLeft("")( (acc,el)=>acc+"\n"+el.toString)
  }

  implicit class StringOpt(str:String) {
    def  isPartOfUrl = str.startsWith("/") || str.startsWith("#") || str.startsWith("?")
  }

  implicit class MapOpt[TValue](source:Map[String,TValue]) {

    def getOrError(key:String) = {
      val g = source.get(key)
      if(g.isEmpty) dom.console.error(s"failed to find item with key $key")
      g
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
