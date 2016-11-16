package org.denigma.preview.templates

import org.denigma.controls.papers.{MediaQueries, TextLayerStyles}
import org.denigma.semantic.styles.{Colors, SelectionStyles}

import scalacss.Defaults._

object MyStyles extends StyleSheet.Standalone with SelectionStyles with TextLayerStyles with MediaQueries{


  import dsl._

    media.maxWidth(1024 px) - {
    &("html") - {
      fontSize(8 pt)
    }
  }
  media.minWidth(1281 px) - {
    &("html") - {
      fontSize(12 pt)
    }
  }

  "html"-(
    onTiny -fontSize(8 pt),
    onLittle -fontSize(9 pt),
    onSmall -fontSize(10 pt),
    onMedium -fontSize(11 pt),
    onLarge -fontSize(12 pt)
    )

  "body"-(
    backgroundColor(skyblue)
    )


  "#paper-row" -(
    maxHeight(75 vh),
    overflowY.auto
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
  ".highlighted" -{
    backgroundColor.gold
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
