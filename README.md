ScalaJS Binding project
#######################
[![Gitter chat channel](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/denigma/denigma-libs?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge)

*scalajs-binding* is a scala-scala-js framework that let you

* bind HTML tags to ScalaJS view classes and their properties
* have a reactive-event flow (you can bind dom events to Scala.Rx definitions)
* bind your collections to html element and have html elements as item templates
* define properties as reactive variables (Scala.Rx is used) that allows to make complex event flow in a reactive way
* have a tree of scala viewclasses in your app
* some useful predefined controls (like select and codeview) that you can
* work with linked data (rdf data) in a convenient way, bind html to RDF properties *(BananaRDF support is in progress)*

Documentation
-------------

Documentation and examples are at [scala-js-binding website](http://scala-js-binding.com).

Adding to your project
----------------------

All versions are published to bintray repository ( https://bintray.com/denigma/denigma-releases/binding/view )

In order to resolve a lib you should add a resolver::
```scala
resolvers += sbt.Resolver.bintrayRepo("denigma", "denigma-releases") //add resolver
libraryDependencies += "org.denigma" %%% "binding" % "0.8.0"
```