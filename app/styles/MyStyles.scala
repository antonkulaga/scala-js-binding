package styles

import scalacss.Defaults._

object MyStyles extends StyleSheet.Standalone {
  import dsl._

  "body" - {
    paddingTop(50 px)
  }

}
