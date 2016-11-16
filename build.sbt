import com.typesafe.sbt.gzip.Import.gzip
import com.typesafe.sbt.SbtNativePackager.autoImport._
import com.typesafe.sbt.web.SbtWeb
import com.typesafe.sbt.web.SbtWeb.autoImport._
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import sbt.Keys._
import sbt._


lazy val bintrayPublishIvyStyle = settingKey[Boolean]("=== !publishMavenStyle") //workaround for sbt-bintray bug

lazy val publishSettings = Seq(
    bintrayRepository := "denigma-releases",
    bintrayOrganization := Some("denigma"),
    licenses += ("MPL-2.0", url("http://opensource.org/licenses/MPL-2.0")),
    bintrayPublishIvyStyle := true,
    developers := Developer("antonkulaga", "Anton Kulaga","antonkulaga@gmail.com", new URL("https://github.com/antonkulaga"))::Nil
  )

/**
 * For parts of the project that we will not publish
 */
lazy val noPublishSettings = Seq(
  publish := (),
  publishLocal := (),
  publishArtifact := false
)

//settings for all the projects
lazy val commonSettings = Seq(
  scalaVersion := Versions.scala,
  organization := "org.denigma",
  scalacOptions ++= Seq( "-feature", "-language:_" ),
  resolvers += sbt.Resolver.bintrayRepo("denigma", "denigma-releases"), // for scala-js-binding
  resolvers += Resolver.jcenterRepo,
  libraryDependencies ++= Dependencies.testing.value,
  unmanagedClasspath in Compile ++= (unmanagedResources in Compile).value,
  updateOptions := updateOptions.value.withCachedResolution(true) // to speed up dependency resolution
)

lazy val bindingMacro = crossProject
  .crossType(CrossType.Full)
  .in(file("macroses"))
  .settings(commonSettings ++ publishSettings: _*)
  .settings(
    version := Versions.macroBinding,
    name := "binding-macro",
    scalaVersion:=Versions.scala,
    //crossScalaVersions := Seq(Versions.scala, "2.12.0"),
    libraryDependencies ++= Dependencies.macroses.shared.value,
    resolvers += Resolver.url("scalameta", url("http://dl.bintray.com/scalameta/maven"))(Resolver.ivyStylePatterns),
    libraryDependencies += scalaVersion("org.scala-lang" % "scala-reflect" % _).value,
    libraryDependencies += compilerPlugin("org.scalamacros" % "paradise" % Versions.macroParadise cross CrossVersion.full),
    addCompilerPlugin("org.scalameta" % "paradise" % "3.0.0.132" cross CrossVersion.full),
    scalacOptions += "-Xplugin-require:macroparadise"
  ).disablePlugins(RevolverPlugin)
  .jvmSettings(
    libraryDependencies ++= Dependencies.macroses.jvm.value
  )
  .jsSettings(
    libraryDependencies ++= Dependencies.macroses.js.value,
    jsDependencies += RuntimeDOM % Test
  )

val macroJS = bindingMacro.js
val macroJVM = bindingMacro.jvm


lazy val binding = crossProject
  .crossType(CrossType.Full)
  .in(file("binding"))
  .settings(  commonSettings ++ publishSettings:_* )
  .settings(
    version := Versions.binding,
    name := "binding",
    scalaVersion:=Versions.scala,
    libraryDependencies ++= Dependencies.binding.shared.value
  ).disablePlugins(RevolverPlugin)
  .jsSettings(
    libraryDependencies ++= Dependencies.binding.js.value,
    jsDependencies += RuntimeDOM % "test"
  )
  .jvmSettings(libraryDependencies ++= Dependencies.binding.jvm.value)
  .dependsOn(bindingMacro)


lazy val bindingJS = binding.js
lazy val bindingJVM = binding.jvm

lazy val pdf= (project in file("pdf"))
  .settings(commonSettings ++ publishSettings: _*)
  .settings(
    name := "pdf-js-facade",
    version := Versions.pdfJSFacade,
    scalaVersion := Versions.scala,
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % Versions.dom
    )
  ).enablePlugins(ScalaJSPlugin, ScalaJSWeb)

lazy val controls = crossProject
  .crossType(CrossType.Full)
  .in(file("controls"))
  .settings(commonSettings ++ publishSettings:_*)
  .settings(
    version := Versions.controls,
    name := "binding-controls",
    scalaVersion:=Versions.scala,
    libraryDependencies ++= Dependencies.controls.shared.value
  ).disablePlugins(RevolverPlugin)
  .jsSettings(
    libraryDependencies ++= Dependencies.controls.js.value,
    jsDependencies += RuntimeDOM % "test"
  )
  .jsConfigure(p=>p.dependsOn(pdf).enablePlugins(ScalaJSWeb))
  .jvmSettings( libraryDependencies ++= Dependencies.controls.jvm.value )
  .jvmConfigure(p=>p.enablePlugins(SbtWeb, SbtTwirl))
  .dependsOn(binding)


lazy val controlsJS = controls.js
lazy val controlsJVM = controls.jvm

lazy val semantic = crossProject
  .crossType(CrossType.Full)
  .in(file("semantic"))
  .settings( commonSettings ++ publishSettings:_* )
  .settings(
    version := Versions.controls,
    name := "semantic-controls",
    scalaVersion:=Versions.scala,
    libraryDependencies ++= Dependencies.semantic.shared.value
  ).disablePlugins(RevolverPlugin)
  .jsSettings(
    libraryDependencies ++= Dependencies.semantic.js.value,
    jsDependencies += RuntimeDOM % Test
  )
  .jvmSettings(  libraryDependencies ++= Dependencies.semantic.jvm.value )
  .jvmConfigure(p=>p.enablePlugins(SbtTwirl))
  .dependsOn(controls)


lazy val semanticJS = semantic.js
lazy val semanticJVM   = semantic.jvm

lazy val experimental = crossProject
  .crossType(CrossType.Full)
  .in(file("experimental"))
  .settings(commonSettings: _*)
  .settings(
    name := "binding-experimental",
    libraryDependencies ++= Dependencies.preview.shared.value,
    libraryDependencies += scalaVersion("org.scala-lang" % "scala-reflect" % _).value,
    libraryDependencies += compilerPlugin("org.scalamacros" % "paradise" % Versions.macroParadise cross CrossVersion.full)
  ).disablePlugins(RevolverPlugin)
  .jsSettings(
    persistLauncher in Compile := true,
    persistLauncher in Test := false,
    libraryDependencies ++= Dependencies.preview.js.value,
    jsDependencies += RuntimeDOM % Test
  ).dependsOn(controls)

lazy val experimentalJS = experimental.js
lazy val experimentalJVM = experimental.jvm

lazy val preview = crossProject
		.crossType(CrossType.Full)
		.in(file("preview"))
		.settings(commonSettings ++ publishSettings: _*)
		.settings(
			name := "preview",
      			libraryDependencies ++= Dependencies.preview.shared.value
		).disablePlugins(RevolverPlugin)
		.jsConfigure(p => p.enablePlugins(ScalaJSWeb))
		.jsSettings(
			persistLauncher in Compile := true,
			persistLauncher in Test := false,
			libraryDependencies ++= Dependencies.preview.js.value,
			jsDependencies += RuntimeDOM % Test
		)
		.jvmConfigure(p => p.enablePlugins(SbtTwirl, SbtWeb))
		.jvmSettings(
      TwirlKeys.templateImports += "org.denigma.preview.Mode._",
      compile in Compile := ((compile in Compile) dependsOn scalaJSPipeline.map(f => f(Seq.empty))).value,
      libraryDependencies ++= Dependencies.akka.value ++ Dependencies.webjars.value++ Dependencies.preview.jvm.value,
			mainClass in Compile := Some("org.denigma.preview.Main"),
			pipelineStages in Assets := Seq(scalaJSPipeline),
			(emitSourceMaps in fullOptJS) := true,
      isDevMode in scalaJSPipeline := { sys.env.get("APP_MODE") match {
        case Some(str) if str.toLowerCase.startsWith("prod") =>
	        println("PRODUCTION MODE")
          false
        case other => true//(devCommands in scalaJSPipeline).value.contains(state.value.history.current)
      }
    }).dependsOn(semantic, controls, experimental)

lazy val previewJS = preview.js
lazy val previewJVM = preview.jvm settings(
  version := Versions.binding,
  scalaJSProjects := Seq(previewJS)
  )


lazy val root = Project("root", file("."),settings = commonSettings)
  .settings(
    name := "scala-js-binding-preview",
    version := Versions.binding,
    javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint"),
    mainClass in Compile := (mainClass in previewJVM in Compile).value,
    (fullClasspath in Runtime) += (packageBin in previewJVM in Assets).value,
    maintainer := "Anton Kulaga <antonkulaga@gmail.com>",
    packageSummary := "scala-js-binding",
    packageDescription := """Scala-js-binding preview App"""
     ) dependsOn previewJVM aggregate(previewJVM, previewJS) enablePlugins(JavaServerAppPackaging, SystemdPlugin)
