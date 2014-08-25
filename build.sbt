name := """binding-preview"""

Build.sameSettings

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += "Typesafe Maven Repository" at "http://repo.typesafe.com/typesafe/maven-releases/"

libraryDependencies ++= Seq(
  //filters, 
  // WebJars pull in client-side web libraries
  "org.webjars" %% "webjars-play" % "2.3.0",
  "org.webjars" % "jquery" % "2.1.1",
  "org.webjars" % "Semantic-UI" % "0.19",
  "org.webjars" % "codemirror" % "4.3",
  "org.webjars" % "ckeditor" % "4.4.1",
  "com.lihaoyi" %% "utest" % "0.2.2",
  "org.scalax" %% "semweb" % Build.semWebVersion,
  "com.assembla.scala-incubator" %% "graph-core" % "1.9.0"
)

// Apply RequireJS optimization, digest calculation and gzip compression to assets
pipelineStages := Seq(digest, gzip)

net.virtualvoid.sbt.graph.Plugin.graphSettings

testFrameworks += new TestFramework("utest.runner.JvmFramework")