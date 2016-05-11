object Versions extends WebJarsVersions with ScalaJSVersions with ScalaJVMVersions with SharedVersions
{
	val scala = "2.11.8"

	val binding = "0.8.7"

  val macroBinding = "0.5"

	val bananaRdf = "0.8.2-SNAP5" // "0.8.1"

	val controls = "0.0.15"

}

trait ScalaJVMVersions {

	val akkaHttp = "2.4.4"

	val akkaHttpExtensions = "0.0.10"

	val ammonite = "0.5.7"

	val seleniumJava = "2.53.0"

	val config = "1.3.0"

	val ficus: String = "1.2.4"

	val betterFiles = "2.15.0"
}

trait ScalaJSVersions {

 	val dom = "0.9.0"

	val jqueryFacade = "0.11"

	val semanticUIFacade = "0.0.1"

	val codemirrorFacade = "5.11-0.7"

	val threejsFacade =  "0.0.74-0.1.6"
}

// versions for libs that are shared between client and server
trait SharedVersions
{
	val scalaRx = "0.3.1" //val scalaRx = "0.3.1.1" //temporary published snapshot

	val scalaTags = "0.5.4"

	val scalaCSS = "0.4.0"

	val macroParadise = "2.1.0"

	val scalaTest =  "3.0.0-M16-SNAP4"

	val booPickle = "1.1.3"

	val productCollections = "1.4.3"

	val fastparse = "0.3.7"
}

trait WebJarsVersions{

	val semanticUI = "2.1.8"

	val codemirror = "5.13.2"

	val threeJS = "r74"

	val webcomponents = "0.7.12"

	val jquery = "2.2.3"
}

