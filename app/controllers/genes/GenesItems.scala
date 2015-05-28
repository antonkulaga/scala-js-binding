package controllers.genes

import controllers.endpoints.{ItemsMock, Items}
import org.denigma.schemas.genes.LoadGenAge
import org.denigma.semweb.shex._




object GenesItems extends GenAgeLoader with ItemsMock{

  lazy val genes:List[PropertyModel] = this.loadData//.take(10)


  def populate(holder:Items)  = {
    holder.properties = this.properties
    //holder.items = holder.items + (evidenceShape.id.asResource->genes)
    holder.shapes = holder.shapes + (evidenceShape.id.asResource-> evidenceShape)
  }

}