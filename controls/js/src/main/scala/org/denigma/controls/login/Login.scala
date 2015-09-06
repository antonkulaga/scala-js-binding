package org.denigma.controls.login
import org.denigma.binding.extensions._
import org.scalajs.dom.ext.AjaxException
import rx.ops._

import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.util.{Failure, Success}

/**
  * Deals with login features
  */
trait Login extends BasicLogin{

  val loginWithEmail = this.username.map(l=>l.contains('@'))

   /**
    * When the user decided to switch to login
    */
   val loginToggleClick = loginClick.takeIf(inRegistration)

   /**
    * When the user comes from registration to login
    */
   val toggleLogin = this.loginToggleClick.handler{
     this.inRegistration() = false
   }

   val authClick = loginClick.takeIfAll(canLogin,inLogin)
   val authHandler = authClick.handler{
     val auth = if(this.loginWithEmail.now) session.emailLogin(email.now,password.now) else session.usernameLogin(username.now,password.now)
     auth.onComplete{
       case Success(result)=>
         session.setUsername(this.username.now)

       case Failure(ex:AjaxException) =>
         //this.report(s"Authentication failed: ${ex.xhr.responseText}")
         this.report(ex.xhr)

       case Failure(th) => this.reportError(s"unknown failure $th")
     }
   }
 }
