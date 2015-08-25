package org.denigma.binding.extensions

import org.scalajs.dom.raw.KeyboardEvent

trait EventsOps {

  object BackspaceKey {
    def unapply(keycode:Int):Option[Int] = if(keycode == 8) Some(keycode) else None

  }
  object TabKey {
    def unapply(keycode:Int):Option[Int] = if(keycode == 9) Some(keycode) else None

  }
  object EnterKey {
    def unapply(keycode:Int):Option[Int] = if(keycode == 13) Some(keycode) else None

  }

  object PauseKey {
    def unapply(keycode:Int):Option[Int] = if(keycode == 19) Some(keycode) else None

  }

  object CapsLockKey {
    def unapply(keycode:Int):Option[Int] = if(keycode == 20) Some(keycode) else None

  }

  object EscapeKey {
    def unapply(keycode:Int):Option[Int] = if(keycode == 27) Some(keycode) else None

  }

  object LeftKey {
    def unapply(keycode:Int):Option[Int] = if(keycode == 37) Some(keycode) else None

  }
  object UpKey {
    def unapply(keycode:Int):Option[Int] = if(keycode == 38) Some(keycode) else None

  }
  object RightKey {
    def unapply(keycode:Int):Option[Int] = if(keycode == 39) Some(keycode) else None

  }
  object DownKey {
    def unapply(keycode:Int):Option[Int] = if(keycode == 40) Some(keycode) else None

  }

  object InsertKey {
    def unapply(keycode:Int):Option[Int] = if(keycode == 45) Some(keycode) else None

  }
  object DeleteKey {
    def unapply(keycode:Int):Option[Int] = if(keycode == 46) Some(keycode) else None

  }
  object HomeKey {
    def unapply(keycode:Int):Option[Int] = if(keycode == 36) Some(keycode) else None

  }
  object EndKey {
    def unapply(keycode:Int):Option[Int] = if(keycode == 35) Some(keycode) else None

  }

  object PageUp {
    def unapply(keycode:Int):Option[Int] = if(keycode == 33) Some(keycode) else None

  }

  object PageDown {
    def unapply(keycode:Int):Option[Int] = if(keycode == 34) Some(keycode) else None

  }

  implicit class KeyboardEventEventExtended(ev:KeyboardEvent)
  {
    def backspaceKey = ev.keyCode==8
    def tabKey	= ev.keyCode==9
    def enterKey	= ev.keyCode == 13
    def pauseKey = ev.keyCode == 19
    def capsLockKey = ev.keyCode ==	20
    def escapeKey	 = ev.keyCode == 27

    def leftKey = ev.keyCode == 37
    def upKey = ev.keyCode == 38
    def rightKey = ev.keyCode == 39
    def downKey = ev.keyCode == 40

    def insertKey = ev.keyCode ==	45
    def deleteKey = ev.keyCode ==	46
    def home = ev.keyCode ==	36
    def end = ev.keyCode ==	35
    def pageUpKey = ev.keyCode ==	33
    def pageDownKey = ev.keyCode ==	34

  }

}
