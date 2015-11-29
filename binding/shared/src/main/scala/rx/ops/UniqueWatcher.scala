package rx.ops

import java.util.concurrent.atomic.AtomicReference

import rx.core._

import scala.util.Try


class UniqueWatcher[T](source: Rx[T])    extends Wrapper[T, T](source, "Unique") with Spinlock[T]{

  protected[this] type StateType = SpinState
  

  private lazy val prev = new AtomicReference[Try[T]](source.toTry)

  override def ping[P: Propagator](incoming: Set[Emitter[_]]): Set[Reactor[_]] =
    if (prev.get() == source.toTry) Set() else  super.ping[P](incoming)

  protected[this] val state = SpinSet(makeState)

  
  def makeState = {
    val pre = prev.get()
    prev.set(source.toTry)
    new SpinState( getStamp, source.toTry  )
  }
  
}