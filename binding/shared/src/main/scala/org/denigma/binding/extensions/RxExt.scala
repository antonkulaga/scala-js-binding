package org.denigma.binding.extensions

import rx._
import rx.Ctx.Owner.Unsafe.Unsafe

import scala.util._
import scala.collection.immutable.{Set, SortedSet, Vector}
import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by antonkulaga on 1/25/16.
  */
trait RxExt extends CommonOps {

  implicit class FutureCombinators[T](val f: Future[T]) {
    def toRx(initial: T)(implicit ec: ExecutionContext, ctx: Ctx.Owner): Rx[T] = {
      @volatile var completed: T = initial
      val ret = Rx.build { (owner, data)  => completed }(ctx)
      f.map { v => completed = v ; ret.recalc() }
      ret
    }
  }

  implicit class AnyVar[T](source: Var[T]) {

    def set(newValue: T): Unit = {
      if (source.now != newValue) source() = newValue
    }

    def futureUpdate(future: Future[T])(valueOnError: Throwable => T)(implicit executor: ExecutionContext): Unit = {
     future.onComplete{
        case Success(result) =>
          source() = result
        case Failure(th) =>
          source.set(valueOnError(th))
      }(executor)
    }

    def extractVar[U](fromSource: T => U)(updateSource: (T, U) => T): Var[U] = { //no can be very dangerous
    val initial = fromSource(source.now)
      val variable = Var(initial)
      source.onChange{
        case s=>
          variable() = fromSource(s)
      }
      variable.onChange{
        case v =>
          source() = updateSource(source.now, v)
      }
      variable
    }

    def push(value: T) = {
      source.Internal.value = value
      source.propagate()
    }
  }

  implicit class AnyRx[T](source: Rx[T]) {

    def zip(implicit ctx: Ctx.Owner) : Rx[(T, T)] = source.fold(source.now, source.now){
      case ( (one, two), el) => (two, el)
    }

    def triggerN(num: Int)(fun: T => Unit): Obs = {
      var counter = num
      def triggerN(obs: =>Obs)(fun: T => Unit): Unit =  {
        counter = counter - 1
        if(counter <=0) {
          fun(source.now)
          obs.kill()
        } else fun(source.now)
      }
      lazy val obs: Obs = source.triggerLater {
        triggerN(obs)(fun)
      }
      obs
    }

    def triggerOnce(fun: T => Unit): Unit = triggerN(1)(fun)

    def triggerIf(value: Rx[Boolean])(fun: T => Unit) = {
      source.triggerLater(if (value.now) fun(source.now) )
    }
    /**
      * Creates a new [[Rx]] which zips the values of the source [[Rx]] according
      * to the given `combiner` function. Failures are passed through directly,
      * and transitioning from a Failure to a Success(s) re-starts the combining
      * using the result `s` of the Success.
      */
    def zip[R](combiner: (T, T) => R)(implicit ctx: Ctx.Owner): Rx[R] = {
        source.zip(ctx).map{ case (one, two)=> combiner(one, two)}
    }


    def onChange(f: T => Unit): Obs = source.triggerLater(f(source.now))

    def onVar(f: Var[T] => Unit) = source match {
      case v: Var[T] => f(v)
      case other => //do nothing
    }

    def takeIf(b: Rx[Boolean])(implicit ctx: Ctx.Owner) = source.filter(el=>b.now)

    def takeIfDefined[Value](b: Rx[Option[Value]])(implicit ctx: Ctx.Owner) = source.filter(el => b.now.isDefined)

    def takeIfAll(bools: Rx[Boolean]*)(implicit ctx: Ctx.Owner) = source.filter(el=>bools.forall(b => b.now))

    def takeIfAny(bools: Rx[Boolean]*)(implicit ctx: Ctx.Owner) = source.filter(el=>bools.exists(b => b.now))

    def observeIf(b: Rx[Boolean])(callback: Boolean=> Unit)(implicit ctx: Ctx.Owner): Obs = takeIf(b).triggerLater(callback(b.now))

  }

  implicit class SeqWatcher[T](col: Rx[Seq[T]]) extends SequenceWatcher[T, Seq[T]](col)

  /**
    * Watch changes in the collection
    *
    * @param col
    * @tparam T
    */
  class SequenceWatcher[T, Col <: Seq[T]](val col: Rx[Col]) {
    //val red: Rx[(List[T], List[T])] = col.reduce((_,_))
    /*
    It would be nice to have a zip or buffer function as reduce does not allow me to map the result to something else
     */

    var previous: Col = col.now

    val zipped: Rx[(Col, Col)] = Rx{
     val old = previous
     val cur = col()
     if (cur != previous)  previous = cur //TODO: maybe dangerous!
     (old, cur)
   }


    lazy val removedInserted: Rx[(Seq[T], Seq[T])] =  zipped map {
        case (prev, cur) => (prev.diff(cur), cur.diff(prev))
      }


    lazy val updates: rx.Rx[SequenceUpdate[T]] = zipped map {
        case (prev, cur) if prev ==cur =>
          SequenceUpdate[T](List.empty, List.empty, List.empty)

        case (prev, cur) =>
          val removed: Seq[T] = prev.diff(cur)
          val added: Seq[T] = cur.diff(prev)
          val remained = prev.filterNot(removed.contains)
          val moved: List[Moved[T]] = remained.foldLeft(List.empty[Moved[T]]) {
            case (acc, el) =>
              val previ = prev.indexOf(el)
              val curi = cur.indexOf(el)
              if (curi != previ) Moved(previ, curi, el) :: acc else acc
          }
          SequenceUpdate[T](removed, added, moved)
      }
  }

  implicit class SortedSetWatcher[T](col: Rx[SortedSet[T]])
  {
    var previous = col.now
    val red: Rx[(SortedSet[T], SortedSet[T])] = Rx.unsafe{
      val old = previous
      previous = col() //TODO: maybe dangerous!
      (old, previous)
    }

    lazy val updates: Rx[SetUpdate[T]] = red map
      {
        case (prev, cur) => SetUpdate(prev.removeAddToBecome(cur))
      }

    def updatesTo[U](vector: Var[Vector[U]])(convert: T => U)(implicit same: (T, U)=> Boolean): Var[Vector[U]] = {
      updates.onChange{
        case u =>
          val remained: Vector[U] = vector.now.filterNot(v=>u.removed.exists(r=>same(r, v)))
          vector() = remained ++ u.added.map(convert)
      }
      vector
    }

    /**
      * Produces a vector that syncs with the set without applying convertions too many times
      *
      * @param convert
      * @param same
      * @tparam U
      * @return
      */
    def toSyncVector[U](convert: T => U)(implicit same: (T, U)=> Boolean): Var[Vector[U]] = updatesTo(Var(col.now.map(convert).toVector))(convert)(same)

  }

  implicit class SetWatcher[T](col: Rx[Set[T]])
  {
    var previous = col.now
    val red: Rx[(Set[T], Set[T])] = Rx.unsafe{
      val old = previous
      previous = col() //TODO: maybe dangerous!
      (old, previous)
    }

    lazy val updates: Rx[SetUpdate[T]] = red map
      {
        case (prev, cur) => SetUpdate(prev.removeAddToBecome(cur))
      }

    def updatesTo[U](vector: Var[Vector[U]])(convert: T => U)(implicit same: (T, U)=> Boolean): Var[Vector[U]] = {
      updates.onChange{
        case u =>
          val remained: Vector[U] = vector.now.filterNot(v=>u.removed.exists(r=>same(r, v)))
          vector() = remained ++ u.added.map(convert)
      }
      vector
    }

    /**
      * Produces a vector that syncs with the set without applying convertions too many times
      *
      * @param convert
      * @param same
      * @tparam U
      * @return
      */
    def toSyncVector[U](convert: T => U)(implicit same: (T, U)=> Boolean): Var[Vector[U]] = updatesTo(Var(col.now.map(convert).toVector))(convert)(same)

  }



  implicit class RxSortedSet[T](source: Rx[SortedSet[T]])  {

    lazy val upd: Rx[SetUpdate[T]] = SortedSetWatcher(source).updates

    def updatesTo[U](vector: Var[Vector[U]])(convert: T => U)(implicit same: (T, U)=> Boolean): Var[Vector[U]] = {
      upd.onChange{
        case u =>
          val remained: Vector[U] = vector.now.filterNot(v=>u.removed.exists(r=>same(r, v)))
          vector() = remained ++ u.added.map(convert)
      }
      vector
    }

    /**
      * Produces a vector that syncs with the set without applying convertions too many times
      *
      * @param convert
      * @param same
      * @tparam U
      * @return
      */
    def toSyncVector[U](convert: T => U)(implicit same: (T, U)=> Boolean): Var[Vector[U]] = updatesTo(Var(source.now.map(convert).toVector))(convert)(same)

  }

}
