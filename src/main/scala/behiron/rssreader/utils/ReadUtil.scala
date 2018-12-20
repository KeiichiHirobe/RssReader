package behiron.rssreader.utils
import scala.io.Source
import java.net.URL
import com.rometools.rome.feed.synd.{SyndFeed, SyndEntry}
import com.rometools.rome.io.SyndFeedInput
import com.rometools.rome.io.XmlReader
import scala.collection.JavaConverters._

object ReadUtil {
  def readFile(pathName: String): Seq[String] = {
    var ret: Seq[String] = Seq.empty[String]
    LoanPattern.using(Source.fromFile(pathName)) { s =>
      ret = s.getLines.toList
    }
    ret
  }

  def readRssURL(url: String): Seq[SyndEntry] = {
    //  FileNotExistException, ParseExceptionなどの例外等特にキャッチしない
    val feedUrl = new URL(url)
    val input = new SyndFeedInput
    val feed: SyndFeed = input.build(new XmlReader(feedUrl))
    asScalaBuffer(feed.getEntries).toVector
  }
}
