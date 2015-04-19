package controllers.genes

import java.text.SimpleDateFormat

import com.github.marklister.collections._
import com.github.marklister.collections.io.CsvParser
import org.denigma.semweb.rdf._
import org.denigma.semweb.rdf.vocabulary.{RDFS, XSD}
import org.denigma.semweb.shex._

import scala.collection.immutable._
import scala.util.Try

trait LoadGenAge extends GeneSchema{

  lazy val formats:List[SimpleDateFormat] = List(
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
    //val str = this.readFromFile(fileName)
    testGenesTable(fileName)
  }


  /**
   * Adds prefixes
   * @param str
   * @return
   */
  protected def resolve(str:String): String = prefixes.collectFirst{
    case (key,value) if str.startsWith(key+":")=>str.replace(key+":",value)
  }.getOrElse(str)

  def str2RDFValue(string:String,base:Option[IRI] = None):RDFValue = resolve(string) match
  {
    case str if str.contains("_:") => BlankNode(string.replace(" ","_"))
    case str if str.contains(":") =>  IRI(this.resolve(string.replace(" ","_")))
    case other=> base.fold[RDFValue](StringLiteral(string))(b=>b / string.replace(" ","_"))
  }
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

        case RDFS.RESOURCE =>
          str2RDFValue(string,base)

        case XSD.IntDatatypeIRI | XSD.IntegerDatatypeIRI =>
          Try(IntLiteral(string.toInt))
            .recover{case result=> play.Logger.info(s"error in parsing Int of $string") ; StringLiteral(string)}.get:RDFValue

        case XSD.DecimalDatatypeIRI | XSD.DoubleDatatypeIRI =>
          Try(DoubleLiteral(string.toDouble))
            .recover{case result=> play.Logger.info(s"error in parsing Double of $string") ; StringLiteral(string)}.get:RDFValue

        case XSD.Date =>
          DateTimeFormats.parseDate(string).map{
            case dt=>
              DateLiteral(dt)
          }.getOrElse{
            play.api.Logger.error("cannot parse following date: "+string)
            StringLiteral(string)
          }

        case XSD.StringDatatypeIRI=>StringLiteral(string):RDFValue

        case other if !other.isInstanceOf[ValueSet] => str2RDFValue(string)

      }
    }
    if(v.isEmpty)  play.Logger.error("DOES NOT WORK WITH"+property.stringValue+" with value = "+string)

    v
  }

  /**
   * Keeps only those columns that are in shapes
   * @param headers
   * @param shape
   * @return
   */
  def filteredCols(headers:Map[String,Int],shape:Shape): Map[String,Int] = {
    val arcs  = shape.arcRules()
    for{
      (key,value) <-headers
      if shape.arcRules().exists{
        case arc=>arc.name.isInstanceOf[NameTerm] &&
          arc.name.asInstanceOf[NameTerm].property.stringValue.contains(key)
      }
    } yield (key,value)
  }

  protected def testGenesTable(fileName:String, sep:Char='|'):List[PropertyModel] = {

    val p= CsvParser[
      String,String,String,String,
      String,String,String,String,
      String,String,String,String,
      String,String
      ]

    val parsed: CollSeq14[String, String, String, String, String,
      String, String, String, String, String,
      String, String, String, String] = p.parseFile(fileName,"\t")

    val (headers,data) = (parsed.head,parsed.tail)
    val cols = headers.productIterator.map(h=>annotationPairs(h.toString).stringValue).toSeq.zipWithIndex.toMap
    val colKeys = filteredCols(cols,this.evidenceShape) //only those cols that are in shape
    //play.api.Logger.info(colKeys.toSeq.mkString("\t"))
    val idNum: Int = cols(entrezId.stringValue)
    //play.api.Logger.info(s"ID NUM = ${idNum}")

    cols.find(kv=>kv._1==entrezId.stringValue) match {
      case Some(ind)=>
        val result = (for{ row <- data  } yield
        {
          val lines = for{
          (key,num) <- colKeys
          col = IRI(this.resolve(key.replace(" ","_")))
          cell = row.productElement(num).toString
          if cell!=""
          cells = cell.split(sep)
          } yield col->cells.map(c=>parse(col,c,Some(gero))(evidenceShape).get ).toSet
          val id = entrez /  row.productElement(idNum).toString
          PropertyModel(id, lines.toMap)
        } ).toList
        //play.api.Logger.error("RESULT = "+result.mkString("\n"))
        result.toList
      case None=>
        play.api.Logger.error(s"cannot find entrezId in${cols}")
        List.empty[PropertyModel]
    }

/*

    val csv = readTSV(table)
    val f = csv.toFrame
    val ent = Cols(entrezId.stringValue).as[String]
    val fr:Frame[String,String]= f.mapColKeys[String]{  case col=>  annotationPairs(col).stringValue  }.reindex(ent)
    val colKeys = filterKeys(fr.colKeys,this.evidenceShape)
    fr.rowKeys.map{
      case r=>
      val values: Map[IRI, Set[RDFValue]] =  (for{
          c <- colKeys
          cell = fr[String](r,c)
          if cell.isValue
          col = IRI(this.resolve(c.replace(" ","_")))
        } yield col->cell.get.split(sep).map(c=>parse(col,c,Some(gero))(evidenceShape).get ).toSet
        ).toMap
    PropertyModel(entrez / r,values)
    }.toList*/
  }


 def readFromFile(path:String): String = ""// Source.fromFile(Play.getFile(path)).getLines().reduce(_+"\n"+_)

/*
  import framian.csv.CsvFormat._

  def readCSV(str:String): LabeledCsv = Csv.parseString(str,CSV).labeled
  def readTSV(str:String): LabeledCsv = Csv.parseString(str,TSV).labeled
*/



}
