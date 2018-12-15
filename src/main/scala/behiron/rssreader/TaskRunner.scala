package behiron.rssreader

object TaskRunner {
  /* outputFileは標準出力の時はNone */
  def taskRun(resource: RssResource, tasks: Seq[Task], writer: RssWriter) = {
    var invalidRecordCount: Int = 0
    val processedEntries: Seq[Option[RssEntry]] = process(resource, tasks)
    val filteredEntries: Seq[RssEntry] = processedEntries.foldLeft(Nil: List[RssEntry]) ((list, entry) => {
      entry match {
        case Some(e) =>       e::list
        case None =>          invalidRecordCount += 1
                              list
      }
    }).reverse

    writer.write(filteredEntries.map(_.dump).mkString("\n") + "\n")

    if (invalidRecordCount > 0) {
      System.err.println(
        s"Detected ${invalidRecordCount} invalid records"
      )
    }
  }

  def process(resource: RssResource, tasks: Seq[Task]): Seq[Option[RssEntry]] = {
    val sortedTasks = tasks.sorted(TaskOrdering)
    resource.getEntries.map(entry => entry match {
      case Some(original)
                        => Some(sortedTasks.foldLeft(original)((e, task) => task.process(e)))
      case None
                        => None
    })
  }
}
