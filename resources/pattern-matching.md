# Pattern Matching (Съпоставяне по образци)

## Възможни pattern-и

* Прости образци
  * всичко – `_`
     
    ```scala 3
    option match
      case Some(x) => x
      case _ => alternative // matches everything else
    ```
  * даване на име – `x`, `person` (всеки идентификатор, започващ с малка буква)
  
    ```scala 3
    list match
      case Nil => "An empty list"
      case nonEmptyList => s"A list with ${nonEmptyList.size} elements"
    ```
  * съпоставяне по тип – `s: String` (придава и име) или `_: Int` (проверява типа, но не деклалира променлива)
  
    ```scala 3
    value match
      case n: Int => n
      case s: String => s.toInt
      case d: Double => d.toInt
    ```
  * съпоставяне по константа/по обект – произволен литерал: `42`, `"abcdef"` и т.н., или константа: `Pi`, `NumberOfRetries`, `Nil` (т.е. идентификатор, започващ с голяма буква). Обикновена променлива може да се използва за константа, ако се загради в "\`\`": `` `localVariable` `` (в противен случай ще се счете за даване на име и ще въведе нова променлива).
    
    ```scala 3
    val perfectNumbers = List(6, 28, 496, 8128)
    
    list match
      case Nil => "Empty list"
      case `perfectNumbers` => "A list of perfect numbers"
      case List(math.Pi) => "A list of Pi"
      case List(42) => "A list of 42"
      case _ => "Some boring list"
    ```
* Съставни образци – деструктурират (разбиват) обект на части, като за всяка част също се посочва образец, който да се приложи на нея (който също може да бъде прост или съставен)
  * наредени n-торки – `(pattern1, pattern2, pattern3, pattern4)`
  
    ```scala 3
    (xs, ys) match
      case (Nil, Nil) => "Both lists are empty"
      case (Nil, y :: _) => s"The ys list has at least one element: $y"
      case _ => "The xs list is not empty"
    ```
    
    ```scala 3
    val (numer, denom) =
      val div = gcd(n, d)
      ((n / div) * d.sign, (d / div).abs)
    
    // We destructured the tuple into the variables number and denom
    ```
  * case class-ове – `Person(pattern1, pattern2, pattern3)`
    
    ```scala 3
    person match
      case Person("Boyan", age, location) => $"Boyan is $age years old and is from $location"
      case Person(name, UltimateAnswer, _) => s"A 42 years young person: $name"
      case Person(name, 34, _) => s"A 34 years young person: $name"
      case Person(name, _, _) => s"Someone: $name"
    ```
  * списъци – `head :: restOfTheList`
    
    ```scala 3
    elements match
      case first :: second :: rest =>
        s"The first element is $first, the second is $second and there are ${rest.size} more elements"
      case Nil => "The list is empty"
      case _ => "There is just one element"
    ```
  * колекции – `Seq(first, second, third)` или `Seq(first, second, rest*)`

    ```scala 3
    "2022-04-01-hello-there".split("-") match
      case Array(year, month, day) => s"Year: $year, month: $month, day: $day"
      case Array(year, month, day, rest*) =>
        s"Year: $year, month: $month, day: $day and some more elements: $rest"
      case _ => "Not enough elements"
    ```
  * **Пояснение:** всички съставни образци с фиксиран брой елементи са имплементирани чрез метод `unapply` на обекта/придружаващия обект, с който се match-ва, а тези с променлив брой елементи – чрез метод `unapplySeq`. Обектите с метод `unapply` или `unapplySeq` се наричат "екстрактори" и включват всички tuple-и, придружаващите обекти на case class-овете (за тях автоматично се генерира `unapply`), колекциите, деструктурирането на списъци чрез `::`, които описахме по-горе, но може също така да е и всеки произволен от нас обект. Вижте по-надолу секцията за екстрактори.
* Комбинация от име и съставен образец – `person @ Person(name, age, _)`. В ляво посочваме името, а в дясно образец за съпостяване:
  
  ```scala 3
  figure match
    case c @ Circle(radius) => s"Circle $c has radius $radius"
    case r @ Rectangle(a, b) => s"Rectangle $r has sides $a and $b"
  ```
  
  ```scala 3
  figures match
    case c @ Circle(radius) :: r @ Rectangle(a, b) :: rest =>
      s"First figure is a circle with radius $radius, second is a rectangle with sides $a and $b and there are ${rest.size} more figure"
  ```
* Алтернативи – `pattern1 | pattern2 | ...`
  
  ```scala 3
  boolean match
    case "true" | "True" | "TRUE" => true
    case "false" | "False" | "FALSE" => false
    case _ => false
  ```
  
* Guard-ове – `pattern if condition`

  ```scala 3
  person match
    case Person(name, age) if age < 30 => s"Young $name"
    case Person(name, _) => s"Old $name"
  ```

  ```scala 3
  val reciprocal: PartialFunction[Int,Double] =
    case x if x != 0 => 1.toDouble / x
  ```

## Къде можем да използваме pattern matching

* В `match` конструкции, използвайки `case` блок
  
  ```scala 3
  def toInteger(value: Int | String | Double): Int =
    value match
      case n: Int => n
      case s: String => s.toInt
      case d: Double => d.toInt
  ```
* В изрази на места, където се очаква стойност от тип `Function1[A, B]` или `PartialFunction[A, B]`

  ```scala 3
  Map(1 -> "one", 2 -> "two").map {
    case (number, letters) => s"$number: $letters"
  }
  // List("1: one", "2: two")
  ```
  
  ```scala 3
  List(Some(1), None, Some(42)).collect {
    case Some(n) => n * n
  }
  // List(1, 1764)
  ```
* При `val` дефиниции

  ```scala 3
  val person @ Person(name, age) = findPerson("id-123")
  val personDescription = s"$name, who is $age years old"
  ```
  
* Ако Scala компилаторът не може да гарантира, че образецът вляво винаги пасва на всяка стойност от типа вдясно, то ще изведе warning:

  ```scala 3
  scala> val first :: rest = List(1, 2, 3, 4)
  1 warning found
  -- Warning: --------------------------------------------------------------------
  1 |val first :: rest = List(1, 2, 3, 4)
  |                    ^^^^^^^^^^^^^^^^
          |pattern's type ::[Int] is more specialized than the right hand side expression's type List[Int]
  |
  |If the narrowing is intentional, this can be communicated by adding `: @unchecked` after the expression,
  |which may result in a MatchError at runtime.
  val first: Int = 1
  val rest: List[Int] = List(2, 3, 4)
  ```

* При `for` в лявата част на неговите генератори и дефиниции

  ```scala 3
  val list1 = List(1, 2, 3, 4, 5)
  val list2 = List(10, 20, 30, 40, 50)
  val list3 = List(100, 200)
  
  for
    (a, b) <- list1 zip list2
    c <- list3
  yield a + b + c
  // List(111, 211, 122, 222, 133, 233, 144, 244, 155, 255)
  ```
  
  От Scala 3.4 е задължително образът вляво да е такъв, за който компилаторът да може да гарантира, че ще пасне при всички случаи.
  
  Алтернативно може да добавим думата `case` през образеца. Тогава ще се филтрира само елементите, които го match-ват:
  
  ```scala 3
  val people = List(("Zdravko", 35), ("Boyan", 4), ("Viktor", 30), ("Taylor", 35))
  
  for
    case (name, 35) <- people
  yield s"$name is 35"
  // List("Zdravko is 35", "Taylor is 35")
  ```
  
  `case` изисква обектът, участващ в for-comprehension (в случая списъкът) да има метод `withFilter` или `filter`.
* В try/catch блок за прихващане на изключения
  
  ```scala 3
  val parsedResult =
    try "42L".toInt
    catch
      case e: NumberFormatException => 0
  ```
  
  По-сложен пример би могъл да се структурира така:

  ```scala 3
  try doSomething()
  catch
    case e: Exception1 => alternative1
    // this catches more than one exception
    case _: Exception1 | _: Exception2 => alternative2
    // this is using a special extractor that ignores all exceptions that
    // generally shouldn't be caught, like VirtualMachineError, InterruptedException and others.
    // see https://scala-lang.org/api/3.x/scala/util/control/NonFatal$.html# for details
    case NonFatal(e) => alternative3
  ```

## Екстрактори

Екстракторите са обекти, които имат методи `unapply` или `unapplySeq` и позволяват деструктурирането на части на обекти от определен тип.

### Фиксиран брой елементи – чрез `unapply`:

```scala 3
object Email:
  def unapply(email: String): Option[(String, String)] = email.split("@", -1) match
    case Array(name, domain) => Some((name, domain))
    case _ => None

List("zdravko@gmail.com", "boyan@stemma.@", "viktor@uni-sofia.bg", "vas@sil@abv.bg", "yahoo.com")
  .collect {
    case Email(name, _) => s"$name's email"
  }
// List("zdravko's email", "viktor's email")
```

`unapply` метод бива генериран автоматично за всички case class-ове, благодарение на което всички те могат да бъдат използвани в pattern matching.

### Променлив брой елементи – чрез `unapplySeq`

```scala 3
object Words:
  def unapplySeq(str: String): Option[Seq[String]] =
    Some(str.split(" ").toSeq)

val phrase = "the quick brown fox"
val Words(first, second, _*) = phrase

s"$first-$second" // "the-quick"

"the quick brown fox" match
  case Words(first, second) => s"Exactly two words: $first, $second"
  case Words(_, _, rest*) => s"More than two words, the rest are: $rest"
```

```scala 3
import scala.util.matching.Regex
val ISODate = new Regex("""(\d{4})-(\d{2})-(\d{2})""")
val ISODate(year, month, day) = "2022-04-13"
```

### Любопитно: как работи `::`

Да разгледаме:

```scala 3
def quickSort(xs: List[Int]): List[Int] = xs match
  case Nil => Nil
  case x :: rest =>
    val (smaller, larger) = rest.partition(_ < x)
    quickSort(smaller) ::: (x :: quickSort(larger))
```

Знаем, че при `x :: quickSort(larger) `символът `::` е метод на `List`. При case `x :: rest` символът `::` изглежда като специален синтаксис от Scala. Но това не е така, `::`  всъщност е имлементиран в библиотеката на Scala и всеки би могъл да си добави подобна операция при pattern matching. Начинът, по който функционира, е следният. `List` в Scala e дефиниран по следния начин:

```scala 3
sealed trait List[+A]
case class ::[+A](head: A, next: List[A]) extends List[A]
case object Nil extends List[Nothing]
```

Забележете, че Cons класът се казва `::` , като той е case class и това значи, че има `unapply` метод. Това значи, че в Pattern Matching можем да пишем:

```scala 3
def quickSort(xs: List[Int]): List[Int] = xs match
  case Nil => Nil
  case ::(x, rest) =>
    val (smaller, larger) = rest.partition(_ < x)
    quickSort(smaller) ::: (x :: quickSort(larger))
```

Точно както при методите обаче, като всичко друго с двама участници, Scala ни позволява да напишем горното и инфиксно, с което получаваме познатия ни синтаксис: `x :: rest`.
