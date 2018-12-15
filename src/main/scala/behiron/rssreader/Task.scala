package behiron.rssreader
import scala.math.Ordering

sealed trait Task {
  def process(entry: RssEntry): RssEntry
}

case class ConvertTask() extends Task {
  protected val REPLACE_FROM = "ユーザベース"
  protected val REPLACE_TO = "UZABASE"
  def process(entry: RssEntry): RssEntry = RssEntry(entry.title,
    entry.body.replaceAll(REPLACE_FROM, REPLACE_TO))
}

case class CutTask() extends Task {
  protected val CUT_TITLE_COUNT = 10
  protected val CUT_BODY_COUNT = 30
  def process(entry: RssEntry): RssEntry = RssEntry(entry.title.take(CUT_TITLE_COUNT),
    entry.body.take(CUT_BODY_COUNT))
}

object TaskOrdering extends Ordering[Task] {
  override def compare(x: Task, y: Task): Int  = {
    /* ConvertTaskはCutTaskより先におこなわなけばならない */
    if (x.isInstanceOf[ConvertTask] && y.isInstanceOf[CutTask]) {
      -1
    } else if (x.isInstanceOf[CutTask] && y.isInstanceOf[ConvertTask]) {
      1
    } else {
      0
    }
  }
}
