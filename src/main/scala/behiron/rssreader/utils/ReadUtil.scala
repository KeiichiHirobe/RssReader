package behiron.rssreader.utils
import scala.io.Source

object ReadUtil {
  def readFile(pathName: String): Seq[String] = {
    var ret: Seq[String] = Seq.empty[String]
    LoanPattern.using(Source.fromFile(pathName)) { s =>
      ret = s.getLines.toList
    }
    ret
  }
}
