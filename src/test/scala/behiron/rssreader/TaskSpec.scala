package behiron.rssreader

import org.scalatest.{DiagrammedAssertions, FunSpec}

class TaskSpec extends FunSpec with DiagrammedAssertions {
  val entry = RssEntry(
"お試し就職制度を導入した話と、導入するに至るまでの話",
"ユーザベースに入社して約2ヶ月しか経ってませんが、ユーザベースユーザベースこの技術ブログに..."
  )

  describe("Task") {
    it("convert") {
      val done = ConvertTask("ユーザベース", "UZABASE").process(entry)
      assert(entry.title == done.title)
      assert(done.body === "UZABASEに入社して約2ヶ月しか経ってませんが、UZABASEUZABASEこの技術ブログに..."
)
    }
    it("cut") {
      val done = CutTask(10, 30).process(entry)
      assert(done.title == "お試し就職制度を導入")
      assert(done.body == "ユーザベースに入社して約2ヶ月しか経ってませんが、ユーザベー")
    }
    it("convert must be done first") {
      val taskList1 = List(CutTask(10, 30), ConvertTask("ユーザベース", "UZABASE"))
      val taskList2 = List(ConvertTask("ユーザベース", "UZABASE"), CutTask(10, 30))

      assert((taskList1.sorted(TaskOrdering))(0) == taskList1(1))
      assert((taskList2.sorted(TaskOrdering))(0) == taskList2(0))
    }
  }
}
