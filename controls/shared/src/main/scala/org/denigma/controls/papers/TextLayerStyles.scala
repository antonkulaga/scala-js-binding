package org.denigma.controls.papers

import scalacss.Defaults._


trait TextLayerStyles extends StyleSheet.Standalone {
  import dsl._
  ".textLayer" -(

    position.absolute,
    left(0 px),
    top( 0 px),
    right (0 px),
    bottom (0 px),
    overflow.hidden,
    opacity(0.2),
    lineHeight(1.0),
    transformOrigin := "0% 0%"
    )

  ".textLayer > div" -(
    color.transparent,
    position.absolute,
    whiteSpace.pre,
    cursor.text,
    transformOrigin := "0% 0%"
    )

  ".textLayer .highlight" - (
    margin(-1.0 px),
    padding(1 px),
    backgroundColor.rgb(180, 0, 170),
    borderRadius( 4 px)
    )
  ".textLayer .highlight.begin" -(
    borderRadius(4 px, 0 px , 0 px , 4 px)
    )

  ".textLayer .highlight.end" -(
    borderRadius(0 px, 4 px , 4 px , 0 px)
    )

  ".textLayer .highlight.middle" -(
    borderRadius(0 px)
    )

  ".textLayer .highlight.middle" -(
    borderRadius(0 px)
    )

  ".textLayer .highlight.selected" -(
    backgroundColor(rgb(0, 100, 0))
    )

  ".textLayer ::selection" -(
    backgroundColor(rgb(0, 0, 255))
    )

  ".textLayer ::-moz-selection " -(
    backgroundColor(rgb(0, 0, 255))
    )

  ".textLayer .endOfContent" -(
    display.block,
    position.absolute,
    left(0 px),
    top(100 %%),
    right(0 px),
    bottom(0 px),
    zIndex(-1),
    cursor.default,
    userSelect := "none"

    )

  ".textLayer .endOfContent.active" -(
    top(0 px)
    )
}
