import com.typesafe.sbt.SbtNativePackager.autoImport._
import com.typesafe.sbt.web._
import com.typesafe.sbt.web.pipeline.Pipeline
import org.scalajs.sbtplugin.ScalaJSPlugin
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import playscalajs.{ScalaJSPlay, PlayScalaJS}
import playscalajs.PlayScalaJS.autoImport._
import sbt.Attributed._
import sbt.Keys._
import sbt._
import bintray._
import BintrayPlugin.autoImport._
import spray.revolver.RevolverPlugin._
import play.twirl.sbt._
import play.twirl.sbt.SbtTwirl.autoImport._
import com.typesafe.sbt.web.SbtWeb.autoImport._
import playscalajs.ScalaJSPlay.autoImport._
import scalatex.ScalatexReadme
import spray.revolver.RevolverPlugin._
import com.typesafe.sbt.gzip.Import.gzip

object Build extends PreviewBuild {

	lazy val root = Project("root",file("."),settings = commonSettings)
		.settings(
			mainClass in Compile := (mainClass in previewJVM in Compile).value,
			(managedClasspath in Runtime) += (packageBin in previewJVM in Assets).value
		) dependsOn previewJVM aggregate(previewJVM, previewJS)
}

class PreviewBuild extends LibraryBuild
{

	val scalaJSDevStage  = Def.taskKey[Pipeline.Stage]("Apply fastOptJS on all Scala.js projects")

	def scalaJSDevTaskStage: Def.Initialize[Task[Pipeline.Stage]] = Def.task { mappings: Seq[PathMapping] =>
		mappings ++ PlayScalaJS.devFiles(Compile).value ++ PlayScalaJS.sourcemapScalaFiles(fastOptJS).value
	}

	lazy val preview = crossProject
		.crossType(CrossType.Full)
		.in(file("preview"))
		.settings(commonSettings++publishSettings: _*)
		.settings(
			name := "preview"
		)
		.jsConfigure(p=>p.enablePlugins(ScalaJSPlay))
		.jsSettings(
			persistLauncher in Compile := true,
			persistLauncher in Test := false,
			libraryDependencies ++= Dependencies.previewJS.value,
			jsDependencies += RuntimeDOM % "test"
		)
		.jvmSettings(Revolver.settings:_*)
		.jvmConfigure(p=>p.enablePlugins(SbtTwirl,SbtWeb).enablePlugins(PlayScalaJS)) //despite "Play" in name it is actually sbtweb-related plugin
		.jvmSettings(Revolver.settings:_*)
		.jvmSettings(
			libraryDependencies ++= Dependencies.akka.value ++ Dependencies.webjars.value,
			mainClass in Compile :=Some("org.denigma.preview.Main"),
			mainClass in Revolver.reStart := Some("org.denigma.preview.Main"),
			sourceMapsDirectories :=Seq( (baseDirectory in bindingJS).value , (baseDirectory in semanticJS).value ),
			scalaJSDevStage := scalaJSDevTaskStage.value,
			//pipelineStages := Seq(scalaJSProd,gzip),
			pipelineStages in Assets := Seq(scalaJSDevStage,gzip), //for run configuration
			(managedClasspath in Runtime) += (packageBin in Assets).value //to package production deps
		).dependsOn(semantic)

	lazy val previewJS = preview.js
	lazy val previewJVM = preview.jvm settings( scalaJSProjects := Seq(previewJS) )

}

class LibraryBuild  extends sbt.Build{
	self=>

	protected lazy val bintrayPublishIvyStyle = settingKey[Boolean]("=== !publishMavenStyle") //workaround for sbt-bintray bug

	lazy val publishSettings = Seq(
		bintrayRepository := "denigma-releases",
		bintrayOrganization := Some("denigma"),
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


	//settings for all the projects
	lazy val commonSettings = Seq(
		scalaVersion := Versions.scala,
		organization := "org.denigma",
		scalacOptions ++= Seq( "-feature", "-language:_" ),
		resolvers += sbt.Resolver.bintrayRepo("denigma", "denigma-releases"), //for scala-js-binding
		libraryDependencies ++= Dependencies.testing.value,
		updateOptions := updateOptions.value.withCachedResolution(true) //to speed up dependency resolution
	)

	lazy val jsmacro = Project(
		id = "js-macro",
		base = file("macroses"),
		settings = commonSettings ++ publishSettings
	).settings(
		name := "js-macro",
		version := Versions.jsmacro,
		libraryDependencies ++= Dependencies.macro_js.value,
		libraryDependencies <+= scalaVersion("org.scala-lang" % "scala-reflect" % _)
	).enablePlugins(ScalaJSPlugin)

	lazy val binding = crossProject
		.crossType(CrossType.Full)
		.in(file("binding"))
		.settings(  commonSettings ++ publishSettings:_* )
		.settings(
			version := Versions.binding,
			name := "binding",
			scalaVersion:=Versions.scala
			)
		.jsSettings(
			libraryDependencies ++= Dependencies.bindingJS.value,
			jsDependencies += RuntimeDOM % "test"
		)
		.jvmSettings(  libraryDependencies ++= Dependencies.bindingJVM.value )


	  lazy val bindingJS = binding.js.dependsOn(jsmacro)
	  lazy val bindingJVM   = binding.jvm


	lazy val semantic = crossProject
		.crossType(CrossType.Full)
		.in(file("semantic"))
		.settings(  commonSettings ++ publishSettings:_* )
		.settings(
			version := Versions.binding,
			name := "semantic-binding",
			scalaVersion:=Versions.scala
		)
		.jsSettings(
			libraryDependencies ++= Dependencies.semanticJS.value,
			jsDependencies += RuntimeDOM % "test"
		)
		.jvmSettings(  libraryDependencies ++= Dependencies.semanticJVM.value )
		.dependsOn(binding)


	lazy val semanticJS = semantic.js.dependsOn(jsmacro)
	lazy val semanticJVM   = semantic.jvm


}
