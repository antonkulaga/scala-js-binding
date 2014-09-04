package controllers.endpoints

import controllers.literature.{ArticleItems, TaskItems}
import controllers.PJaxPlatformWith

/**
 * Tools like sparql and paper viewer
 */
object MainEndpoint extends PJaxPlatformWith("literature") with ExploreEndpoint with ModelEndpoint
{
  ArticleItems.populate(this)
  TaskItems.populate(this)
}

