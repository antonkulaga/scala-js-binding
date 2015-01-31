import bintray.Opts
import sbt.Keys._
import sbt._

import scala.scalajs.sbtplugin.ScalaJSPlugin._

object Dependencies
{
  val macwireVersion = "0.7.3"

  val scaleniumVersion = "1.0.1"

  val scalaRxVersion = "0.2.6"

  val semanticUIVersion = "1.7.3"

  val codeMirrorVersion = "4.11"

  val selectizeVersion = "0.11.2"

  val sesameVersion = "2.7.12"


  val shared = Def.setting(Seq())

  val preview = Def.setting(shared.value ++ Seq(

    "org.scala-lang.modules" %% "scala-async" % "0.9.2",

    "org.webjars" %% "webjars-play" % "2.3.0-2",

    "org.webjars" % "jquery" % "2.1.1",

    "org.webjars" % "Semantic-UI" % semanticUIVersion,

    "org.webjars" % "codemirror" % "4.11",

    "org.webjars" % "ckeditor" % "4.4.1",

    "com.lihaoyi" %% "utest" % "0.2.4",

    "org.webjars" % "three.js" % "r66",

    "org.webjars" % "selectize.js" % selectizeVersion,

    "org.scalax" %% "semweb" % Versions.semWebVersion,

    "org.scalax" %% "semweb-sesame" % Versions.semWebVersion,

    "org.w3" %% "sesame" % "0.7.2-SNAPSHOT" excludeAll ExclusionRule(organization = "org.openrdf.sesame"), //sesame bunding to bananardf

    "org.openrdf.sesame" % "sesame-rio-rdfxml" % sesameVersion,

    "org.openrdf.sesame" % "sesame-rio-turtle" % sesameVersion,

    "com.pellucid" %% "framian" % "0.3.3",

    "com.markatta" %% "scalenium" % scaleniumVersion % "test"  excludeAll ExclusionRule(organization = "org.specs2")

  ))
  val macro_js = Def.setting(shared.value++Seq(
     "org.scala-lang.modules.scalajs" %%% "scalajs-dom" % "0.6.1",

     "com.scalatags" %%% "scalatags" % "0.4.2",

      "com.scalarx" %%% "scalarx" % scalaRxVersion
  ))

  val models_js = Def.setting(shared.value++Seq(
      "org.scalax" %%% "semweb" % Versions.semWebVersion,
     "org.scalajs" %%% "scalajs-pickling" % "0.3.1",
      "com.scalarx" %%% "scalarx" % "0.2.6"
  ))

  val models_jvm = Def.setting(shared.value++Seq(

      "org.scalax" %% "semweb" % Versions.semWebVersion,
     "org.scalajs" %% "scalajs-pickling-play-json" % "0.3.1",
      "com.scalarx" %% "scalarx" % scalaRxVersion

  ))

  val binding = Def.setting(shared.value++Seq(

    "org.scala-lang.modules.scalajs" %%% "scalajs-jquery" % "0.6",

    "org.scalajs" %%% "codemirror" % "4.5-0.1",

    "com.softwaremill.macwire" %% "macros" % macwireVersion,

    "com.markatta" %% "scalenium" % scaleniumVersion % "test"

  )
  )


  val bindingPlay = Def.setting(shared.value++Seq(

    "com.typesafe.play" %% "play" % "2.3.7",

    "org.scalajs" %% "scalajs-pickling-play-json" % "0.3.1",

    "com.softwaremill.macwire" %% "macros" % macwireVersion,

    "com.softwaremill.macwire" %% "runtime" % macwireVersion
  ))

  val ui = Def.setting(shared.value)



}