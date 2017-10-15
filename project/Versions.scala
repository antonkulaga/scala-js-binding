object Versions extends WebJarsVersions with ScalaJSVersions with ScalaJVMVersions with SharedVersions
{
	val sourcecode = "0.1.1"

	val scala = "2.12.3"

	val binding = "0.8.18"

	val macroBinding = "0.6.5"

	val bananaRdf = "0.9.0"

	val controls = "0.0.27"

	val pdfJSFacade = "0.8.0-0.0.6"

}

trait ScalaJVMVersions {

	val akka = "2.4.16"

	val akkaHttp = "10.0.10"

	val akkaHttpExtensions = "0.0.15"

	val ammonite = "1.0.0"

	val seleniumJava = "3.0.1"

	val config = "1.3.0"

	val ficus = "1.4.2"

	val betterFiles = "2.17.1"

	val scalaJSscripts = "1.1.1"
}

trait ScalaJSVersions {

 	val dom = "0.9.1"

	val jqueryFacade = "1.0"

	val codemirrorFacade = "5.22.0-0.8"

	val threejsFacade =  "0.0.74-0.1.6"
}

// versions for libs that are shared between client and server
trait SharedVersions
{
	val scalaRx = "0.3.2"

	val scalaTags = "0.6.2"

	val scalaCSS = "0.5.3"

	val macroParadise = "2.1.0"

	val scalaTest = "3.0.4"

	val booPickle = "1.2.5"

	val productCollections = "1.4.5"

	val fastparse = "1.0.0"

	val pprint = "0.5.3"

	val scalameta = "1.3.0"
}

trait WebJarsVersions{

	val semanticUI = "2.2.10"

	val codemirror = "5.24.2"

	val threeJS = "r77"

	val webcomponents = "1.0.1"

	val jquery = "3.1.1"
}

