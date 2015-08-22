package org.denigma.controls.login
import org.denigma.binding.extensions._
import org.scalajs.dom.ext.AjaxException
import rx.Rx

import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.util.{Failure, Success}

trait Signed extends Registration {

   lazy val neutralColor = "blue"

   val onLogout = logoutClick.takeIf(this.isSigned)

   val logoutHandler = onLogout.handler{
     session.logout().onComplete{
       case Success(req) =>
         session.logout()
         this.clearAll()


       case Failure(ex:AjaxException) =>
         //this.report(s"logout failed: ${ex.xhr.responseText}")
         this.report(ex.xhr)


       case _ => this.reportError("unknown failure")

     }

   }
   /**
    * Clears everything
    */
   def clearAll() = {
     this.inRegistration()=false
     this.username() = ""
     this.password() = ""
     this.repeat() = ""
     this.email() = ""
   }

   val signupClass: Rx[String] =  Rx{
     if(this.inRegistration())
       if(this.canRegister()) "positive" else neutralColor
     else
       "basic"
   }

   val loginClass: Rx[String] = Rx{
     if(this.inLogin())
       if(this.canLogin()) "positive" else neutralColor
     else
       "basic"
   }
 }
