package behiron.rssreader
import scala.math.Ordering

sealed trait Task {
  def process(entry: RssEntry): RssEntry
}

case class ConvertTask(val replaceFrom: String, val replaceTo: String) extends Task {
  def process(entry: RssEntry): RssEntry = RssEntry(entry.title,
    entry.body.replaceAll(replaceFrom, replaceTo))
}

case class CutTask(val titleCount: Int, val bodyCount: Int) extends Task {
  def process(entry: RssEntry): RssEntry = RssEntry(entry.title.take(titleCount),
    entry.body.take(bodyCount))
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
