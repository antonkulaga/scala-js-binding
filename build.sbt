name := """binding-preview"""

version := "0.1"

Build.sameSettings

libraryDependencies ++= Seq(
  //filters, 
  // WebJars pull in client-side web libraries
  "org.webjars" %% "webjars-play" % "2.2.2",
  "org.webjars" % "jquery" % "2.1.1",
  "org.webjars" % "Semantic-UI" % "0.16.1",
  "org.scalajs" %% "scalajs-pickling-play-json" % "0.2",
  "org.webjars" % "codemirror" % "4.1",
  "org.scalax" %% "semweb" % semWebVersion
)

play.Project.playScalaSettings

net.virtualvoid.sbt.graph.Plugin.graphSettings