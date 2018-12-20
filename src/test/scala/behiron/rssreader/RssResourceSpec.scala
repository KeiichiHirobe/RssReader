package behiron.rssreader

import org.scalatest.{DiagrammedAssertions, FunSpec}

class RssResorceSpec extends FunSpec with DiagrammedAssertions {
  describe("RssTextResource") {
    it("valid record parsed correctly") {
      val contents = List(
        "title:aaaaaa   body:bbbbb",
        "     title:aaaaaa   body:bbbbb",
        "title:   aaaaaa   body:bbbbb",
        "title:aaaaaa       body:bbbbb",
        "title:aaaaaa   body:    bbbbb",
        "title:aaaaaa   body:bbbbb     ",
      )
      val resource = RssTextResource(contents)
      assert(resource.getEntries.forall(entry =>
        entry.get.title == "aaaaaa" && entry.get.body == "bbbbb"
      ))
    }
    it("invalid record parsed as None") {
      val contents = List(
        "title aaaaaa   body:bbbbb",
        "title:aaaaaa   body  bbbbb",
        "title:aaaaaa   body:",
        "body:bbbbb   title:aaaaa",
        "hoge:aaaaaa   body:bbbbb",
      )
      val resource = RssTextResource(contents)
      assert(resource.getEntries.forall(entry =>
        entry == None
      ))
    }
  }
}
