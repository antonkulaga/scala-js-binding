package controllers.genes

import java.text.SimpleDateFormat
import java.util.Date

import framian.csv.{Csv, LabeledCsv}
import org.denigma.binding.play.UserAction
import org.scalax.semweb.shex._
import play.api.Play
import play.api.mvc.{RequestHeader, Controller}
import play.twirl.api.Html
import play.api.Play.current
import scala.io.Source
import framian._
import framian.csv.Csv
import spire.implicits._
import org.scalax.semweb.rdf.vocabulary.{RDFS, RDF, XSD}
import org.scalax.semweb.rdf._
import scala.collection.JavaConversions._
import scala.collection.immutable._

import scala.util.Try

trait LoadGenAge extends GeneSchema{

  val formats:List[SimpleDateFormat] = List(
    new SimpleDateFormat("dd/MM/yyyy"),
    new SimpleDateFormat("dd-MM-yyyy"),
    new SimpleDateFormat("dd.MM.yyyy"),
    new SimpleDateFormat("yyyy/MM/dd"),
    new SimpleDateFormat("yyyy-MM-dd"),
    new SimpleDateFormat("yyyy.MM.dd")
  )


  lazy val genAgePairs: Map[String, IRI] = Map("cDB" ->db,
    "DB.Object.ID"-> dbObjectName,
    "DB.Object.Symbol"->symbol,
    "ENTREZID"-> entrezId,
    "DB.Reference"->ref,
    "Evidence.Code" ->code,
    "With.or.From"->from,
    "DB.Object.Name" ->dbObjectName,
    "DB.Object.Synonym"->synonym,
    "DB.Object.Type"->tp,
    "Date"->date,
    "Annotation.Extension" -> extension,
    "Gene.Product.Form.ID" ->product,
    "Taxon" ->taxon,
    "Tissue" ->tissue,
    "Influence" ->influence)

  lazy val annotationPairs: Map[String, IRI] = Map("DB" ->db,
    "DB Object Name" ->dbObjectName,
    "DB Object ID"-> objId,
    "DB Object Symbol"->symbol,
    "ENTREZID"-> entrezId,
    "DB:Reference"->ref,
    "Evidence Code" ->code,
   // "With.or.From"->from,
    "DB Object Synonym"->synonym,
    "Gene product"->tp,
    "Date"->date,
    "Class" -> clazz,
    "Gene.Product.Form.ID" ->product,
    "Model organism" ->taxon,
    "Tissue" ->tissue,
    "Influence" ->influence)



  def loadData: List[PropertyModel] = {
    val fileName = "public/resources/annotations.tsv"
    val str = this.readFromFile(fileName)
    testGenesTable(str)
  }




  def str2RDFValue(string:String,base:Option[IRI] = None):RDFValue = if(string.contains("_:"))
    BlankNode(string) else if(string.contains(":")) IRI(string) else base.fold[RDFValue](StringLiteral(string))(b=>b / string)


  /**
   * Note: buggy way to parse literal
   * @param property
   * @param string
   * @param shape
   * @return
   */
  def parse(property:IRI,string:String,base:Option[IRI])(implicit shape:Shape):Option[RDFValue] =  {
   val v = shape.arcRules().collect{
      case rule if rule.name.isInstanceOf[NameTerm]
        && rule.name.asInstanceOf[NameTerm].property==property=> rule.value
    }.collectFirst{
      case ValueSet(results) if results.exists(r=>r.label==string  || r.stringValue==string) =>
        results.find(r=>r.label==string  || r.stringValue==string).get
      /*val value: RDFValue = str2RDFValue(string,base)
      results.collectFirst{
        case r if r==value || r.label==string
      }.get

      if(results.exists(r=>r==value || r.label==value)) value else {
        play.Logger.info(s"$string is not in the valueset")
        value
      }*/
      case ValueStem(stem) =>
        if(string.startsWith(stem.stringValue)) IRI(string.replace(" ","_")) else stem / string.replace(" ","_")

      case ValueType(t) => t match {
        case RDFS.RESOURCE => str2RDFValue(string,base)
        case XSD.IntDatatypeIRI | XSD.IntegerDatatypeIRI =>
          Try(IntLiteral(string.toInt))
            .recover{case result=> play.Logger.info(s"error in parsing Int of $string") ; StringLiteral(string)}.get:RDFValue

        case XSD.DecimalDatatypeIRI | XSD.DoubleDatatypeIRI =>
          Try(DoubleLiteral(string.toDouble))
            .recover{case result=> play.Logger.info(s"error in parsing Double of $string") ; StringLiteral(string)}.get:RDFValue

        case XSD.Date =>
          formats.collectFirst{
            case format if Try(format.parse(string)).isSuccess=>
              val d: Date = format.parse(string)
              //play.api.Logger.info(d.toString)
              new DateLiteral(d)
          }.getOrElse(StringLiteral(string))

        case XSD.StringDatatypeIRI=>StringLiteral(string):RDFValue

        case other if !other.isInstanceOf[ValueSet] => StringLiteral(string):RDFValue

      }
    }
    if(v.isEmpty)  play.Logger.error("DOES NOT WORK WIth"+property.stringValue+" with value = "+string)

    v
  }

  def filterKeys(keys:Set[String],shape:Shape): List[String] = {
    shape.arcRules().collect{
      case arc if arc.name.isInstanceOf[NameTerm] => //keys.contains(arc.name.asInstanceOf[NameTerm].property.stringValue)
        arc.name.asInstanceOf[NameTerm].property }.collect{
      case prop if keys.exists(k=>prop.stringValue.contains(k)) =>prop.stringValue
    }
  }


  protected def testGenesTable(table:String):List[PropertyModel] = {
    val csv = readTSV(table)
    val f = csv.toFrame
    val entrez = Cols(entrezId.stringValue).as[String]
    val fr:Frame[String,String]= f.mapColKeys[String]{  case col=>  annotationPairs(col).stringValue  }.reindex(entrez)
    val colKeys = filterKeys(fr.colKeys,this.evidenceShape)
    fr.rowKeys.map{   case r=>
      val values =  (for{
          c <- colKeys
          cell = fr[String](r,c)
          if cell.isValue
          col = IRI(c.replace(" ","_"))
        } yield (col ,
          parse(col,cell.get,Some(gero))(evidenceShape).get
          )
        ).toSeq
      PropertyModel(gero / r,values:_*)
    }.toList
  }


 def readFromFile(path:String) =  Source.fromFile(Play.getFile(path)).getLines().reduce(_+"\n"+_)

  import framian.csv.CsvFormat._

  def readCSV(str:String): LabeledCsv = Csv.parseString(str,CSV).labeled
  def readTSV(str:String): LabeledCsv = Csv.parseString(str,TSV).labeled



}
