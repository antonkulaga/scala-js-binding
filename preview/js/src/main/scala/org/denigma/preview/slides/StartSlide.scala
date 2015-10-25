package org.denigma.preview.slides

import org.denigma.binding.views.BindableView
import org.scalajs.dom.raw.Element
import rx.core.Var

class StartSlide(val elem:Element) extends BindableView{

  val install = Var(
    """
      |   $ sbt//sbt console
      |   $ re-start //from sbt console
    """.stripMargin
  )

}
