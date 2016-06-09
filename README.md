ScalaJS Binding project
#######################
[![Gitter chat channel](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/denigma/denigma-libs?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge)

*scalajs-binding* is a scala-scala-js framework that let you

* bind HTML tags to ScalaJS view classes and their properties
* have a reactive-event flow (you can bind dom events to Scala.Rx definitions)
* bind your collections to html element and have html elements as item templates
* define properties as reactive variables (Scala.Rx is used) that allows to make complex event flow in a reactive way
* have a tree of scala view-classes in your app
* some useful predefined controls (like select, codeview and chart) that you can use
* work with linked data (rdf data) in a convenient way, bind html to RDF properties *(BananaRDF support is in progress)*

Documentation
-------------

Documentation and examples are at [scala-js-binding website](http://scala-js-binding.com).

Using in your project
----------------------

To use scala-js binding library in your project you should add it to your dependencies. 
All versions are published to bintray repository ( https://bintray.com/denigma/denigma-releases/binding/view )
You can depend on scala-js-binding library itself:
```scala
resolvers += sbt.Resolver.bintrayRepo("denigma", "denigma-releases") //add resolver
libraryDependencies += "org.denigma" %%% "binding" % "0.8.8"
```
Or you can use binding-controls library that contains UI controls and is based on scala-js-binding library:
```scala
resolvers += sbt.Resolver.bintrayRepo("denigma", "denigma-releases") //add resolver
libraryDependencies += "org.denigma" %%% "binding-controls" % "0.0.16" // to depend on html controls that are based on scala-js-binding lib
```


Building from source and running examples
-----------------------------------------

To build library from source and to look at some examples:
Install [sbt](http://www.scala-sbt.org/)
Type the following commands:
```scala
$ sbt // to open sbt console
$ re-start // will open akka-http application with examples
```          
It will open a local version of scala-js-binding website with some examples at http://localhost:5553/


The Structure of repository
---------------------------
The repo consists of several subprojects:

* Preview cross-project. This cross-project is not published and used only to show code examples. It contains akka-http based server part and scalajs part with examples.
* Binding library itself. It is a cross-project, most of its code is in scalajs part.
* Macro subproject. It is a subproject that is internally used by scala-js-binding to extract reactive variables from views with macro.
* Controls subproject. It depends on binding subproject and contains some useful html controls and charts.
* Semantic subproject. It depends on controls subproject and contains integration with Banana-RDF library. It allows to bind RDF properties to HTML and scalajs views.
* PDF subproject. This subproject is a facade for PDF.js that will be later separated to a separate repository