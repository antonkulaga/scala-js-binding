ScalaJS Binding project
###########################

General idea
============

Thi library is used to provide html binding of ScalaJS reactive variables to HTML tags.

The main idea behind frontend is binding of scalajs views to html.
For instance taken following html:
```html
   <div class="right menu" data-view="login">
       <div class="item"  data-showif="isSigned">
            <div class="ui teal button" data-event-click="logoutClick">Log out</div>
        </div>
   <!some other code>
   </div>
```

Here at the beginning the html is binding to LoginView class, then each data-<something> property is binding to corresponding
reactive variable (Rx-s and Var-s in ScalaRX https://github.com/scala-js/scala-js ), so when this variable changes so does html.
There is a view hierarchy, that starts from a view that is automaticly binded to "body" tag

There is also a shared (scala) folder for classes that are shared between frontend and backend, as well as picklers.

Getting started
===============

Setting Up
==========

To set up the project you need to (most of instructions are for Deiban/Ubuntu based Linux, but for Windows it will be somehow similar)::

    * Install TypeSafe stack:
        - Make sure you use JDK 1.7+ and have JAVA_HOME variable in your PATH
        - Download TypeSafe Activator (  http://typesafe.com/platform/getstarted ) and add it to your PATH
    * run the app:
        $ activator run
    * generate project files of your favourite IDE
        $ activator gen-idea #for Intellij IDEA, OR
        $ activator eclipse #for Eclipse

By deafault play app will open with various samples 

How it works
------------

In order to benefit from html bindings you should create scalajs classes that inherit from views (for instance OrdinaryView) and connect them to html
 
For instance taken following html:
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
class LoginView(element:HTMLElement, params:Map[String,Any]) extends OrdinaryView("login",element) 
{
val isSigned = Var(fale)
val loginClick: Var[MouseEvent] = Var(this.createMouseEvent())
//...some other code
```
Each view takes html element and some arbitrary params as input. 
* To specify a view  data-view-viewname is used
* To bind to a property data-bind-propertyname is used
* To bind to an event data-even-eventvariablename is used

In case of binding to events you should create them as reactive variables ( Var(this.createMouseEvent()) ) and than use observers to monitor changes.
There are several useful methods that allow to put observables/handlers to reactive variables, incl. those created from events.
So in order to handle, for instance, logout click, you can:
```scala
  val logoutHandler = logoutClick.handler{ //some actions inside }
```

Under the hood bindings are done with use of macroses. All rx variables (more about reactive variables https://github.com/lihaoyi/scala.rx ) are extracted by macroses into Map-s to make them accessible
for binding views.
 
 
Rough edges
-------------
 
 There is a problem with macro evaluation that I do not know yet how to solve: all macroses are evaluated in classes
where they are declared,that means that if you declared extractMap(this) and inherit from this class somewhere in ChildClass the maps
will be done only from the class where the macro was declared. That is the reason why there are a lot of abstract methods (with macroses) that must be
implemented when you inherit form one of the views.

That is why in your views you have to add strings like this (the easiest way it so inherit from OrdinaryView and let your IDEA tell you what methods to implement)

```scala
  val tags: Map[String, Rx[HtmlTag]] = this.extractTagRx(this)

  val strings: Map[String, Rx[String]] = this.extractStringRx(this)

  lazy val bools: Map[String, Rx[Boolean]] = this.extractBooleanRx(this)

  override def mouseEvents: Map[String, Var[MouseEvent]] = this.extractMouseEvens(this)
```

You also have to register the view somewhere in your main class by providing a functions that will create the view. The best place for this is ScalaJS main object

```scala
  org.denigma.views.register("menu", (el, params) =>Try{ new MenuView(el,params) })
```

I hope to get rid of this need when I will figure out what dependency injection to choose for view creation.
 
 
Project structure
-----------------

The repo consists of several subprojects:
* Demo code (play app root project, frontend subproject, shared subproject) //they are in the repo but they are not published
* Library itself ( binding and jsmacro projects)


The root project is play app with some samples. It is not published but used only for preview purposes.