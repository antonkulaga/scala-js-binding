
/**
 * Different components have different version we keep them here
 */
object Versions extends LibVersions with WebJarsVersions with ScalaJSVersions{

  val main = "0.7.6"

  val semWeb = "0.7.0"

  val binding = "0.7.9"

  val semanticBinding = "0.7.9"

  val bindingPlay = "0.7.6"

  val jsmacro = "0.1.9"

  val scalajsJquery =  "0.8.0"

}

trait WebJarsVersions{

  val jquery =  "2.1.3"

  val semanticUI = "1.11.6"

  val threeJS = "r66"

  val N3 = "799fee7697"

  val selectize = "0.12.0"

  val codeMirror = "4.11"

  val ckeditor = "4.4.1"

  val codemirror = "4.11"
}

/**
 * Scala js and mixed libs
 */
trait ScalaJSVersions {
  val dom = "0.8.0"

  val scalaRx = "0.2.8"

  val scalaTags = "0.5.1"

  val quicklens = "1.3.1"

  val scalaCSS = "0.1.0"

  val productCollections = "1.4.2"

  val codeMirrorFacade = "4.8-0.4"

}


trait LibVersions {

  val lruMap = "0.6.2"

  val macwire = "0.8.0"

  val scalenium = "1.0.1"

  val sesame = "2.7.12"

  val scalaz = "7.1.1"

  val specs2 = "3.1"

  val play = "2.3.8"

  val banana = "0.8.1"

  val playScripts = "0.1.0"

}
