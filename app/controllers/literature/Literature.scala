package controllers.literature

import controllers.PjaxController
import org.denigma.endpoints.UserAction

/**
 * Literature controller
 */
object Literature extends PjaxController{

  def reports() = UserAction{implicit request=>
    this.pj(views.html.papers.reports(request))
  }


}
