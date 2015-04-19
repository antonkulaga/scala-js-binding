package controllers.genes

import controllers.endpoints.Items
import org.denigma.semweb.shex._




object GenesItems extends LoadGenAge{

  lazy val genes:List[PropertyModel] = this.loadData//.take(10)


  def populate(holder:Items)  = {
    holder.properties = this.properties
    holder.items = holder.items + (evidenceShape.id.asResource->genes)
    holder.shapes = holder.shapes + (evidenceShape.id.asResource-> evidenceShape)
  }

}