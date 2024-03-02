def toInteger(value: Any): Int =
  value match
    case n: Int => n
    case s: String => s.toInt
    case d: Double => d.toInt

toInteger("42") // 42
toInteger(10.5) // 42
toInteger(2) // 42
toInteger("Hello world")
toInteger(List.empty)

val parsedResult =
  try "42L".toInt
  catch
    case e: NumberFormatException => 0
    case e: IllegalArgumentException => -1

parsedResult

(1 to 4).toList

for
  i <- 1 to 4 // генератор
  if i % 2 == 0 // филтър
  c <- 'a' to 'c' // генератор
  s = s"$i$c" // дефиниция
do
  val newValue = i + c + s
  println(newValue)
  println(s)

val tuple = ("Ivan", 27, "Sofia")
tuple._2
tuple._3

val vivi = Set(10, 11, 12)
vivi + 13
vivi(11) // true
vivi(20)
vivi(13)

Map(-1 -> "Three", 4 -> "Three", 3 -> "Three", (1, "One"), 2 -> "Two").head
2 -> "Two"

1 +: List(2, 3, 4)

val result = for
  x <- List(1, 2, 3, 4, 5)
  if x % 2 != 0
  y <- List(x, x * 2, x * 3)
yield
  val result = s"$x: $y"
  s"Result $result"

result
