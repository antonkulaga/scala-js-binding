package org.denigma.preview.templates

import org.denigma.semantic.styles.{SelectionStyles, Colors}

import scalacss.Defaults._

object MyStyles extends StyleSheet.Standalone with SelectionStyles {
  import dsl._

  "body"-(
    backgroundColor(mediumseagreen)
    )

  "#promo" -(
    backgroundColor(cornflowerblue)
    )

  "#central_promo"-(
    backgroundColor(blanchedalmond)
    )


  ".CodeMirror" -(
    height.auto important
   // width.auto important
    )
  ".CodeMirror-scroll" -(
    overflow.visible,
    height.auto
    )//-(overflowX.auto,overflowY.hidden)

  "#logo" -(
    maxHeight(30 vw))
}
