package org.denigma.preview.templates

import org.denigma.semantic.styles.{Selectors, Colors}

import scalacss.Defaults._

object MyStyles extends StyleSheet.Standalone with Selectors {
  import dsl._

  ".CodeMirror" - height.auto
  ".CodeMirror-scroll" -(overflowX.auto,overflowY.hidden)


}
