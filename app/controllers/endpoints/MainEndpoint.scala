package controllers.endpoints

import akka.actor.Status.Success
import controllers.genes.GenesItems
import controllers.literature.{ArticleItems, TaskItems}
import controllers.PJaxPlatformWith

import scala.util.Try

/**
 * Tools like sparql and paper viewer
 */
object MainEndpoint extends PJaxPlatformWith("literature") with ExploreEndpoint with ModelEndpoint with ShapeEndpoint
{
  ArticleItems.populate(this)
  TaskItems.populate(this)
  GenesItems.populate(this)
}

