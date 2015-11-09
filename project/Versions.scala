object Versions extends WebJarsVersions with ScalaJSVersions with ScalaJVMVersions with SharedVersions
{
	val scala = "2.11.7"

	val binding = "0.8.0-M7"

  val macroBinding = "0.1.15"

	val bananaRdf = "0.8.2-SNAP4" // "0.8.1"

	val controls = "0.0.8-M8"
}

trait ScalaJVMVersions {

	val akkaHttp = "1.0"

	val akkaHttpExtensions = "0.0.6"

	val ammonite = "0.4.8"

	val seleniumJava = "2.47.2"
}

trait ScalaJSVersions {

 	val dom = "0.8.2"

	val facade = "0.0.1"

	val jqueryFacade = "0.8"

	val semanticUIFacade = "0.0.1"

	val codemirrorFacade = "5.4-0.5"

  val ckeditor = "4.4.7-1"

	val threejsFacade =  "0.0.71-0.1.5"
}

// versions for libs that are shared between client and server
trait SharedVersions
{
	val autowire = "0.2.5"

	val scalaRx = "0.2.8"

	val quicklens = "1.4.1"

	val scalaTags = "0.5.3"

	val scalaCSS = "0.3.1"

	val productCollections = "1.4.2"

	val macroParadise = "2.1.0-M5"

	val scalaTest =  "3.0.0-SNAP11"

	val booPickle = "1.1.0"

	val spire = "0.11.0"
}


trait WebJarsVersions{

	val jquery =  "2.1.4"

	val semanticUI = "2.1.4"

	val codemirror = "5.5"

	val threeJS = "r71"

	val webcomponents = "0.7.7"
}

