object Versions extends WebJarsVersions with ScalaJSVersions with ScalaJVMVersions with SharedVersions
{
	val scala = "2.11.7"

	val binding = "0.8.1-M2"

  val macroBinding = "0.1.16"

	val bananaRdf = "0.8.2-SNAP4" // "0.8.1"

	val controls = "0.0.9-M2"
}

trait ScalaJVMVersions {

	val akkaHttp = "2.0.1"

	val akkaHttpExtensions = "0.0.9-M2"

	val ammonite = "0.5.2"

	val seleniumJava = "2.48.2"

	val config = "1.3.0"
}

trait ScalaJSVersions {

 	val dom = "0.8.2"

	val jqueryFacade = "0.10"

	val semanticUIFacade = "0.0.1"

	val codemirrorFacade = "5.4-0.5"

	val threejsFacade =  "0.0.71-0.1.5"
}

// versions for libs that are shared between client and server
trait SharedVersions
{
	val scalaRx = "0.2.8"

	val quicklens = "1.4.2"

	val scalaTags = "0.5.3"

	val scalaCSS = "0.3.1"

	val macroParadise = "2.1.0-M5"

	val scalaTest =  "3.0.0-SNAP13"

	val booPickle = "1.1.1"

	val productCollections = "1.4.2"

	//val spire = "0.11.0"
}

trait WebJarsVersions{

	val semanticUI = "2.1.6"

	val codemirror = "5.5"

	val threeJS = "r71"

	val webcomponents = "0.7.7"

	val jquery = "3.0.0-alpha1"
}

