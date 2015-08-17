import org.denigma.binding.macroses.Mappable

/*

import org.denigma.binding.macroses._
val v = Helper.test
v.two
v.five*/
def fun[T<:Mappable[T]](value:T)= implicitly[Mappable[T]].toMap(value)