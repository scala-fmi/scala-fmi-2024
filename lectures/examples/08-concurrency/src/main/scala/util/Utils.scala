package util

object Utils:
  def time[T](name: String)(operation: => T): T =
    val startTime = System.currentTimeMillis()

    val result = operation

    println(s"$name took ${System.currentTimeMillis - startTime} millis")

    result

  def doWork = (1 to 10000000).map(math.pow(2, _)).toList
  def doMoreWork = (1 to 10000000).map(math.pow(3, _)).toList
