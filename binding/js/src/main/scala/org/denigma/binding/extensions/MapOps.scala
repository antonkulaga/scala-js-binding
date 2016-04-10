package org.denigma.binding.extensions

import rx._
import rx.Ctx.Owner.Unsafe.Unsafe

trait MapOps {
  implicit class MapWatcher[Key, Value](mp: Rx[Map[Key, Value]])
  {

    var previous = mp.now

    val zipped: Rx[(Map[Key, Value], Map[Key, Value])] = Rx.unsafe{
      val old = previous
      previous = mp() //TODO: maybe dangerous!
      (old, previous)
    }

    lazy val updates: Rx[MapUpdate[Key, Value]] = zipped map
      {
        case (past, present) =>
          val prev = past.toSet
          val cur = present.toSet
          val (updated1, removed) = prev.diff(cur).partition{ case (key, value) => present.contains(key)}
          val added = cur.diff(prev).filterNot{case (key, value)=>past.contains(key)}
          val updates = updated1.toMap.map{case (key, value) => key -> (value, present(key)) }
          MapUpdate(added.toMap, removed.toMap, updated = updates)
      }
  }
}

case class MapUpdate[Key, Value](added: Map[Key, Value], removed: Map[Key, Value], updated : Map[Key, (Value,Value)])