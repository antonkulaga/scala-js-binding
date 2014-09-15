package controllers.literature

import controllers.endpoints.{ItemsMock, Items}
import org.scalax.semweb.rdf.vocabulary.{RDF, XSD}
import org.scalax.semweb.rdf.{IRI, RDFValue, StringLiteral, vocabulary}
import org.scalax.semweb.shex.{Star, PropertyModel, ShapeBuilder}



object ArticleItems extends ItemsMock{



  private val art = new ShapeBuilder(de / "Article_Shape")
  art has de /"is_authored_by" occurs Star //occurs Plus
  art has de / "is_published_in" occurs Star //occurs Plus
  art has dc / "title" occurs Star //occurs ExactlyOne
  //art has de / "date" occurs Star //occurs ExactlyOne
  art has de / "abstract" of XSD.StringDatatypeIRI  occurs Star//occurs Star
  art has  de / "excerpt" of XSD.StringDatatypeIRI  occurs Star//occurs Star
  val paperShape = art.result

  def populate(holder:Items)  = {
    holder.properties = this.properties
    holder.items = holder.items + (paperShape.id.asResource->papers)
    holder.shapes = holder.shapes + (paperShape.id.asResource-> paperShape)
  }




  var papers = List(
    PropertyModel(pmid / "17098929",Map(
      RDF.TYPE->Set[RDFValue](article),
      authors -> Set[RDFValue](de /"Jin-zhong_Chen", de / "Chao-neng_Ji", de / "Guan-lan_Xu",  de / "Rong-yan_Pang", de / "Ji-hua_Yao", de / "Huan-zhang_Zhu", de / "Jing-lun_Xue", de / "William_Jia"),
      published ->Set[RDFValue](de / "Nucleic_acids_research"),
      dc / "title" -> Set[RDFValue](StringLiteral("DAXX interacts with phage PhiC31 integrase and inhibits recombination")),
      abs -> Set[RDFValue](StringLiteral(
        """BACKGROUND: Inteins are self-splicing protein elements. They are translated as inserts within host proteins that excise themselves and ligate the flanking portions of the host protein (exteins) with a peptide bond. They are encoded as in-frame insertions within the genes for the host proteins. Inteins are found in all three domains of life and in viruses, but have a very sporadic distribution. Only a small number of intein coding sequences have been identified in eukaryotic nuclear genes, and all of these are from ascomycete or basidiomycete fungi. RESULTS: We identified seven intein coding sequences within nuclear genes coding for the second largest subunits of RNA polymerase. These sequences were found in diverse eukaryotes: one is in the second largest subunit of RNA polymerase I (RPA2) from the ascomycete fungus Phaeosphaeria nodorum, one is in the RNA polymerase III (RPC2) of the slime mould Dictyostelium discoideum and four intein coding sequences are in RNA polymerase II genes (RPB2), one each from the green alga Chlamydomonas reinhardtii, the zygomycete fungus Spiromyces aspiralis and the chytrid fungi Batrachochytrium dendrobatidis and Coelomomyces stegomyiae. The remaining intein coding sequence is in a viral relic embedded within the genome of the oomycete Phytophthora ramorum. The Chlamydomonas and Dictyostelium inteins are the first nuclear-encoded inteins found outside of the fungi. These new inteins represent a unique dataset: they are found in homologous proteins that form a paralogous group. Although these paralogues diverged early in eukaryotic evolution, their sequences can be aligned over most of their length. The inteins are inserted at multiple distinct sites, each of which corresponds to a highly conserved region of RNA polymerase. This dataset supports earlier work suggesting that inteins preferentially occur in highly conserved regions of their host proteins. CONCLUSION: The identification of these new inteins increases the known host range of intein sequences in eukaryotes, and provides fresh insights into their origins and evolution. We conclude that inteins are ancient eukaryote elements once found widely among microbial eukaryotes. They persist as rarities in the genomes of a sporadic array of microorganisms, occupying highly conserved sites in diverse proteins."""))
      , excerpt ->Set[RDFValue]( StringLiteral("""Phage PhiC31 integrase has potential as a means of inserting therapeutic genes into specific sites in the human genome. However, the possible interactions between PhiC31 integrase and cellular proteins have never been investigated. Using pLexA-PhiC31 integrase as bait, we screened a pB42AD-human fetal brain cDNA library for potential interacting cellular proteins. ... Therefore, endogenous DAXX may interact with PhiC31 causing a mild inhibition in the integration efficiency. This is the first time that PhiC31 was shown to interact with an important cellular protein and the potential effect of this interaction should be further studied.""") )    )
    ),
    PropertyModel(pmid / "17069655",Map(
      RDF.TYPE->Set[RDFValue](article),
      authors -> Set[RDFValue](de / "Timothy_J_D_Goodwin", de / "Margaret_I_Butler", de / "Russell_T_M_Poulter"),
      published ->Set[RDFValue](de / "BMC_Biology"),
      dc / "title" -> Set[RDFValue](StringLiteral("Multiple, non-allelic, intein-coding sequences in eukaryotic RNA polymerase genes")),
      abs -> Set[RDFValue](StringLiteral(
        """rminal fragments. The strong interaction between DAXX and PhiC31 was further confirmed by co-immunoprecipitation. Deletion analysis revealed that the fas-binding domain of DAXX is also the region for PhiC31 binding. Hybridization between a PhiC31 integrase peptide array and an HEK293 cell extract revealed that a tetramer, 451RFGK454, in the C-terminus of PhiC31 is responsible for the interaction with DAXX. This tetramer is also necessary for PhiC31 integrase activity as removal of this tetramer resulted in a complete loss of integrase activity. Co-expression of DAXX with PhiC31 integrase in a HEK293-derived PhiC31 integrase activity reporter cell line significantly reduced the PhiC31-mediated recombination rate. Knocking down DAXX with a DAXX-specific duplex RNA resulted in increased recombination efficiency. Therefore, endogenous DAXX may interact with PhiC31 causing a mild inhibition in the integration efficiency. This is the first time that PhiC31 was shown to interact with an important cellular protein and the potential effect of this interaction should be further studied.""" ))
      , excerpt ->Set[RDFValue]( StringLiteral("""We identified seven intein coding sequences within nuclear genes coding for the second largest subunits of RNA polymerase. These sequences were found in diverse eukaryotes: one is in the second largest subunit of RNA polymerase I (RPA2) from the ascomycete fungus Phaeosphaeria nodorum, one is in the RNA polymerase III (RPC2) of the slime mould Dictyostelium discoideum and four intein coding sequences are in RNA polymerase II genes (RPB2), one each from the green alga Chlamydomonas reinhardtii, the zygomycete fungus Spiromyces aspiralis and the chytrid fungi Batrachochytrium dendrobatidis and Coelomomyces stegomyiae. The remaining intein coding sequence is in a viral relic embedded within the genome of the oomycete Phytophthora ramorum. The Chlamydomonas and Dictyostelium inteins are the first nuclear-encoded inteins found outside of the fungi.""") )    )
    ),
    PropertyModel(pmid / "17069655",Map(
      RDF.TYPE->Set[RDFValue](article),
      authors -> Set[RDFValue](de / "Eros_Lazzerini_Denchi", de / "Eros_Lazzerini_Denchi",de /"Giulia_Celli",de /"Titia_de_Lange" ),
      published ->Set[RDFValue](de / "Genes_And_development"),
      dc / "title" -> Set[RDFValue](StringLiteral("Hepatocytes with extensive telomere deprotection and fusion remain viable and regenerate liver mass through endoreduplication")),
      abs -> Set[RDFValue](StringLiteral(
        """We report that mouse liver cells are highly resistant to extensive telomere dysfunction. In proliferating cells, telomere dysfunction results in chromosome end fusions, a DNA damage signal, and apoptosis or senescence. To determine the consequences of telomere dysfunction in noncycling cells, we used conditional deletion of the telomeric protein TRF2 in hepatocytes. TRF2 loss resulted in telomeric accumulation of gamma-H2AX and frequent telomere fusions, indicating telomere deprotection. However, there was no induction of p53 or apoptosis, and liver function appeared unaffected. Furthermore, the loss of TRF2 did not compromise liver regeneration after partial hepatectomy. Remarkably, liver regeneration occurred without cell division involving endoreduplication and cell growth, thereby circumventing the chromosome segregation problems associated with telomere fusions. We conclude that nondividing hepatocytes can maintain and regenerate liver function despite substantial loss of telomere integrity."""))
      , excerpt ->Set[RDFValue]( StringLiteral("We report that mouse liver cells are highly resistant to extensive telomere dysfunction. ... To determine the consequences of telomere dysfunction in noncycling cells, we used conditional deletion of the telomeric protein TRF2 in hepatocytes. TRF2 loss resulted in telomeric accumulation of gamma-H2AX and frequent telomere fusions, indicating telomere deprotection. However, there was no induction of p53 or apoptosis, and liver function appeared unaffected. Furthermore, the loss of TRF2 did not compromise liver regeneration after partial hepatectomy. Remarkably, liver regeneration occurred without cell division involving endoreduplication and cell growth, thereby circumventing the chromosome segregation problems associated with telomere fusions. We conclude that nondividing hepatocytes can maintain and regenerate liver function despite substantial loss of telomere integrity."   ))
    )
    )
  )
}
