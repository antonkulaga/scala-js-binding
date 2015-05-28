package controllers.endpoints

import controllers.PjaxController
import controllers.genes.GenesItems
import controllers.literature.{ArticleItems, TaskItems}

/**
 * Tools like sparql and paper viewer
 */
object MainEndpoint extends PjaxController with ExploreEndpoint with ModelEndpoint with ShapeEndpoint
{
  ArticleItems.populate(this)
  TaskItems.populate(this)
  GenesItems.populate(this)
}

