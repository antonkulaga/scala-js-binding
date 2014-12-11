import com.typesafe.sbt.digest.Import._
import com.typesafe.sbt.gzip.Import._
import com.typesafe.sbt.web.Import._
import sbt.Keys._
import sbt._
import bintray.Opts
import bintray.Plugin._
import bintray.Keys._
import com.typesafe.sbt.packager.universal.UniversalKeys
import play._
import play.Play._

import scala.scalajs.sbtplugin.ScalaJSPlugin.ScalaJSKeys._
import scala.scalajs.sbtplugin.ScalaJSPlugin._


trait Versions {
  val macwireVersion = "0.7.3"

  val semWebVersion =  "0.6.15"

  val bindingVersion = "0.6.5"
}

object BindingBuild extends sbt.Build with UniversalKeys  with Versions{

  val scalajsOutputDir = Def.settingKey[File]("directory for javascript files output by scalajs")

  override def rootProject = Some(preview)

  lazy val frontEndSettings = scalaJsSettings ++ Seq(
    version := bindingVersion,

    name := "frontend",

    resolvers += Opts.resolver.repo("alexander-myltsev", "maven"),

    scalacOptions ++= Seq( "-feature", "-language:_" ),

    ScalaJSKeys.persistLauncher := true,

    ScalaJSKeys.persistLauncher in Test := false
  )

  lazy val frontend = Project(
    id   = "frontend",

    base = file("frontend"),

    settings = frontEndSettings

  ) dependsOn binding

  lazy val bindingSettings = scalaJsSettings++publishSettings ++ Seq(
    version := bindingVersion,

    name := "binding",

    ScalaJSKeys.persistLauncher := true,

    ScalaJSKeys.persistLauncher in Test := false,

    resolvers  += "Online Play Repository" at  "http://repo.typesafe.com/typesafe/simple/maven-releases/",

    libraryDependencies += "org.scala-lang.modules.scalajs" %%% "scalajs-jquery" % "0.6",

    libraryDependencies += "org.scalajs" %%% "codemirror" % "4.5-0.1",

    libraryDependencies += "com.softwaremill.macwire" %% "macros" % macwireVersion
  )

  lazy val binding = Project(
    id = "binding",
    base = file("binding"),
    settings = bindingSettings
  ) dependsOn (jsmacro, models_js)



  lazy val modelsJsSettings =  scalaJsSettings ++ publishSettings++ Seq(
    name := "models",
    ScalaJSKeys.relativeSourceMaps := true,
    ScalaJSKeys.persistLauncher := true,
    ScalaJSKeys.persistLauncher in Test := false,
    libraryDependencies +=  "org.scalax" %%% "semweb" % semWebVersion,
    libraryDependencies += "org.scalajs" %%% "scalajs-pickling" % "0.3.1",
    libraryDependencies +=  "com.scalarx" %%% "scalarx" % "0.2.6"
  )

  /** `models_js`, a js only meta project. */
  lazy val models_js = Project(
    id = "models_js",
    base = file("models/js"),
    settings = modelsJsSettings
  )
  lazy val modelsJvmSettings =  sameSettings ++ publishSettings ++ Seq(
    libraryDependencies +=  "org.scalax" %% "semweb" % semWebVersion,
    libraryDependencies += "org.scalajs" %% "scalajs-pickling-play-json" % "0.3.1",
    libraryDependencies +=  "com.scalarx" %% "scalarx" % "0.2.6"
  )

  lazy val models_jvm = Project(
    id = "models_jvm",
    base = file("models/jvm"),
    settings = modelsJvmSettings
  )

  lazy val jsMacroSettings = scalaJsSettings++ publishSettings ++ Seq(
    name := "js-macro",

    version := "0.1.6",

    libraryDependencies += "org.scala-lang.modules.scalajs" %%% "scalajs-dom" % "0.6.1",

    libraryDependencies += "com.scalatags" %%% "scalatags" % "0.4.2",

    libraryDependencies +=  "com.scalarx" %%% "scalarx" % "0.2.6",

    libraryDependencies <+= scalaVersion("org.scala-lang" % "scala-reflect" % _)
  )

  lazy val jsmacro = Project(
    id = "js-macro",
    base = file("jsmacro"),
    settings = jsMacroSettings
  )

  lazy val bindingPlaySettings = sameSettings ++ bintraySettings ++ publishSettings ++ Seq(
    name := "binding-play",

    version := "0.6.3",

    libraryDependencies +="com.typesafe.play" %% "play" % "2.3.6",

    libraryDependencies += "org.scalajs" %% "scalajs-pickling-play-json" % "0.3.1",

    libraryDependencies += "com.softwaremill.macwire" %% "macros" % macwireVersion,

    libraryDependencies += "com.softwaremill.macwire" %% "runtime" % macwireVersion
  )

  lazy val bindingPlay = Project(
    id = "binding-play",
    base = file("binding-play"),
    settings = bindingPlaySettings
  ) dependsOn models_jvm
   //aggregate models_jvm

  //lazy val sharedCode= unmanagedSourceDirectories in Compile += baseDirectory.value / "shared" / "src" / "main" / "scala"

  lazy val previewSettings = sameSettings ++ Seq(
      name := """binding-preview""",

      version := bindingVersion,

      libraryDependencies += "org.webjars" %% "webjars-play" % "2.3.0-2",

      libraryDependencies += "org.webjars" % "jquery" % "2.1.1",

      libraryDependencies += "org.webjars" % "Semantic-UI" % "1.1.0",

      //libraryDependencies += "org.webjars" % "Semantic-UI" % "0.19",

      libraryDependencies += "org.webjars" % "codemirror" % "4.8",

      libraryDependencies += "org.webjars" % "ckeditor" % "4.4.1",

      libraryDependencies += "com.lihaoyi" %% "utest" % "0.2.4",

      libraryDependencies += "org.webjars" % "three.js" % "r66",

      libraryDependencies += "org.scalax" %% "semweb" % BindingBuild.semWebVersion,

      // Apply RequireJS optimization, digest calculation and gzip compression to assets
      pipelineStages := Seq(digest, gzip),

      testFrameworks += new TestFramework("utest.runner.JvmFramework"),

      scalajsOutputDir := (classDirectory in Compile).value  / "public" / "javascripts",

      //scalajsOutputDir := (crossTarget in Compile).value / "classes"  / "public" / "javascripts",

      //scalajsOutputDir     := baseDirectory.value / "public" / "javascripts" / "scalajs",

      compile in Compile <<= (compile in Compile) dependsOn (fastOptJS in (frontend, Compile)),

      dist <<= dist dependsOn (fullOptJS in (frontend, Compile)),

      stage <<= stage dependsOn (fullOptJS in (frontend, Compile)),

      //test in Test <<= (test in Test) dependsOn (test in (binding, Test)),

      watchSources <++= (sourceDirectory in (frontend, Compile)).map { path => (path ** "*.scala").get}

    ) ++ (   Seq( fastOptJS, fullOptJS) map { packageJSKey =>
      crossTarget in (frontend, Compile, packageJSKey) := scalajsOutputDir.value
    }
    )

  // Use reflection to rename the 'start' command to 'play-start'
  Option(play.Play.playStartCommand.getClass.getDeclaredField("name")) map { field =>
    field.setAccessible(true)
    field.set(playStartCommand, "play-start")
  }


  // The new 'start' command optimises the JS before calling the Play 'start' renamed 'play-start'
  lazy val preStartCommand = Command.args("start", "<port>") { (state: State, args: Seq[String]) =>
    Project.runTask(fullOptJS in (frontend, Compile), state)
    state.copy(remainingCommands = ("play-start " + args.mkString(" ")) +: state.remainingCommands)
  }


  // JsEngineKeys.engineType := JsEngineKeys.EngineType.Node

  lazy val preview = (project in file("."))
    .enablePlugins(PlayScala)
    .settings(previewSettings: _*)
    .dependsOn(bindingPlay).aggregate(frontend)




  protected val bintrayPublishIvyStyle = settingKey[Boolean]("=== !publishMavenStyle") //workaround for sbt-bintray bug


  lazy val sameSettings = bintraySettings ++Seq(

    organization := "org.denigma",

    version := "0.6.2",

    scalaVersion := "2.11.2",

    resolvers += Opts.resolver.repo("scalax", "scalax-releases"),

    resolvers += Opts.resolver.repo("denigma", "denigma-releases"),

    resolvers += Opts.resolver.repo("alexander-myltsev", "maven"),

    // The Typesafe repository
    resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/",


    resolvers +=  Resolver.url("scala-js-releases",
      url("http://dl.bintray.com/content/scala-js/scala-js-releases"))(
        Resolver.ivyStylePatterns),

    scalacOptions ++= Seq( "-feature", "-language:_" ),

    parallelExecution in Test := false

  )

  lazy val scalaJsSettings = scalaJSSettings ++ sameSettings ++ bintraySettings++
    Seq(
      resolvers +=  Resolver.url("scala-js-releases",
        url("http://dl.bintray.com/content/scala-js/scala-js-releases"))(
          Resolver.ivyStylePatterns),
      resolvers += Opts.resolver.repo("alexander-myltsev", "maven"),
      ScalaJSKeys.relativeSourceMaps := true
    )




  lazy val publishSettings = Seq(
    repository in bintray := "denigma-releases",

    bintrayOrganization in bintray := Some("denigma"),

    licenses += ("MPL-2.0", url("http://opensource.org/licenses/MPL-2.0")),

    bintrayPublishIvyStyle := true
  )

  /**
   * For parts of the project that we will not publish
   */
  lazy val noPublishSettings = Seq(
    publish := (),
    publishLocal := (),
    publishArtifact := false
  )


}
