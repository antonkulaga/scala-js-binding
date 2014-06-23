import rx.Rx
import rx.core.Var
import rx.extensions._
import utest._
import utest.framework.TestSuite

object CollectionWatcher extends TestSuite{

  def tests = TestSuite{

    "collection updater"- {


      "handles remove, insert and swaps well" - {

        val l = List("one", "two", "three")

        val list: Var[List[String]] = Var(l)
        val w = Watcher(list)
        val u: Rx[CollectionUpdate[String]] = list.updates
        assert(w.previous == l)

        list() = List("zero","one", "two", "three")

        assert(u.now.inserted == List("zero"))
        assert(u.now.removed == List.empty)
        assert(u.now.moved == List.empty)


        list() = list.now.filterNot(_ == "two")
        assert(list.now == List("zero", "one", "three"))

        assert(u.now.inserted == List.empty)
        assert(u.now.removed == List("two"))
        assert(u.now.moved == List.empty)
        list() = List("three","zero","one")

        assert(u.now.inserted ==List.empty)
        assert(u.now.removed ==List.empty)
        assert(u.now.moved.size==3)

      }



    }

  }

}

