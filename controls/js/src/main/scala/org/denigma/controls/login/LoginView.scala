package org.denigma.controls.login

import org.denigma.binding.extensions._
import org.denigma.binding.views.BindableView
import org.scalajs.dom.raw.Element
import rx._
//import rx.Ctx.Owner.voodoo
import rx.Ctx.Owner.Unsafe.Unsafe

import scala.collection.immutable._
/**
 * Login view
 */
class LoginView(val elem: Element, val session: Session)
  extends BindableView
  with Login
  with Registration
  with Signed
{

  isSigned.onChange { case value=>
    if(value) inRegistration() = false
  }

  val emailLogin = Rx{ username().contains("@") }

  /**
   * If anything changed
   */
  val anyChange = Rx{ (username(),password(),email(),repeat(),inLogin()) }
  val clearMessage = anyChange.onChange{ value=>
    message()=""
  }

}











