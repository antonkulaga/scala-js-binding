package controllers

import controllers.literature.{ArticleItems, TaskItems}
import org.denigma.binding.play.UserAction

/**
 * Tools like sparql and paper viewer
 */
object MainEndpoint extends PJaxPlatformWith("literature") with ExploreEndpoint with ModelEndpoint
{
  ArticleItems.populate(this)
  TaskItems.populate(this)
}

