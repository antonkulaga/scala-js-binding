ScalaJS Binding project
#######################

*scalajs-binding* is a scala-scala-js framework that let you

* bind HTML tags to ScalaJS view classes and their properties
* have a reactive-event flow (you can bind dom events to Scala.Rx definitions)
* bind your collections to html element and have html elements as item templates
* deal with the backend by means of convenient storage classes that simplify CRUD operations and search/filtering
* define properties as reactive variables (Scala.Rx is used) that allows to make complex event flow in a reactive way
* use some play clases that make working for binding-framework easier
* have a tree of scala viewclasses in your app
* some useful predefined controls (like select and codeview) that you can extend
* work with linked data (rdf data) in a convenient way, bind html to RDF properties

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
Common usage practise is extending one of abstract classes in binding or semantic package and adding binders by defining them
in overrides of def attachBinders() method.


Getting started
===============

The best way to understand is to look at code and a SampleApp


Looking into sample App
-----------------------

You can also look at a Play application inside scalajs-binding repository to see how bindings can be used in Play app. 

    * Install TypeSafe stack:
        - Make sure you use JDK 1.7+ and have JAVA_HOME variable in your PATH
        - Download TypeSafe Activator (  http://typesafe.com/platform/getstarted ) and add it to your PATH
    * run the app:
        $ activator run
    * generate project files of your favourite IDE
        $ activator gen-idea #for Intellij IDEA, OR
        $ activator eclipse #for Eclipse

Play app will open with some samples code. 
Note: models and shared projects have same source folder. If you use IntellijIDEA you may encounter a bug with shared source folder,
there is a workaround for such bug - go to File->Projects_Structure and manually add symbolic link as a source folder for shared project 

Adding to your project
----------------------

All versions are published to bintray repository ( https://bintray.com/denigma/denigma-releases/binding/view )
So in order to use the library you have to add bintray sbt plugin to your sbt configuration (see https://github.com/softprops/bintray-sbt
 for more info) in plugins.sbt

```scala
resolvers += Resolver.url(
  "bintray-sbt-plugin-releases",
    url("http://dl.bintray.com/content/sbt/sbt-plugin-releases"))(
        Resolver.ivyStylePatterns)

addSbtPlugin("me.lessis" % "bintray-sbt" % "0.1.2")
```

In your sbt config you should add resolver and dependency
```scala
resolvers += bintray.Opts.resolver.repo("denigma", "denigma-releases")

libraryDependencies += "org.denigma" %%% "binding" % "0.6.2"
```

NOTE: at the moment library is published only for scalajs 0.5.x and scala 2.11.2

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
General binder is used to bind html propertries to reactive strings (Rx[String]),booleans and so on.

3)  Declare  views in html and register view names by ViewInjector (in any place you want),like
```scala
  ViewInjector.register("login", (el, params) =>Try(new LoginView(el,params)))
```

Under the hood bindings are done with use of macroses. All rx variables (more about reactive variables are extracted by macroses into Map-s to make them accessible
for binding views.
 
 
Storages
--------

In order to do CRUD on iterms rendered to collections of views we need some code that will send requests to the server and get responses.
In order to abstract veiws from communication details storages have been created. Storages are just classes that do CRUD via ajax or websockets.
In scala-js-binding there are several predefined storage classes that use scala-js-pickling and send/receive scala case classes to the server and back.
 
Semantic web
------------

A big part of the code (whole  org.denigma.semantic package) is devoted to binding of semanticweb properties and resources to html.
Semantic web stack is a great thing to deal with heterogeneous information and extracting knowledge from data. The data there is stored as
as statements about different facts in a triplet/quad format (subject predicate object), usually RDF databases like Sesame, Jena, Bigdata
are used. In future Semanticweb part of the library will be separated into separate subproject and published as separate artifact.

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
That is why you should override def activateMacro() in your code with a code that calls properties extraction

```scala
    override def activateMacro(): Unit = { extractors.foreach(_.extractEverything(this))}

```
The easiest way it so inherit from OrdinaryView (where this methods are not implemented and you can let your IDEA tell you what methods to implement)

I know that it would be nice to get rid of this boilerplate but I have not found the solution how to trick macro evaluations

View registration
-----------------

In order to know what view should be created for value of data-view attribute, some factories should be initialized. 
So you also have to register with ViewInspector somewhere by providing a factory function that will create it from parameters. 
The best place for this is ScalaJS main object

```scala
  ViewInjector.register("menu", (el, params) =>Try{ new MenuView(el,params) })
```

I hope to get rid of this in future when I will figure out what kind of dependency injection to choose for view creation.

 
Architecture
============

Project structure
-----------------

The repo consists of several subprojects:

* Demo code (play app root project, frontend subproject, shared subproject) //they are in the repo but they are not published
* Library itself ( binding and jsmacro projects)

The root project is play app with some samples. binding-preview project is not published but used only for preview purposes.