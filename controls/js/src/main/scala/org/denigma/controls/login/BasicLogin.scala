package org.denigma.controls.login

import org.denigma.binding.binders.Events
import org.denigma.binding.extensions._
import org.denigma.binding.views.BindableView
import org.scalajs.dom
import org.scalajs.dom._
import rx._
//import rx.Ctx.Owner.voodoo
import rx.Ctx.Owner.Unsafe.Unsafe

/**
  * Basic login varibales/events
  */
trait BasicLogin extends BindableView
 {


 def session: Session //is added to constructor
  /**
   * Extracts name from global
   */
  val registeredName: Rx[String]  = session.username //I know, that it is bad to have shared mustable state=)
  val isSigned: Rx[Boolean] = session.currentUser.map(_.isDefined)

   val username = Var("")
   val password = Var("")
   val email = Var("")
   val message = Var("")
   val hasMessage = message.map(_.length>0)

   val inRegistration = Var(false)
   val inLogin = Rx(!inRegistration() && !isSigned())

   val validUsername:Rx[Boolean] = username.map(l=>l.length>4 && l.length<50 && !l.contains(" ") && l!="guest")
   val validPassword:Rx[Boolean] = password.map(p=>p.length>4 && p!=username.now)
   val canLogin = Rx{validUsername() && validPassword()}
   val wantsLogin = Rx{ !isSigned() && inLogin() && canLogin() }

   val loginClick: Var[MouseEvent] = Var(Events.createMouseEvent())
   val logoutClick: Var[MouseEvent] = Var(Events.createMouseEvent())
   val signupClick: Var[MouseEvent] = Var(Events.createMouseEvent())

   def report(req: org.scalajs.dom.XMLHttpRequest): String = req.response.dyn.message match {
     case m if m.isNullOrUndef => this.report(req.responseText)
     case other => this.report(other.toString)
   }

   /**
    * Reports some info
    * @param str
    * @return
    */
   def report(str: String): String = {
     this.message()=str
     str
   }

   def reportError(str: String) = dom.console.error(this.report(str))
 }
