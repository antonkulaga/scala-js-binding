package rx.ops

import java.util.concurrent.atomic.AtomicReference

import rx.core._

import scala.util.Try

/**
 * Zipper class that
 * @param source source reactive
 * @param transformer transforming function
 * @tparam T incoming type
 * @tparam A outgoing type
 */
class Zipper[T,+A](source: Rx[T])
                          (transformer: (Try[T], Try[T]) => Try[A])
  extends Wrapper[T, A](source, "Zip")
  with Spinlock[A]{

  protected[this] type StateType = SpinState

  /**
   * Previous value
   */
  private lazy val prev= new AtomicReference[Try[T]](source.toTry)

  override def ping[P: Propagator](incoming: Set[Emitter[_]]): Set[Reactor[_]] =
    if(prev.get()==source.toTry) Set() else  super.ping[P](incoming)


  protected[this] val state = SpinSet(makeState)

  def makeState = {
    val pre = prev.get()
    prev.set(source.toTry)
    new SpinState( getStamp, transformer(pre, source.toTry)  )
  }

}

