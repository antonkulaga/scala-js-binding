package org.denigma.preview.slides

import org.denigma.binding.views.BindableView
import org.scalajs.dom.raw.Element
import rx.core.Var

class StartSlide(val elem:Element) extends BindableView{

  val bindingDepend = Var(
    """
      |resolvers += sbt.Resolver.bintrayRepo("denigma", "denigma-releases") //add resolver
      |libraryDependencies += "org.denigma" %%% "binding" % "0.8.1" // to depend on scala-js-binding library
    """.stripMargin
  )

  val controlsDepend = Var(
  """
    |resolvers += sbt.Resolver.bintrayRepo("denigma", "denigma-releases") //add resolver
    |libraryDependencies += "org.denigma" %%% "binding-controls" % "0.0.9" // to depend on html controls that are based on scala-js-binding lib
  """.stripMargin
  )

  val install = Var(
    """
      |   $ sbt // to open sbt console
      |   $ re-start // will open akka-http application with examples
    """.stripMargin
  )

}
