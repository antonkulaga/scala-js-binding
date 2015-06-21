
/**
 * Different components have different version we keep them here
 */
object Versions extends LibVersions with WebJarsVersions with ScalaJSVersions with SharedVersions
{

  val main = "0.7.8"

  val semWeb = "0.7.6"

  val binding = "0.7.16"

  val semanticBinding = "0.7.16"

  val bindingPlay = "0.7.9"

  val jsmacro = "0.1.11"

  val scala = "2.11.6"

  val scraper = "0.1.1"

  val schemas = "0.1.1"


}

trait WebJarsVersions{

  val jquery =  "2.1.3"

  val semanticUI = "1.12.3"

  val threeJS = "r66"

  val N3 = "799fee7697"

  val selectize = "0.12.1"

  val codemirror = "5.3"

  val ckeditor = "4.4.7-1"
}

/**
 * Scala js and mixed libs
 */
trait ScalaJSVersions {

  val jqueryFacade =  "0.6"

  val dom = "0.8.1"

  val codeMirrorFacade = "5.3-0.5"

  val selectizeFacade = "0.12.1-0.2.0"

}

//verrions for libs that are shared between client and server
trait SharedVersions
{
  val autowire = "0.2.5"

  val scalaRx = "0.2.8"

  val scalaTags = "0.5.2"

  val quicklens = "1.3.1"

  val scalaCSS = "0.3.0"

  val productCollections = "1.4.2"
}


trait LibVersions {

  val lruMap = "0.6.2"

  val macwire = "1.0.2"

  val scalenium = "1.0.1"

  val sesame = "2.7.12"

  val specs2 = "3.1"

  val play = "2.4.0"

  val banana = "0.8.2-SNAP3"

  val playScripts = "0.1.0"

}
