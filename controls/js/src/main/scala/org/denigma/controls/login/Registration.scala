package org.denigma.controls.login
import org.denigma.binding.extensions._
import org.scalajs.dom
import org.scalajs.dom.ext.{Ajax, AjaxException}
import rx._

import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.util.{Failure, Success}
//import rx.Ctx.Owner.voodoo
import rx.Ctx.Owner.Unsafe.Unsafe

/**
  * Part of the view that deals with registration
  */
trait Registration extends BasicLogin{

   /**
    * rx property binded to repeat password input
    */
   val repeat = Var("repeat")
   val emailValid: Rx[Boolean] = Rx {email().length>4 && this.isValidEmail(email())}

   /**
    * Email regex to check if email is valid
    * @param email
    * @return
    */
   def isValidEmail(email: String): Boolean = """(\w+)@([\w\.]+)""".r.unapplySeq(email).isDefined

   /**
    * True if password and repeatpassword match
    */
   val samePassword = Rx{
     password()==repeat()
   }
   /**
    * Reactive variable telling if register request can be send
    */
   val canRegister = Rx{ samePassword() && canLogin() && emailValid()}

   val wantsRegistration = Rx{ canRegister() && inRegistration()}


   val toggleRegisterClick = this.signupClick.takeIf(this.inLogin)
   val toggleRegisterHandler = this.toggleRegisterClick.onChange{ ev=>
     this.inRegistration() = true
   }

   val registerClick = this.signupClick.takeIfAll(this.canRegister,this.inRegistration)

   val registerHandler = this.registerClick.onChange{ ev=>
     session.register(username.now, password.now, email.now) onComplete {

       case Success(result)=>

         session.setUsername(this.username.now)

       case Failure(ex:AjaxException) =>
         //this.report(s"Registration failed: ${ex.xhr.responseText}")
         this.report(ex.xhr)

       case Failure(th) => this.reportError(s"unknown failure $th")
     }
   }
 }
