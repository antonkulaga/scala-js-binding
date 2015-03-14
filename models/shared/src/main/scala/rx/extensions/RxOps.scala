package rx.extensions

import scala.collection.immutable._
import scala.util.{Failure, Success}
import rx._
import rx.ops._
//NOTE THIS CODE IS NOT TESTED YET
trait RxOps {




  implicit class AnyRx[T,M](source:Rx[T])
  {

    def takeIf(b:Rx[Boolean]) = RxOps(source).filter(el=>b.now)

    def takeIfAll(bools:Rx[Boolean]*) = RxOps(source).filter(el=>bools.forall(b=>b.now))

    def takeIfAny(bools:Rx[Boolean]*) = RxOps(source).filter(el=>bools.exists(b=>b.now))


    def observeIf(b:Rx[Boolean])(callback: => Unit) = Obs(takeIf(b),skipInitial = true)(callback)

    def handler(callback: => Unit) = Obs(source, skipInitial = true)(callback)

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
      def is[R<:T]: rx.Rx[Boolean] = RxOps(source).map(_.isInstanceOf[R])
      def isnt[R<:T]: rx.Rx[Boolean] =RxOps(source).map(!_.isInstanceOf[R])

      def unique() = new UniqueWatcher(source)

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

/*
  implicit class SortedSetWatcher[T](col:Rx[SortedSet[T]])
  {
    var previous = col.now
    val red= Rx{
      val old = previous
      previous =RxOps(col).filter(_!=this.previous)() //TODO: maybe dangerous!
      (old,previous)
    }

    lazy val updates: rx.Rx[CollectionUpdate[T]] = RxOps(red).map{case (prev,cur)=>
      val removed = prev.diff(cur)
      val inserted = cur.diff(prev)
      val same = prev & cur
      val unPre = prev.toList.filterNot(removed.contains).zipWithIndex
      val unCur = cur.toList.filterNot(inserted.contains)
      assert(unPre.size==unCur.size)
      val swapped = unPre.foldLeft(List.empty[Moved[T]]){
        case (acc,(el: T,int: Int))=>
          val i = unCur.indexOf(el)
          if(i!=int) Moved(int,i,el)::acc else acc
      }

      CollectionUpdate[T](removed.toList,inserted.toList,swapped)
    }

    def watcher: Rx[CollectionUpdate[T]] = updates
  }
*/



  /**
   * Watch changes in the collection
   * @param col
   * @tparam T
   */
  implicit class Watcher[T](col:Rx[List[T]])
  {
    //val red: Rx[(List[T], List[T])] = col.reduce((_,_))
    /*
    It would be nice to have a zip or buffer function as reduce does not allow me to map the result to something else
     */

    var previous: List[T] = col.now
    val red: Rx[(List[T], List[T])] = Rx{
      val old = previous
      previous =RxOps(col).filter(_!=this.previous)() //TODO: maybe dangerous!
      (old,previous)
    }

    lazy val updates: rx.Rx[CollectionUpdate[T]] = RxOps(red).map{case (prev,cur)=>
      val removed = prev.diff(cur)
      val inserted = cur.diff(prev)
      val unPre = prev.filterNot(removed.contains).zipWithIndex
      val unCur = cur.filterNot(inserted.contains)
      assert(unPre.size==unCur.size)
      val swapped = unPre.foldLeft(List.empty[Moved[T]]){
        case (acc,(el: T,int: Int))=>
          val i = unCur.indexOf(el)
          if(i!=int) Moved(int,i,el)::acc else acc
      }

      CollectionUpdate[T](removed,inserted,swapped)
    }

    def watcher: Rx[CollectionUpdate[T]] = updates
  }

}
case class Moved[T](from:Int,to:Int,item:T)

/**
 * Contains changes between previous and current collection
 * @param removed list of removed elements
 * @param inserted lifs of instertea elements
 * @param moved list of position changes
 * @tparam T type of the elements
 */
case class CollectionUpdate[T](removed:List[T],inserted:List[T],moved:List[Moved[T]] = List.empty)

case class SortedCollectionUpdate[T](removed:List[T],inserted:List[T],moved:List[(T,T)] = List.empty)