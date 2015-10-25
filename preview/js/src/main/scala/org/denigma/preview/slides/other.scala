package org.denigma.preview.slides

import org.denigma.binding.extensions._
import org.denigma.binding.macroses.ClassToMap
import org.denigma.binding.views.{BindableView, MapCollectionView}
import org.scalajs.dom
import org.scalajs.dom.html.Input
import org.scalajs.dom.raw.{Element, KeyboardEvent}
import rx._
import rx.core.Var

import scala.collection.immutable.Map
import scala.util.Random


/**
 * Slide about RDF-related binding
 * @param elem html element to which view is attached
 */
class SparqlSlide(val elem:Element) extends BindableView
{
  val text = Var("")


  val input = Var("01.01.2010")
  val tree = Rx {
    // new Calculator(input()).InputLine.run().map(i=>i.toString).getOrElse("failure") // evaluates to `scala.util.Success(2)`
    //  new DateParser(input()).InputLine.run().map(i=>i.toString).getOrElse("failure")
    ""
  }

}



/**
 * Class for testing purposes that makes a long list out of test element
 */
class LongListView(element:Element) extends MapCollectionView(element){

  val items: Var[List[Map[String, Any]]] = Var{
    List(
      Map("prop"->"value1"),Map("prop"->"value2"),Map("prop"->"value3"),Map("prop"->"value4"),Map("prop"->"value5")
    )

  }

}

class TestMacroView(val elem:Element) extends BindableView{

  case class HelloWorld(hello:String){

    val one = "ONE"
    val two = "TWO"
    val three = "THREE"

    val num = 12345678
  }
  val h = HelloWorld("WORLD")
  val result = implicitly[ClassToMap[HelloWorld]].asMap(h)
  dom.console.log(result.toString)

}

class ControlSlide(val elem:Element, val params:Map[String,Any]) extends BindableView
{

  val loginHTML: rx.Var[String] = Var(
  """
    |<nav class="ui fluid vertical menu" data-view="login">
    |
    |  <div id="message" class="ui negative message" data-bind="message" data-showif="hasMessage">
    |    </div>
    |
    |  <div class="ui icon input item" data-hideif="isSigned">
    |    <input id="login" type="text" placeholder="username" data-bind="username">
    |    <i data-class-icon-if="validUsername" class="checkmark green sign"></i>
    |  </div>
    |
    |
    |  <div class="ui icon input item" data-showif="inRegistration" data-hideif="isSigned" data-class-error-unless="emailValid">
    |    <input id="email" type="text" data-bind="email" placeholder="email"><i data-class-icon-if="emailValid" class="checkmark green sign"></i>
    |  </div>
    |
    |
    |  <div class="ui input icon item" data-hideif="isSigned">
    |    <input type="password" data-bind="password" placeholder="password"><i data-class-icon-if="validPassword" class="checkmark green sign"></i>
    |  </div>
    |
    |  <div class="ui input icon item" data-class-error-unless="samePassword" data-showif="inRegistration" data-hideif="isSigned">
    |    <input type="password" data-bind="repeat" placeholder="repeat password"><i data-class-icon-if="samePassword" class="checkmark green sign"></i>
    |  </div>
    |
    |  <div class="ui small buttons" data-hideif="isSigned">
    |    <button id="loginBtn" class="ui small button"  data-class="loginClass" data-event-click="loginClick">Login</button>
    |    <div class="or"></div>
    |    <button  id="registerBtn" class="ui small button" data-class="signupClass" data-event-click="signupClick">Sign up</button>
    |  </div>
    |
    |  <div class="ui positive small message item" data-showif="wantsLogin">
    |      Click on <i>Login</i> will sign in
    |  </div>
    |
    |  <div class="ui positive small message item" data-showif="wantsRegistration">
    |      Click on <i>Sign up</i> will register
    |  </div>
    |
    |  <div class="ui item"  data-showif="isSigned">
    |        Logged in as <span data-bind="registeredName"></span>
    |  </div>
    |
    |  <div class="ui item"  data-showif="isSigned">
    |    <div class="ui green button" data-event-click="logoutClick">Log out</div>
    |  </div>
    |
    |</nav>
  """.stripMargin
  )

  val loginCode: rx.Var[String] = Var(
  """
    |class LoginView(val elem:Element, val session:Session, val params:Map[String,Any])
    |  extends BindableView
    |  with Login
    |  with Registration
    |  with Signed
    |{
    |
    |  isSigned.onChange("isSigned",uniqueValue = true,skipInitial = true) { value=>
    |    if(value)  inRegistration() = false
    |  }
    |
    |  val emailLogin = Rx{ username().contains("@") }
    |
    |  /**
    |   * If anything changed
    |   */
    |  val anyChange = Rx{ (username(),password(),email(),repeat(),inLogin()) }
    |  val clearMessage = anyChange.onChange("anyChange",uniqueValue = true,skipInitial = true) { value=>
    |    message()=""
    |  }
    |}
    |
    |
    |/**
    |  * Deals with login features
    |  */
    |trait Login extends BasicLogin{
    |
    |  val loginWithEmail = this.username.map(l=>l.contains('@'))
    |
    |   /**
    |    * When the user decided to switch to login
    |    */
    |   val loginToggleClick = loginClick.takeIf(inRegistration)
    |
    |   /**
    |    * When the user comes from registration to login
    |    */
    |   val toggleLogin = this.loginToggleClick.handler{
    |     this.inRegistration() = false
    |   }
    |
    |   val authClick = loginClick.takeIfAll(canLogin,inLogin)
    |   val authHandler = authClick.handler{
    |     val auth = if(this.loginWithEmail.now) session.emailLogin(email.now,password.now) else session.usernameLogin(username.now,password.now)
    |     auth.onComplete{
    |       case Success(result)=>
    |         session.setUsername(this.username.now)
    |
    |       case Failure(ex:AjaxException) =>
    |         //this.report(s"Authentication failed: ${ex.xhr.responseText}")
    |         this.report(ex.xhr)
    |
    |       case Failure(th) => this.reportError(s"unknown failure $th")
    |     }
    |   }
    | }
  """.stripMargin
  )

  val selectionHTML = Var(
  """
    |<section data-view="Selection">
    |  <div class="selection box">
    |    <a data-template="true" class="selection item ui label" data-bind-value="value" data-bind="label" data-style-order="order"></a>
    |    <input class="selection search" data-bind="input" data-event-keydown="onkeydown" data-style-order="order" autofocus>
    |  </div>
    |  <div class="selection options" data-showif="hasOptions" data-view="options">
    |    <div data-template="true" class="selection option" data-event-click="select" data-bind-value="value" data-bind="label">Afghanistan</div>
    |  </div>
    |</section>
  """.stripMargin
  )

  val selectionCode = Var(
    """
      |  class TestPromoSelection(val elem:Element, val params:Map[String,Any]) extends TextSelectionView {
      |    override val suggester = new TypedSuggester(input,Var(TestOptions.options))
      |    override lazy val items:Var[collection.immutable.SortedSet[Item]] = Var(TestOptions.items.map(Var(_)))
      |  }
    """.stripMargin)

}


class Test(val elem:Element) extends BindableView{


  protected def onKeyChange(fun:Input=>Unit)(k:KeyboardEvent) =  k.target match {
      case n:dom.html.Input if k.currentTarget==k.target=> fun(n)
      case other=> //nothing
    }

  def onChange(input:dom.html.Input) = {
    var (oldvalue,newvalue) = ("","")
    input.onkeydown = onKeyChange(input=>oldvalue=input.value) _
    input.onkeyup = onKeyChange{input=>
      oldvalue=input.value
      dom.console.log(s"VALUES = $oldvalue and $newvalue")
    } _
  }

  override def bindView() = {
    super.bindView()
    dom.console.log("let us start!")
    sq.byId("txt") match {
      case Some(input:dom.html.Input)=>
        onChange(input)
      case other=>dom.console.error("cannot find txt")
    }

  }



}


