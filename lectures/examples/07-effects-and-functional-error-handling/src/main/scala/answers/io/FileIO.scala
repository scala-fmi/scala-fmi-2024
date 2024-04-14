package answers.io

import scala.io.Source

object FileIO:
  def readFileLines(file: String): IO[List[String]] = IO:
    val source = Source.fromFile(file)
    try source.getLines().toList
    finally source.close()
