package org.denigma.binding

import controllers.PjaxController
import controllers.endpoints.{ExploreEndpoint, ModelEndpoint, ShapeEndpoint}
import controllers.genes.GenesItems
import controllers.literature.{ArticleItems, TaskItems}

object Endpoint extends PjaxController("literature")
with ExploreEndpoint with ModelEndpoint with ShapeEndpoint
{
  ArticleItems.populate(this)
  TaskItems.populate(this)
  GenesItems.populate(this)
}
