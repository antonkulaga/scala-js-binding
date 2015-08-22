package org.denigma.semantic.styles


import scalacss.Defaults._
import scalacss.LengthUnit.em

trait SelectionStyles extends Colors{
  self:StyleSheet.Standalone =>
  import dsl._

  ".fluid" -(
    display.flex,
    flexWrap.wrap,
    alignContent.spaceBetween
    )

  ".flexible" - display.inlineFlex

  ".selected.item" - (
    display.inlineFlex,
    backgroundColor(green),
    borderColor(green),
    color(white)
    )

  ".selector" - (
    display.inlineFlex,
    flexGrow(1),
    border.none
    )

   ".ui.multiple.dropdown" -(
        padding(0.22 em, 0.22 em, 0.22 em, 0.22 em)
    //padding: 0.22620476em 2.6em 0.22620476em 0.28571429em;
  )

  ".ui.fluid.search.dropdown.selection.multiple"-(
    display.flex important
    )

  ".options" -{
    position.relative
  }

}
