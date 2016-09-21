object Versions extends WebJarsVersions with ScalaJSVersions with ScalaJVMVersions with SharedVersions
{
	val sourcecode = "0.1.1"

	val scala = "2.11.8"

	val binding = "0.8.15"

	val macroBinding = "0.6.1"

	val bananaRdf = "0.8.3-SNAP1"

	val controls = "0.0.22"

	val pdfJSFacade = "0.8.0-0.0.5"

}

trait ScalaJVMVersions {

	val akkaHttp = "2.4.10"

	val akkaHttpExtensions = "0.0.14"

	val ammonite = "0.5.7"

	val seleniumJava = "2.53.1"

	val config = "1.3.0"

	val ficus = "1.2.6"

	val betterFiles = "2.16.0"

	val scalaJSscripts = "1.0.0"
}

trait ScalaJSVersions {

 	val dom = "0.9.1"

	val jqueryFacade = "1.0-RC6"

	val semanticUIFacade = "0.0.1"

	val codemirrorFacade = "5.13.2-0.7"

	val threejsFacade =  "0.0.74-0.1.6"
}

// versions for libs that are shared between client and server
trait SharedVersions
{
	val scalaRx = "0.3.1"

	val scalaTags = "0.6.0"

	val scalaCSS = "0.5.0"

	val macroParadise = "2.1.0"

	val scalaTest = "3.0.0"

	val booPickle = "1.2.4"

	val productCollections = "1.4.3"

	val fastparse = "0.4.1"

	val pprint = "0.4.2"
}

trait WebJarsVersions{

	val semanticUI = "2.2.2"

	val codemirror = "5.13.2"

	val threeJS = "r77"

	val webcomponents = "0.7.22"

	val jquery = "3.1.0"
}

