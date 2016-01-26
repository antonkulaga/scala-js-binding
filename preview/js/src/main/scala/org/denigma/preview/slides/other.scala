package org.denigma.preview.slides

import org.denigma.binding.binders.GeneralBinder
import org.denigma.binding.views.BindableView
import org.denigma.controls.login.LoginView
import org.denigma.controls.tabs.{TabItem, TabsView}
import org.denigma.preview.FrontEnd._
import org.denigma.preview.StatesSelectionView
import org.scalajs.dom.raw.Element
import rx.Var

import scala.collection.immutable._

class ControlSlide(val elem: Element) extends BindableView
  with LoginExample
  with SelectionExample
  with TabExample
{

  val testTabs:Var[Seq[Var[TabItem]]] = Var(
    Seq(
      Var(TabItem("Console", "Console content")),
      Var(TabItem("Chart", "Chart content")),
      Var(TabItem("Causality", "Causality content"))
    )
  )

  /**
    * Register views
    */
  override lazy val injector = defaultInjector
    .register("login") { case (el, args) =>
      new LoginView(el, session).withBinder(new GeneralBinder(_))
    }
    .register("Selection"){case (el, args) =>
      new StatesSelectionView(el, "test").withBinder{case view => new GeneralBinder(view)}
    }
    .register("Tabs"){case (el, args) =>
      new TabsView(el, testTabs).withBinder{case view => new GeneralBinder(view)}
    }
}





trait LoginExample {
  self: BindableView =>


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
      |   val toggleLogin = this.loginToggleClick.triggerLater{
      |     this.inRegistration() = false
      |   }
      |
      |   val authClick = loginClick.takeIfAll(canLogin,inLogin)
      |   val authHandler = authClick.triggerLater{
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

}

trait SelectionExample {
  self: BindableView =>


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

trait TabExample {
  self: BindableView =>

  val tabHTML = Var(
    """
      |<section data-view="@{viewName}">
      |  <div class="ui top attached tabular menu">
      |    <a data-template="true" data-bind="label" class="active item" data-class-active-if="active" data-event-click="onClick"></a>
      |  </div>
      |  <span data-view="content">
      |    <div data-template="true" class="ui bottom attached active tab segment" data-class-active-if="active" >
      |      <div data-bind="content"></div>
      |    </div>
      |  </span>
      |</section>
    """.stripMargin)

  val tabCode = Var(
  """
    |case class TabItem(label: String, content: String) // content: Element)
    |
    |case class TabItemView(elem: Element, item: Rx[TabItem], selection: Var[Option[Rx[TabItem]]]) extends BindableView {
    |  val content = item.map(_.content)
    |  val label = item.map(_.label)
    |
    |  val active = Rx{
    |    val sel = selection()
    |    sel.isDefined && sel.get.now == item()
    |  }
    |
    |  val onClick = Var(Events.createMouseEvent())
    |  onClick.triggerLater{
    |    selection() = Some(this.item)
    |  }
    |}
    |
    |class TabsContentView(val elem: Element, val items: Rx[Seq[Rx[TabItem]]], val active: Var[Option[Rx[TabItem]]]) extends BasicTabsView
    |class TabsView(val elem: Element, val items: Rx[Seq[Rx[TabItem]]]) extends BasicTabsView{
    |
    |  protected def defaultContent = ""
    |  protected def defaultLabel = ""
    |
    |  val active: Var[Option[Item]] = Var(None)
    |
    |  override protected def subscribeUpdates() = {
    |    template.hide()
    |    this.items.now.foreach(i => this.addItemView(i, this.newItemView(i)))
    |    updates.onChange("ItemsUpdates")(upd => {
    |      upd.added.foreach(onInsert)
    |      upd.removed.foreach(onRemove)
    |      upd.moved.foreach(onMove)
    |      if (active.now.isEmpty && items.now.nonEmpty) active() = items.now.headOption
    |    })
    |    if (active.now.isEmpty && items.now.nonEmpty) active() = items.now.headOption  //TODO: refactor
    |  }
    |
    |  override lazy val injector = defaultInjector
    |    .register("content"){
    |      case (el, args) =>  new TabsContentView(el, items, active).withBinder(new GeneralBinder(_))
    |    }
    |}
    |
    |trait BasicTabsView extends ItemsSeqView {
    |  type Item = Rx[TabItem]
    |  type ItemView = TabItemView
    |  def active: Var[Option[Item]]
    |
    |  override def newItemView(item: Item): ItemView = this.constructItemView(item){
    |    case (el, mp) => TabItemView(el, item, active).withBinder(new GeneralBinder(_))
    |  }
    |}
    |
  """.stripMargin
  )

}