package org.denigma.preview.templates

import scalacss.Defaults._

object MyStyles extends StyleSheet.Standalone {
  import dsl._

  ".CodeMirror" -(
    height.auto
    )

  ".CodeMirror-scroll" -(
    overflowX.auto,overflowY.hidden
  )

}