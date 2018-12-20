package behiron.rssreader
import behiron.rssreader.utils.ReadUtil

object RssReader {

  type OptionMap = Map[Symbol, Any]
  val REPLACE_FROM = "ユーザベース"
  val REPLACE_TO = "UZABASE"
  val CUT_TITLE_COUNT = 10
  val CUT_BODY_COUNT = 30

  def printUsage(): Unit = {
    val usage = """
    Usage: RssReader [--cut] [--convert] [ -o outputFile | --output outputFile] resourceName
  """
    println(usage)
  }

  def main(args: Array[String]): Unit = {
    val options: OptionMap = getOpt(args)
    var taskList: List[Task] = List.empty[Task]
    if (options('help).asInstanceOf[Boolean]) {
      printUsage
      sys.exit(0)
    }

    val resource: RssResource = options('input) match {
      case Some(v) if v.asInstanceOf[String].matches("^http.+") => RssURLResource(ReadUtil.readRssURL(v.asInstanceOf[String]))
      case Some(v) => RssTextResource(ReadUtil.readFile(v.asInstanceOf[String]))
      case _       => throw new Exception("resourceName not specified")
    }
    val writer: RssWriter = options('output) match {
      case Some(v) => FileRssWriter(v.asInstanceOf[String])
      case _       => StdOutRssWriter()
    }
    if (options('cut).asInstanceOf[Boolean]) taskList = CutTask(CUT_TITLE_COUNT, CUT_BODY_COUNT) :: taskList
    if (options('convert).asInstanceOf[Boolean]) taskList = ConvertTask(REPLACE_FROM, REPLACE_TO) :: taskList

    TaskRunner.taskRun(resource, taskList, writer)
  }

  def getOpt(args: Array[String]): OptionMap = {
    val defaults: OptionMap =  Map(
      'help -> false,
      'cut -> false,
      'convert -> false,
      'input -> None,
      'output -> None
    )

    def nextOption(map : OptionMap, list: List[String]) : OptionMap = {
      list match {
        case Nil => map
        case "--cut" :: rest =>
                                nextOption(map ++ Map('cut -> true), rest)
        case "--convert" :: rest =>
                                nextOption(map ++ Map('convert -> true), rest)
        case ("-o" | "--output") :: output :: rest =>
                                nextOption(map ++ Map('output -> Some(output)), rest)
        case ("-o" | "--output") ::  Nil =>
                                throw new Exception("Option [-o|--output] requires an argument")
        case ("-h" | "--help") :: Nil =>
                                nextOption(map ++ Map('help -> true), list.tail)
        case input :: rest =>
                                nextOption(map ++ Map('input -> Some(input)), rest)
      }
    }

    if (args.length == 0) {
      defaults ++ Map('help -> true)
    } else {
      defaults ++  nextOption(Map(), args.toList)
    }
  }
}
