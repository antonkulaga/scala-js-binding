package controllers.genes

import java.util.Date

import controllers.endpoints.{Items, ItemsMock}
import org.scalax.semweb.rdf._
import org.scalax.semweb.rdf.vocabulary.{RDF, XSD}
import org.scalax.semweb.shex
import org.scalax.semweb.shex._




object GenesItems extends LoadGenAge{

  lazy val genes:List[PropertyModel] = this.loadData//.take(10)

    /*List(   PropertyModel(entrezId,
      Map[IRI,Set[RDFValue]](
        db -> Set(GenAge),
        objId -> Set(IntLiteral(20)),
        entrezId -> Set(entrez / "32001"),
        ref -> Set(pmid / "18059160"),
        code -> Set(IMP.code),
        dbObjectName ->Set(StringLiteral("Autophagy-related 8a")),
        clazz ->Set(de / "autophagy genes"),
        date ->Set(DateLiteral(new Date(2014,11,27))),
        taxon -> Set(StringLiteral("Drosophila melanogaster")),
        influence->Set(StringLiteral("Pro-Longevity"))
      )
    )
  )*/

  def populate(holder:Items)  = {
    holder.properties = this.properties
    holder.items = holder.items + (evidenceShape.id.asResource->genes)
    holder.shapes = holder.shapes + (evidenceShape.id.asResource-> evidenceShape)
  }
  //DB	DB Object ID	DB Object Symbol	ENTREZID	DB:Reference	Evidence Code	With (or) From	DB Object Name	DB Object Synonym	DB Object Type	Class	Date	Annotation Extension	Gene Product Form ID	Model organism	Tissue	Influence	<- словесный идентификатор колонки
  //GenAge	22	Atg8a	32001	PMID:18059160	IMP		Autophagy-related 8a	atg8; Atg8; Atg8/LC3; atg8a; Atg8A; ATG8a; BcDNA:LD05816	protein	autophagy genes	20141127


}