import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import sbt._


object Dependencies
{

  val shared = Def.setting(Seq())

  val preview = Def.setting(shared.value ++ Seq(

    "com.vmunier" %% "play-scalajs-scripts" %  Versions.playScripts,

    "org.scala-lang.modules" %% "scala-async" % "0.9.2",

    "com.lihaoyi" %% "utest" % "0.3.1",

    "org.denigma" %% "semweb" % Versions.semWeb,

    "org.denigma" %% "semweb-sesame" % Versions.semWeb,

    "org.w3" %% "banana-sesame" % Versions.banana excludeAll ExclusionRule(organization = "org.openrdf.sesame"), //sesame bunding to bananardf

    "org.openrdf.sesame" % "sesame-rio-rdfxml" % Versions.sesame,

    "org.openrdf.sesame" % "sesame-rio-turtle" % Versions.sesame,

    "com.pellucid" %% "framian" % Versions.framian,

    "com.markatta" %% "scalenium" % Versions.scalenium % "test"  excludeAll ExclusionRule(organization = "org.specs2"),

    "org.specs2" %% "specs2-core" % Versions.specs2 % "test",

    "org.scalaz" %% "scalaz-core" % Versions.scalaz,

    "com.github.cb372" %% "scalacache-lrumap" % Versions.lruMap

   /* "com.github.japgolly.scalacss" %% "core" % Versions.scalaCSS excludeAll(
      ExclusionRule(organization = "com.chuusai"),
      ExclusionRule(organization = "org.scalaz")
      )*/


  )++webjars)


  protected lazy val webjars: Seq[ModuleID] = Seq(
    "org.webjars" %% "webjars-play" % "2.3.0-3",

    "org.webjars" % "jquery" % Versions.jquery,

    "org.webjars" % "Semantic-UI" % Versions.semanticUI,

    "org.webjars" % "codemirror" % "4.11",

    "org.webjars" % "ckeditor" % "4.4.1",

    "org.webjars" % "N3.js" % Versions.N3,

    "org.webjars" % "three.js" % Versions.threeJS,

    "org.webjars" % "selectize.js" % Versions.selectize
  )

  val macro_js = Def.setting(shared.value++Seq(
      "org.scala-js" %%% "scalajs-dom" % Versions.dom,

      "com.lihaoyi" %%% "scalatags" % Versions.scalaTags,

      "com.lihaoyi" %%% "scalarx" % Versions.scalaRx
  ))

  val models_js = Def.setting(shared.value++Seq(
      "org.denigma" %%% "semweb" % Versions.semWeb,

      "com.lihaoyi" %%% "scalarx" % Versions.scalaRx
  ))

  val models_jvm = Def.setting(shared.value++Seq(

      "org.denigma" %% "semweb" % Versions.semWeb,

      "com.lihaoyi" %% "scalarx" % Versions.scalaRx

  ))

  val binding = Def.setting(shared.value++Seq(

    "be.doeraene" %%% "scalajs-jquery" % "0.8.0",

    "org.denigma" %%% "codemirror" % Versions.codeMirrorFacade
  )  )

  val semanticBinding = Def.setting(shared.value++Seq(
    "com.lihaoyi" %%% "scalarx" % Versions.scalaRx,

    "org.w3" %%% "banana-n3-js" % Versions.banana

  ))


  val bindingPlay = Def.setting(shared.value++Seq(

    "com.typesafe.play" %% "play" %  Versions.play
  ))

  val ui = Def.setting(shared.value)



}
