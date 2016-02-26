object Versions extends WebJarsVersions with ScalaJSVersions with ScalaJVMVersions with SharedVersions
{
	val scala = "2.11.7"

	val binding = "0.8.2"

  val macroBinding = "0.4"

	val bananaRdf = "0.8.2-SNAP4" // "0.8.1"

	val controls = "0.0.10"
}

trait ScalaJVMVersions {

	val akkaHttp = "2.4.2"

	val akkaHttpExtensions = "0.0.10"

	val ammonite = "0.5.3"

	val seleniumJava = "2.52.0"

	val config = "1.3.0"
}

trait ScalaJSVersions {

 	val dom = "0.9.0"

	val jqueryFacade = "0.10"

	val semanticUIFacade = "0.0.1"


	val codemirrorFacade = "5.4-0.5"

	val threejsFacade =  "0.0.71-0.1.5"
}

// versions for libs that are shared between client and server
trait SharedVersions
{
	val scalaRx = "0.3.0" //val scalaRx = "0.3.1.1" //temporary published snapshot

	val quicklens = "1.4.2"

	val scalaTags = "0.5.4"

	val scalaCSS = "0.4.0"

	val macroParadise = "2.1.0"

	val scalaTest =  "3.0.0-SNAP13"

	val booPickle = "1.1.2"

	val productCollections = "1.4.2"

	val fastParse = "0.3.4"
}

trait WebJarsVersions{

	val semanticUI = "2.1.8"

	val codemirror = "5.11"

	val threeJS = "r74"

	val webcomponents = "0.7.12"

	val jquery = "3.0.0-alpha1"
}

