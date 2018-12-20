package behiron.rssreader

/* title, bodyともに改行を含まないという制約がある */
class RssEntry(
  val title: String, val body: String) {

  def dump(): String = {
    s"title: ${title} body: ${body}"
  }
}

object RssEntry {
  /* title, bodyともに改行を含まないという契約を守っているかチェック */
  def apply(title: String, body: String): RssEntry = {
    if (title.contains("\n") || body.contains("\n")) {
      throw new Exception("Invalid argument of RssEntry constructor")
    } else {
      new RssEntry(title, body)
    }
  }
}
