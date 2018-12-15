package behiron.rssreader
import java.net.URL
import com.rometools.rome.feed.synd.{SyndFeed, SyndEntry}
import com.rometools.rome.io.SyndFeedInput
import com.rometools.rome.io.XmlReader
import scala.collection.JavaConverters._
import behiron.rssreader.utils.LoanPattern.using
import scala.io.Source


sealed trait RssResource {
  /* title, bodyのどちらかが取得できない場合はそのレコードは除かずにNoneを返す */
  def getEntries() : Seq[Option[RssEntry]]
}

object RssResource {
  /* 改行はエスケープする */
  def cleansingText(orig: String): String = orig.replaceAllLiterally("\n", "\\n")
}

case class RssTextResource(val pathName: String) extends RssResource {

  def getEntries(): Seq[Option[RssEntry]] = {
    var ret: Seq[Option[RssEntry]] = Seq.empty[Option[RssEntry]]
    using(Source.fromFile(pathName)) { s =>
      ret = s.getLines.foldLeft(Nil: List[Option[RssEntry]])((list, line) => {
        getTitleAndBody(line) match {
          case Some((title, body)) => Some(RssEntry(title, body)) :: list
          case _                   => None :: list
        }
      }).reverse
    }
    ret
  }
  /* 一行に「title:~ body:~」という形式でレコードがあることを想定 */
  protected def getTitleAndBody(record: String): Option[(String, String)] = {
    val recordFormat = """\s*title:\s*(.+?)\s*body:\s*(.+?)\s*""".r
    record match {
      case recordFormat(title, body) => Some((title, body))
      case _                         => None
    }
  }
}

case class RssURLResource(val url: String) extends RssResource {
  def getEntries(): Seq[Option[RssEntry]] = {

    //  FileNotExistException, ParseExceptionなどの例外等特にキャッチしない
    val feedUrl = new URL(url)
    val input = new SyndFeedInput
    val feed: SyndFeed = input.build(new XmlReader(feedUrl))
    val entries = asScalaBuffer(feed.getEntries).toVector

    entries.map(entry => {
        for {
          title <- getTitle(entry)
          rawBody <- getBody(entry)
        } yield RssEntry(title, cleansingBody(rawBody))
      }
    )
  }

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
