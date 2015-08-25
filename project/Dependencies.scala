import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import sbt._

case class CrossDep(
										 shared:Def.Initialize[Seq[ModuleID]],
										 jvm:Def.Initialize[Seq[ModuleID]] = Def.setting(Seq.empty[ModuleID]),
										 js:Def.Initialize[Seq[ModuleID]] = Def.setting(Seq.empty[ModuleID]))

object Dependencies {

	//libs for testing
  lazy val testing: Def.Initialize[Seq[ModuleID]] = Def.setting(Seq(
		"org.scalatest" %%% "scalatest" % Versions.scalaTest
  ))

	lazy val macroses = CrossDep(
		shared = Def.setting(Seq(
			"com.lihaoyi" %%% "scalatags" % Versions.scalaTags,
			"com.lihaoyi" %%% "scalarx" % Versions.scalaRx,
			"com.github.marklister" %%% "product-collections" % Versions.productCollections
		)),

		jvm = Def.setting(Seq.empty),

		js = Def.setting(Seq( 	"org.scala-js" %%% "scalajs-dom" % Versions.dom	))
	)

	val binding = CrossDep(shared = Def.setting(Seq("com.softwaremill.quicklens" %%% "quicklens" % Versions.quicklens)),
			jvm = Def.setting(Seq.empty),
			js  = Def.setting(Seq(

			"org.querki" %%% "jquery-facade" % Versions.jqueryFacade
		)  )
	)


	val controls = CrossDep(
		shared = Def.setting(Seq.empty),
		jvm  = Def.setting( styles.value++akka.value)
		,
		js =Def.setting(	styles.value ++ Seq(
			"org.denigma" %%% "codemirror-facade" % Versions.codemirrorFacade,

			"org.denigma" %%% "selectize-facade" % Versions.selectizeFacade

		)		)
	)


	val semantic = CrossDep(
		shared = Def.setting(Seq.empty),
		jvm  = Def.setting(
			styles.value ++ Seq( "org.w3" %% "banana-plantain" % Versions.bananaRdf excludeAll ExclusionRule(organization = "com.github.inthenow")	)
		)
		,
		js =Def.setting(Seq(

			"org.w3" %%% "banana-plantain" % Versions.bananaRdf excludeAll ExclusionRule(organization = "com.github.inthenow")
		))
	)


	val previewJS = Def.setting( Seq( "org.denigma" %%% "semantic-ui-facade" % Versions.semanticUIFacade ) 	)


	//akka-related libs
	lazy val akka = Def.setting(Seq(

		"org.denigma" %%% "akka-http-extensions" % Versions.akkaHttpExtensions,

		"com.typesafe.akka" %% "akka-http-testkit-experimental" % Versions.akkaHttp
	))


	//dependencies on javascript libs
	lazy val webjars= Def.setting(Seq(

		"org.webjars" % "Semantic-UI" % Versions.semanticUI, //css theme, similar to bootstrap

		"org.webjars" % "selectize.js" % Versions.selectize, //select control

		"org.webjars" % "codemirror" % Versions.codemirror,

		"org.webjars" % "jquery" % Versions.jquery,

		"org.webjars" % "ckeditor" % Versions.ckeditor,

		"org.webjars" % "N3.js" % Versions.N3,

		"org.webjars" % "three.js" % Versions.threeJS,

		"org.webjars" % "webcomponentsjs" % Versions.webcomponents
	))

	//common purpose libs
	lazy val styles: Def.Initialize[Seq[ModuleID]] = Def.setting(Seq(
		"com.github.japgolly.scalacss" %%% "core" % Versions.scalaCSS,

		"com.github.japgolly.scalacss" %%% "ext-scalatags" %  Versions.scalaCSS
	))


}

