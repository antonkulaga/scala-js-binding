package org.denigma.preview.templates

import org.denigma.semantic.styles.{SelectionStyles, Colors}

import scalacss.Defaults._

object MyStyles extends StyleSheet.Standalone with SelectionStyles {
  import dsl._

  "body"-(
    backgroundColor(bindingViolet)
    )

  "#main"-(
    margin(20 px)
    //backgroundColor(bindingGreen)
    //backgroundColor(bindingGreen)
   )

  "#promo" -(
    backgroundColor(bindingBlue)
    )

  "#left_promo"-(
    backgroundColor(bindingBlue)
    )

  "#central_promo"-(
    backgroundColor(blanchedalmond)
    )

  "#right_promo"-(
    backgroundColor(bindingGreen)
    )

  ".ui.items > .item:first-child" -{
    marginTop(20 px) important
  }


  ".CodeMirror" -(
    height.auto important
   // width.auto important
    )
  ".CodeMirror-scroll" -(
    overflow.visible,
    height.auto
    )//-(overflowX.auto,overflowY.hidden)

  "#logo" -(
    maxHeight(25 vw)
    )


  ".ui.menu.main" - {
    backgroundColor(bindingGreen) important
  }

  ".ui.menu.main.active" - {
    backgroundColor(green) important
  }

  ".ui.main.menu .item" - {
    backgroundColor(bindingGreen) important
  }

  "#main" -{
    backgroundColor(bindingGreen)
  }

}
