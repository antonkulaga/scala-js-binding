package org.denigma.binding

import controllers.PJaxPlatformWith
import controllers.endpoints.{ExploreEndpoint, ModelEndpoint, ShapeEndpoint}
import controllers.genes.GenesItems
import controllers.literature.{ArticleItems, TaskItems}

object Endpoint extends PJaxPlatformWith("literature")
with ExploreEndpoint with ModelEndpoint with ShapeEndpoint
{
  ArticleItems.populate(this)
  TaskItems.populate(this)
  GenesItems.populate(this)
}
