import bintray.Opts
import sbt.Keys._
import sbt._
import org.scalajs.sbtplugin.ScalaJSPlugin
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import sbt.Project.projectToRef

object Dependencies
{
  val macwireVersion = "0.8.0"

  val scaleniumVersion = "1.0.1"

  val scalaRxVersion = "0.2.8"

  val scalaTagsVersion = "0.4.6"

  val semanticUIVersion = "1.11.2"

  val codeMirrorVersion = "4.11"

  val selectizeVersion = "0.12.0"

  val sesameVersion = "2.7.12"

  lazy val codeMirrorFacade = "4.8-0.4"


  val shared = Def.setting(Seq())

  val preview = Def.setting(shared.value ++ Seq(

    "com.vmunier" %% "play-scalajs-scripts" % "0.1.0",

    "org.scala-lang.modules" %% "scala-async" % "0.9.2",

    "org.webjars" %% "webjars-play" % "2.3.0-2",

    "org.webjars" % "jquery" % "2.1.1",

    "org.webjars" % "Semantic-UI" % semanticUIVersion,

    "org.webjars" % "codemirror" % "4.11",

    "org.webjars" % "ckeditor" % "4.4.1",

    "com.lihaoyi" %% "utest" % "0.3.1",

    "org.webjars" % "three.js" % "r66",

    "org.webjars" % "selectize.js" % selectizeVersion,

    "org.denigma" %% "semweb" % Versions.semWebVersion,

    "org.denigma" %% "semweb-sesame" % Versions.semWebVersion,

    "org.w3" %% "sesame" % "0.7.2-SNAPSHOT" excludeAll ExclusionRule(organization = "org.openrdf.sesame"), //sesame bunding to bananardf

    "org.openrdf.sesame" % "sesame-rio-rdfxml" % sesameVersion,

    "org.openrdf.sesame" % "sesame-rio-turtle" % sesameVersion,

    "com.pellucid" %% "framian" % "0.3.3",

    "com.markatta" %% "scalenium" % scaleniumVersion % "test"  excludeAll ExclusionRule(organization = "org.specs2")

  ))
  val macro_js = Def.setting(shared.value++Seq(
      "org.scala-js" %%% "scalajs-dom" % "0.8.0",

      "com.lihaoyi" %%% "scalatags" % scalaTagsVersion,

      "com.lihaoyi" %%% "scalarx" % scalaRxVersion
  ))

  val models_js = Def.setting(shared.value++Seq(
      "org.denigma" %%% "semweb" % Versions.semWebVersion,

      "com.lihaoyi" %%% "scalarx" % scalaRxVersion
  ))

  val models_jvm = Def.setting(shared.value++Seq(

      "org.denigma" %% "semweb" % Versions.semWebVersion,

      "com.lihaoyi" %% "scalarx" % scalaRxVersion

  ))

  val binding = Def.setting(shared.value++Seq(

    "be.doeraene" %%% "scalajs-jquery" % "0.8.0",

    "org.denigma" %%% "codemirror" % codeMirrorFacade,

    "com.softwaremill.macwire" %% "macros" % macwireVersion,

    "com.markatta" %% "scalenium" % scaleniumVersion % "test"

  )
  )


  val bindingPlay = Def.setting(shared.value++Seq(

    "com.typesafe.play" %% "play" % "2.3.8",

    "com.softwaremill.macwire" %% "macros" % macwireVersion,

    "com.softwaremill.macwire" %% "runtime" % macwireVersion
  ))

  val ui = Def.setting(shared.value)



}