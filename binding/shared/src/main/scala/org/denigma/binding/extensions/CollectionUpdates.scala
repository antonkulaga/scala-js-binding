package org.denigma.binding.extensions

//package is chosen to treack sealed restrictions



/*
import org.denigma.binding.extensions.CommonOps
import rx._
import rx.opmacros.Operators
import rx.opmacros.Utils.Id

import scala.collection.immutable._
import scala.util.{Failure, Success}
//NOTE THIS CODE NEEDS TESTING
trait RxExt extends CommonOps
{

  implicit class AnyVar[T, M](source: Var[T]) {

    def set(newValue: T): Unit = {
      if(source.now!=newValue) source() = newValue
    }
  }


/**
  * All [[Rx]]s have a set of operations you can perform on them, e.g. `map` or `filter`
  */
  implicit class AnyRx[T](val node: Rx[T])  {


    import scala.language.experimental.macros

    def macroImpls = new GenericOps.Macros(node)
    def map[V](f: Id[T] => Id[V])(implicit ownerCtx: Ctx.Owner): Rx.Dynamic[V] = macro Operators.map[T, V, Id]

    def flatMap[V](f: Id[T] => Id[Rx[V]])(implicit ownerCtx: Ctx.Owner): Rx.Dynamic[V] = macro Operators.flatMap[T, V, Id]

    def filter(f: Id[T] => Boolean)(implicit ownerCtx: Ctx.Owner): Rx.Dynamic[T] = macro Operators.filter[T,T]

    def fold[V](start: Id[V])(f: ((Id[V], Id[T]) => Id[V]))(implicit ownerCtx: Ctx.Owner): Rx.Dynamic[V] = macro Operators.fold[T, V, Id]

    def reduce(f: (Id[T], Id[T]) => Id[T])(implicit ownerCtx: Ctx.Owner): Rx.Dynamic[T] = macro Operators.reduce[T, Id]

    def foreach(f: T => Unit) = node.trigger(f(node.now))

    def takeIf(b: Rx[Boolean]) = node.filRxOps(source).filter(el=>b.now)

    def takeIfDefined[Value](b: Rx[Option[Value]]) = RxOps(source).filter(el => b.now.isDefined)

    def takeIfAll(bools: Rx[Boolean]*) = RxOps(source).filter(el=>bools.forall(b => b.now))

    def takeIfAny(bools: Rx[Boolean]*) = RxOps(source).filter(el=>bools.exists(b => b.now))

    //def observeIf(b: Rx[Boolean])(callback: => Unit): Obs = Obs(takeIf(b),skipInitial = true)(callback)

   /* def handler(callback: => Unit): Obs = Obs(source, skipInitial = true)(callback)

    def onChange(callback: T=> Unit): Obs = {
      new Obs()
      Obs(source, "onChange " + source.name, skipInitial = true){callback(source())}
    }*/

    def onVar(fun: Var[T] => Unit) = source match{
      case v: Var[T] => fun(v)
      case other=> //do nothing
    }

/*    def onVarWith(fun:Var[T]=>Unit)(initialize: =>T) = source match{
      case v:Var[T]=>
        fun(v)
        v.set(initialize)
      case other=> //do nothing
    }*/


    def onChange(name: String, uniqueValue: Boolean = true, skipInitial: Boolean = true)(callback: T => Unit): Obs =
      if(uniqueValue){
        if (source == null) println(s"SOURCE of $name IS FUCKING NULL!")
        val uni = this.unique()
        uni.onChange(name, uniqueValue = false, skipInitial = skipInitial)(callback)
      }
      else
      {
        if (source == null) println(s"SOURCE of $name IS FUCKING NULL!")
        Obs(source, name+"_"+source.name, skipInitial){callback(source())}
      }


    /**
       * Creates a new [[Rx]] which zips the values of the source [[Rx]] according
       * to the given `combiner` function. Failures are passed through directly,
       * and transitioning from a Failure to a Success(s) re-starts the combining
       * using the result `s` of the Success.
       */
      def zip[R](combiner: (T, T) => R): Rx[R] = {
        new Zipper[T,R](source)(
          (x, y) => (x, y) match{
            case (Success(a), Success(b)) => Success(combiner(a, b))
            case (Failure(a), Success(b)) => Failure(a)
            case (Success(_), Failure(b)) => Failure(b)
            case (Failure(_), Failure(b)) => Failure(b)
          }
        )
      }

      /**
       * Just simple zip, without mapping
       */
      def zip(): Rx[(T, T)] = this.zip[(T,T)]((a,b)=>(a,b))

      def is(value:T): rx.Rx[Boolean] = RxOps(source).map(_==value)
      def isnt(value:T): rx.Rx[Boolean] = RxOps(source).map(_!=value)

      def unique():Rx[T] = new UniqueWatcher(source)
  }

  implicit class WrappedRx[TW<:Rx[Rx[T]],T](source:TW)
  {
    def isIn(obj:TW)(value:T) = Rx{ source()==obj && source.now()==value }
    def isNotIn(obj:TW)(value:T) = Rx{ source()!=obj || source.now()!=value }

    //def is(obj:TW)(filter:(T=>Boolean)) = Rx{ source()==obj && filter(source.now()) }
  }

  implicit class ZippedRx[TO,TN](source:Rx[(TO,TN)]) {

    def from(value: TO)(callback: => Unit) = Obs(source,skipInitial = true){    if(source.now._1==value) callback  }
    def to(value: TN)(callback: => Unit) = Obs(source,skipInitial = true){    if(source.now._2==value) callback  }
    def transition(from: TO, to: TN)(callback: => Unit) =  Obs(source,skipInitial = true){    if(source.now == (from->to) ) callback  }

    def isFrom(value: TO) = RxOps(source).map(_._1==value)
    def isTo(value: TN) = RxOps(source).map(_._2==value)
  }


}
/**
 * Contains changes between previous and current collection
  *
  * @param removed list of removed elements
 * @param added lifs of instertea elements
 * @param moved list of position changes
 * @tparam T type of the elements
 */
*/

case class SequenceUpdate[T](removed: Seq[T], added: Seq[T], moved: List[Moved[T]] = List.empty)
case class Moved[T](from:Int,to:Int,item:T)

object SetUpdate{
  def apply[T](minusPlus: (Set[T], Set[T]) ): SetUpdate[T] = SetUpdate(minusPlus._1,minusPlus._2)
}
case class SetUpdate[T](removed: Set[T], added: Set[T])
