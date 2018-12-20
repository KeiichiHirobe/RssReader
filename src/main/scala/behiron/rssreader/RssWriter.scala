package behiron.rssreader
import behiron.rssreader.utils.LoanPattern.using
import java.io._

sealed trait RssWriter {
  def write(s: String): Unit
}

case class StdOutRssWriter() extends RssWriter {
  def write(s: String): Unit = print(s)
}

case class FileRssWriter(val path: String) extends RssWriter {
  def write(s: String): Unit = {
      using(new PrintWriter(new File(path))) {
        _.write(s)
      }
  }
}
