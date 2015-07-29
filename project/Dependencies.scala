import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import sbt._


object Dependencies {

	//libs for testing
  lazy val testing = Def.setting(Seq(
		"org.scalatest" %%% "scalatest" % Versions.scalaTest
  ))

	//akka-related libs
	lazy val akka = Def.setting(Seq(

		"org.denigma" %%% "akka-http-extensions" % Versions.akkaHttpExtensions,

		"com.typesafe.akka" %% "akka-http-testkit-experimental" % Versions.akkaHttp
	))

	val previewJS = Def.setting( Seq(
		"org.denigma" %%% "semantic-ui-facade" % Versions.semanticUIFacade
	)
	)

	/*val previewJVM = Def.setting( Seq(
		)
	)*/

 val bindingJS: Def.Initialize[Seq[ModuleID]] = Def.setting(Seq(

    "org.scala-js" %%% "scalajs-dom" % Versions.dom,

    "org.querki" %%% "jquery-facade" % Versions.jqueryFacade,

    "org.denigma" %%% "codemirror-facade" % Versions.codemirrorFacade,

    "org.denigma" %%% "selectize-facade" % Versions.selectizeFacade,

    "com.softwaremill.quicklens" %%% "quicklens" % Versions.quicklens,

    "com.lihaoyi" %%% "scalarx" % Versions.scalaRx

  )  )

  val bindingJVM: Def.Initialize[Seq[ModuleID]] = Def.setting(Seq(

    "com.softwaremill.quicklens" %% "quicklens" % Versions.quicklens,

    "com.lihaoyi" %% "scalarx" % Versions.scalaRx

  )  )

  val macro_js = Def.setting(Seq(
	      "org.scala-js" %%% "scalajs-dom" % Versions.dom,

	      "com.lihaoyi" %%% "scalatags" % Versions.scalaTags,

	      "com.lihaoyi" %%% "scalarx" % Versions.scalaRx
	  ))

	//dependencies on javascript libs
	lazy val webjars= Def.setting(Seq(

		"org.webjars" % "Semantic-UI" % Versions.semanticUI, //css theme, similar to bootstrap

		"org.webjars" % "selectize.js" % Versions.selectize, //select control

		"org.webjars" % "codemirror" % Versions.codemirror,

		"org.webjars" % "jquery" % Versions.jquery,

		"org.webjars" % "ckeditor" % Versions.ckeditor,

		"org.webjars" % "N3.js" % Versions.N3,

		"org.webjars" % "three.js" % Versions.threeJS
	))

	//common purpose libs
	lazy val styles: Def.Initialize[Seq[ModuleID]] = Def.setting(Seq(
		"com.github.japgolly.scalacss" %%% "core" % Versions.scalaCSS,

		"com.github.japgolly.scalacss" %%% "ext-scalatags" %  Versions.scalaCSS
	))


}

