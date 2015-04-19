//import org.denigma.semweb.rdf.vocabulary._
//import org.denigma.semweb.rdf.{IRI, StringLiteral, Trip}
//import org.denigma.semweb.sparql._
//import org.specs2.mutable._
//import play.api.test.WithApplication
//import rx._
//import rx.core.Var
//import rx.extensions._
//import utest._
//
//import scala.concurrent.Future
//import scala.util.Try


//class CollectionSpec extends Specification {
//
//  "Collection watcher" should {
//
//    "update when elementes are removed or added" in     {
//      val l = List("one", "two", "three")
//
//      val list: Var[List[String]] = Var(l)
//      val w = Watcher(list)
//      val u: Rx[CollectionUpdate[String]] = list.updates
//      assert(w.previous == l)
//
//      list() = "zero" :: list.now
//
//
//      assert(u.now.inserted == List("zero"))
//      assert(u.now.removed == List.empty)
//      assert(u.now.moved == List.empty)
//
//      list() = list.now.filterNot(_ == "two")
//      assert(list.now == List("zero", "one", "three"))
//
//      assert(u.now.inserted == List.empty)
//      assert(u.now.removed == List("two"))
//      assert(u.now.moved == List.empty)
//      true
//    }
//    "fail when we ask it to do so" in {
//      assert(true==false)
//      true
//    }
//
//
//  }
//}