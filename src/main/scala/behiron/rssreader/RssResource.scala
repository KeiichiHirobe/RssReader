package behiron.rssreader
import com.rometools.rome.feed.synd.SyndEntry

sealed trait RssResource {
  /* title, bodyのどちらかが取得できない場合はそのレコードは除かずにNoneを返す */
  def getEntries() : Seq[Option[RssEntry]]
}

object RssResource {
  /* 改行はエスケープする */
  def cleansingText(orig: String): String = orig.replaceAllLiterally("\n", "\\n")
}

case class RssTextResource(val contents: Seq[String]) extends RssResource {

  private val entries = {
    var ret: Seq[Option[RssEntry]] = Seq.empty[Option[RssEntry]]
    ret = contents.foldLeft(Nil: List[Option[RssEntry]])((list, line) => {
      getTitleAndBody(line) match {
        case Some((title, body)) => Some(RssEntry(title, body)) :: list
        case _                   => None :: list
      }
    }).reverse
    ret
  }

  def getEntries(): Seq[Option[RssEntry]] = entries

  /* 一行に「title:~ body:~」という形式でレコードがあることを想定 */
  protected def getTitleAndBody(record: String): Option[(String, String)] = {
    val recordFormat = """\s*title:\s*(.+?)\s*body:\s*(.+?)\s*""".r
    record match {
      case recordFormat(title, body) => Some((title, body))
      case _                         => None
    }
  }
}

case class RssURLResource(val syndEntries: Seq[SyndEntry]) extends RssResource {

  private val entries = syndEntries.map(entry => {
      for {
        title <- getTitle(entry)
        rawBody <- getBody(entry)
      } yield RssEntry(title, cleansingBody(rawBody))
    }
  )

  def getEntries(): Seq[Option[RssEntry]] = entries

  /* bodyがdescription以外にある場合はoverrideしてもらう */
  protected def getBody(entry: SyndEntry): Option[String] = {
    // null if none
    val syndBody = entry.getDescription
    if (syndBody == null) None else { Option(RssResource.cleansingText(syndBody.getValue)) }
  }

  /* titleも念のためoverride可能にしておく */
  protected def getTitle(entry: SyndEntry): Option[String] = Option(RssResource.cleansingText(entry.getTitle))
  /**
   * デフォルト実装ではタグを除く
   * 正規表現の判断は荒いことに注意 参考:https://www.ipentec.com/document/regularexpression-html-tag-detect
   */
  protected def cleansingBody(body: String): String = body.replaceAll("""<.*?>""", "")
}
