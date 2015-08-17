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

Bindings are done in a following way. For instance taken following html:
```html
   <div class="right menu" data-view="login">
       <div class="item"  data-showif="isSigned">
            <div class="ui teal button" data-event-click="logoutClick">Log out</div>
        </div>
   <!some other code>
   </div>
```

Here at the beginning property *data-view="login"*  attaches LoginView scala class to corresponding div tag.  
Then each *data-bind-propertyname* property is binded to corresponding property of LoginView class.
All bindings inside of children tags will be binded tp properties of the view or its child views.
Each view can have children (subviews). There is also a main, top view, that usually binds to body tag.
Usually bindings are done to reactive variables (see further), but binders are separate from view classes,
 so one can define whatever binder (s)he wants. In quoted sample there are bindings to isSigned and logoutClick reactive variables.

Binders are the classes that bind ScalaJS view to html elements. Each view can have many different binders. 
Common usage practise is extending one of abstract classes in binding or semantic-binding package and adding binders by defining them
in overrides of def attachBinders() method.


Getting started
===============

The best way to understand is to look at the code (mostly js part of binding crossproject) and at the Preview application.

**WARNING** current branch is development one. At the moment library is underdoing heavy refactoring.


Looking into sample App
-----------------------

You can also look at Akka-http application inside scalajs-binding repository to see how bindings can be used in Akka-http app. 

    * Install sbt (from http://www.scala-sbt.org/ ):
    * type following commands
        $ sbt//sbt console
        $ re-start //from sbt console

It will open Akka-http app with some examples 

Adding to your project
----------------------

All versions are published to bintray repository ( https://bintray.com/denigma/denigma-releases/binding/view )
So in order to use the library you have to add bintray sbt plugin to your sbt configuration (see https://github.com/softprops/bintray-sbt
 for more info) in plugins.sbt

In order to resolve a lib you should add a resolver::
```scala
resolvers += sbt.Resolver.bintrayRepo("denigma", "denigma-releases") //add resolver
libraryDependencies += "org.denigma" %%% "binding" % "0.8.0-M1"
```

If you want to use semantic-web binding (binding to RDF Graphs) than also add 

```scala
libraryDependencies += "org.denigma" %%% "semantic-binding" % "0.8.0-M1"
```

The library is published for scalajs 0.6.x and scala 2.11.7

Framework elements
=================


Binding to properties
---------------------

For instance taken following html:
```html
   <div class="right menu" data-view="login">
       <div class="item"  data-showif="isSigned">
            <div class="ui teal button" data-event-click="logoutClick">Log out</div>
        </div>
   <!some other code>
   </div>
```

Here at the beginning property *data-view="login"*  attaches LoginView scala class to corresponding div tag.  
Then each *data-bind-propertyname* property is binded to corresponding property of LoginView class.
All bindings inside of children tags will be to properties of this view. There is a view hierarchy. Each view can have subviews. 
Usually mains view attached to body tag is created. In quoted sample there are bindings to isSigned and logoutClick reactive variables.


In general following kinds of bindings are available: 
    * data-bind="propertyname" that binds html element to the property. Depending on html tag it can be text inside html or value (if input element is used)     
    * data-bind-attributename="propertyname" that binds attribute to property. For instance data-bind-href="currentLink" binds href attribute   
    * data-showif="propertyname" and data-hide-if that show/hide tags according to boolean value
    * data-event-eventname="eventproperty" that handles various events (clicks, mouseovers and so on)
    * data-html that binds inner html to some scalatag
    * data-view="ViewClassName" - to load Scala view class for html element. 
    * data-param="paramvalue" - defines some parameter for the view that will be used for its initialization
    * data-template="true" - defines if current tag is a template of item inside collection, if so it will be copied for each new item
    * data-item-bind="itempropertyvalue" that are used inside collections to bind tag to properties of corresponding item in the collections
In most of the cases reactive definitions (Rx-es) or reactive varaibles (Var-s) are used for binding because of they great ability to propagate
changes to other variables and be observed by handlers. You can read more about them in ScalaRx library ( https://github.com/scala-js/scala-js )
documentation. You can also write your own bindings and add them to views.


Collection binding
------------------

Like in template engines you may define a template html for collection element. Consider following sample: 

```html
<nav class="ui large blue inverted main menu" data-view="menu" data-param-path="menu/top">
    <a data-template="true" class="active item header" data-item-bind-href="uri" data-item-bind="label" data-load-into="main">
        <i class="ui-icon-home"></i>
    </a>
</nav>
```

There we have a MenuView that will be initialized with "menu/top" parameter. 
Inside MenuView scala class there may be an Ajax call that will get JSON with elements.
data-template="true" defines that this tag will be copied for each Menu item.
Item properties will also bind to tags copied from this template ( data-item-bind is used for this).
Link text will be taken from "label" properties of MenuItems (due to data-item-bind="label").


Views
-----

The general idea of the library that you deal with scala view classes that are connected to corresponding html elements
and initiated according to data-view="ViewClassName" html attributes.
Each view can also take some parameters defined by data-param="value" html attributes. For simplicity you may treat views as user interface components.
Views have their hierarchy. Each view can have subviews and has parent view. There is also a recursive topParent method if you want to get top view.
Each view has bindView and bind methods.
*bindView* method is fired when the view has been attached to html element.
*bind* method is used to assess and html element, if it has some *data-bind* attributes they will be binded to corresponding view properties.
Whenever bind method finds any html tag with data-view="viewclassname" property it tries to create a view and add it to subviews. After that all children elements
of this html element will be binded not by this view but by corresponding subview.


How views and bindings work
---------------------------

In order to benefit from html bindings you should:
Declare attributes for views and properties in html. For instance taken following html:
```html
<div class="right menu" data-view="login">
    <div class="item"  data-showif="isSigned">
         <div class="ui teal button" data-event-click="logoutClick">Log out</div>
     </div>
<!some other code>
</div>
```

For it you should have LoginView class with corresponding reactive definitions (rx) or reactive variables (isSigned and logoutClick). 
You can read about reactive variables/definitions (or at ScalaRx readme ( https://github.com/lihaoyi/scala.rx ).
In our case it may look like:

```scala
class LoginView(val elem:HTMLElement, val params:Map[String,Any]) extends BindableView
{
val isSigned = Var(false)
val loginClick: Var[MouseEvent] = Var(EventBinding.createMouseEvent())
//...some other code
```
Each view takes html element and some arbitrary params as input. 
* To specify a view  data-view-viewname is used
* To bind to a property data-bind-propertyname is used
* To bind to an event data-even-eventvariablename is used

In case of binding to events you should create them as reactive variables ( Var(EventBinding.createMouseEvent()) ) and than use observers to monitor changes.
There are several useful methods that allow to put observables/handlers to reactive variables, incl. those created from events.
So in order to handle, for instance, logout click, you can:
```scala
  val logoutHandler = logoutClick.handler{ //some actions inside }
```

Then you should assign binders to your class. Binders are classes that bind your view to html. Each view can have multiple binders.
In the simpliest case it looks like that:
```scala
 override protected def attachBinders(): Unit = this.withBinders(new GeneralBinder(this))
```

3)  Declare child views in html and register view names by ViewInjector (in any place you want),like
```scala
    override lazy val injector = defaultInjector
        .register("login"){
          case (el, args) =>
            new LoginView(el,args).withBinder(new GeneralBinder(_))
        }
```
Here we also assign general binder that is used to bind html propertries to reactive strings (Rx[String]),booleans and so on.
Under the hood bindings are done with use of macroses. All rx variables (more about reactive variables are extracted by macroses into Map-s to make them accessible
for binding views.
 
 
Semantic-binding
----------------

A big part of the code (whole  org.denigma.semantic package) is devoted to binding of semanticweb properties and resources to html.
Semantic web stack is a great thing to deal with heterogeneous information and extracting knowledge from data. The data there is stored as
as statements about different facts in a triplet/quad format (subject predicate object), usually RDF databases like Sesame, Jena, Bigdata
are used. Banana-RDF is not stable yet =(

Rough edges
===========

Macro evaluation
----------------

As macroses create Maps of properties that will be further used for binding, all macroses are evaluated when they are called.
That means that if you declared 
```scala 
val strings = this.extractStringRx(this)
```
to extract all properties of the view and than inherited from this view, you will have all only properties of parent class in the map, but not of the current one,
That is why we declare binders that extract properties only in final classes.

```scala
        override lazy val injector = defaultInjector
            .register("login"){
              case (el, args) =>
                new LoginView(el,args).withBinder(new GeneralBinder(_))
            }
```

 
Architecture
============

Project structure
-----------------

The repo consists of several subprojects:

* Preview cross-project (akka-http jvm projct and some shared code) //they are in the repo but they are not published
* Binding library itself:  binding (it is crossproject where most of the code is in js subproject) and jsmacro projects
* semantic-binding library (it is based on binding library and will be separated from it in the future)
The root project is akka-http app with some samples. preview project is used only for preview purposes.