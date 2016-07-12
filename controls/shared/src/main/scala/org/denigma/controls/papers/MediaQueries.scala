package org.denigma.controls.papers

import scalacss.mutable.StyleSheet

trait MediaQueries extends StyleSheet.Standalone  {


  import dsl._

  def onTiny = media.maxWidth (800 px)
  def onLittle = media.minWidth (801 px).maxWidth (1024 px)
  def onSmall = media.minWidth (1025 px).maxWidth (1280 px)
  def onMedium = media.minWidth (1281 px).maxWidth (1366 px)
  def onLarge = media.minWidth (1367 px)

}