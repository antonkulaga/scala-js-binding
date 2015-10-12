package rx.ops //package is chosen to treack sealed restrictions

import org.denigma.binding.extensions.CommonOps
import rx._

import scala.collection.immutable._
import scala.util.{Failure, Success}
//NOTE THIS CODE NEEDS TESTING
trait RxExt extends CommonOps
{


  implicit class AnyVar[T,M](source:Var[T]) {

    def set(newValue:T) = {
      if(source.now!=newValue) source() = newValue
    }
  }

  implicit class AnyRx[T,M](source:Rx[T])
  {

    def takeIf(b:Rx[Boolean]) = RxOps(source).filter(el=>b.now)

    def takeIfDefined[Value](b:Rx[Option[Value]]) = RxOps(source).filter(el=>b.now.isDefined)

    def takeIfAll(bools:Rx[Boolean]*) = RxOps(source).filter(el=>bools.forall(b=>b.now))

    def takeIfAny(bools:Rx[Boolean]*) = RxOps(source).filter(el=>bools.exists(b=>b.now))

    def observeIf(b:Rx[Boolean])(callback: => Unit): Obs = Obs(takeIf(b),skipInitial = true)(callback)

    def handler(callback: => Unit): Obs = Obs(source, skipInitial = true)(callback)

    def onChange(callback: T=> Unit): Obs = {
      Obs(source, "onChange " + source.name, skipInitial = true){callback(source())}
    }

    def onVar(fun:Var[T]=>Unit) = source match{
      case v:Var[T]=> fun(v)
      case other=> //do nothing
    }

/*    def onVarWith(fun:Var[T]=>Unit)(initialize: =>T) = source match{
      case v:Var[T]=>
        fun(v)
        v.set(initialize)
      case other=> //do nothing
    }*/


    def onChange(name:String,uniqueValue:Boolean = true,skipInitial:Boolean = true)(callback: T=> Unit): Obs =
      if(uniqueValue){
        if(source==null) println(s"SOURCE of $name IS FUCKING NULL!")
        val uni = this.unique()
        uni.onChange(name,uniqueValue = false,skipInitial = skipInitial)(callback)
      }
      else
      {
        if(source==null) println(s"SOURCE of $name IS FUCKING NULL!")
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

    def from(value:TO)(callback: => Unit) = Obs(source,skipInitial = true){    if(source.now._1==value) callback  }
    def to(value:TN)(callback: => Unit) = Obs(source,skipInitial = true){    if(source.now._2==value) callback  }
    def transition(from:TO,to:TN)(callback: => Unit) =  Obs(source,skipInitial = true){    if(source.now == (from->to) ) callback  }

    def isFrom(value:TO) = RxOps(source).map(_._1==value)
    def isTo(value:TN) = RxOps(source).map(_._2==value)
  }



  implicit class SeqWatcher[T](col:Rx[Seq[T]]) extends SequenceWatcher[T,Seq[T]](col)

  /**
   * Watch changes in the collection
   * @param col
   * @tparam T
   */
  class SequenceWatcher[T,Col<:Seq[T]](col:Rx[Col])
  {
    //val red: Rx[(List[T], List[T])] = col.reduce((_,_))
    /*
    It would be nice to have a zip or buffer function as reduce does not allow me to map the result to something else
     */

    var previous: Col = col.now
    val red: Rx[(Col, Col)] = Rx{
      val old = previous
      previous =RxOps(col).filter(_!=this.previous)() //TODO: maybe dangerous!
      (old,previous)
    }

    lazy val removedInserted: Rx[(Seq[T], Seq[T])] = RxOps(red).map{case (prev,cur)=>(prev.diff(cur), cur.diff(prev))}

    lazy val updates: rx.Rx[SequenceUpdate[T]] = RxOps(red).map{case (prev,cur)=>
      val removed: Seq[T] = prev.diff(cur)
      val inserted: Seq[T] = cur.diff(prev)
      val unPre = prev.filterNot(removed.contains).zipWithIndex
      val unCur = cur.filterNot(inserted.contains)
      assert(unPre.size==unCur.size)
      val swapped = unPre.foldLeft(List.empty[Moved[T]]){
        case (acc,(el: T,int: Int))=>
          val i = unCur.indexOf(el)
          if(i!=int) Moved(int,i,el)::acc else acc
      }

      SequenceUpdate[T](removed,inserted,swapped)
    }
  }


  implicit class SortedSetWatcher[T](col:Rx[SortedSet[T]])
  {
    var previous = col.now
    val red: Rx[(SortedSet[T], SortedSet[T])] = Rx{
      val old = previous
      previous =RxOps(col).filter(_!=this.previous)() //TODO: maybe dangerous!
      (old,previous)
    }

    lazy val updates: Rx[SetUpdate[T]] = RxOps(red).map{case (prev,cur)=>
      SetUpdate(prev.removeAddToBecome(cur))
    }

  }

}
/**
 * Contains changes between previous and current collection
 * @param removed list of removed elements
 * @param added lifs of instertea elements
 * @param moved list of position changes
 * @tparam T type of the elements
 */
case class SequenceUpdate[T](removed:Seq[T],added:Seq[T],moved:List[Moved[T]] = List.empty)
case class Moved[T](from:Int,to:Int,item:T)

object SetUpdate{
  def apply[T](minusPlus:(Set[T],Set[T])):SetUpdate[T] = SetUpdate(minusPlus._1,minusPlus._2)
}
case class SetUpdate[T](removed:Set[T],added:Set[T])