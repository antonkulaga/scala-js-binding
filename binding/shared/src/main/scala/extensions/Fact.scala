package rx.extensions

import java.util.concurrent.atomic.AtomicReference

import rx.core.{Emitter, Propagator, Rx}

import scala.collection.immutable.Set
import scala.util.{DynamicVariable, Try}


class Fact[T](initValue: => T, val name: String = "") extends Rx[T]{

 lazy val state: AtomicReference[Try[T]] = new AtomicReference(Try(initValue))

  /**
   * Updates the value in this `Var` and propagates the change through to its
   * children and descendents
   */
  def update[P: Propagator](newValue: => T) = if(newValue!=currentValue){
    updateSilent(newValue)
    propagate()
  }

  /**
   * Updates the value in this `Var` *without* propagating the change through
   * to its children and descendents
   */
  def updateSilent(newValue: => T) = {
    state.set(Try(newValue))
  }
  def level = 0

  def toTry = state.get()
  def parents: Set[Emitter[Any]] = Set.empty

  def ping[P: Propagator](incoming: Set[Emitter[_]]) = {
    this.children
  }

}

object Fact{
  def apply[T](initValue: =>T) = new Fact(initValue)
}

