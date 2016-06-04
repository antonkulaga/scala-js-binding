package org.denigma.binding.extensions

import org.scalajs.dom
import org.scalajs.dom.ext.KeyCode
import org.scalajs.dom.raw.{HTMLElement, KeyboardEvent}

import scala.scalajs.js

trait EventsOps {

  implicit class KeyboardEventEventExtended(ev: KeyboardEvent)
  {
    def backspaceKey = ev.keyCode==KeyCode.Backspace
    def tabKey	= ev.keyCode==KeyCode.Tab
    def enterKey	= ev.keyCode == KeyCode.Enter
    def pauseKey = ev.keyCode == KeyCode.Pause
    def capsLockKey = ev.keyCode ==	KeyCode.CapsLock
    def escapeKey	 = ev.keyCode == KeyCode.Escape

    def leftKey = ev.keyCode == KeyCode.Left
    def upKey = ev.keyCode == KeyCode.Up
    def rightKey = ev.keyCode == KeyCode.Right
    def downKey = ev.keyCode == KeyCode.Down

    def insertKey = ev.keyCode ==	KeyCode.Insert
    def deleteKey = ev.keyCode ==	KeyCode.Delete
    def home = ev.keyCode ==	KeyCode.Home
    def end = ev.keyCode ==	KeyCode.End
    def pageUpKey = ev.keyCode ==	KeyCode.PageUp
    def pageDownKey = ev.keyCode ==	KeyCode.PageDown
  }

}
