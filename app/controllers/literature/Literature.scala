package controllers.literature

import controllers.PJaxPlatformWith
import org.denigma.binding.play.UserAction

/**
 * Tools like sparql and paper viewer
 */
object Literature extends PJaxPlatformWith("literature") with ExploreArticles with ArticleModels{

  def reports() = UserAction{implicit request=>
    this.pj(views.html.papers.reports(request))
  }


}

