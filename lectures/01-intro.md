---
title: Scala 2024 (tm)
---

# Функционално програмиране за напреднали със Scala

2023/2024

![](images/scala-logo.png)

# Днес :)

![](images/01-intro/pretending-to-write.gif){ height=180 }

::: incremental

Неща които ще чуете днес...

* Кои сме ние?
* Административни неща за курса
* Накратко защо въобще функционално?
* Има ли причина да уча Скала 2024-а година, човек?
* Kакво ще учим?
* Ресурси
* Малко примерен код

:::

# Ние?

<ul style="display: grid; grid-template-columns: repeat(2, 1fr); list-style: none">
<li>[![](images/01-intro/zdravko.jpg){ height=100 style="vertical-align: middle; border-radius: 50%" } Здравко Стойчев](https://www.linkedin.com/in/zdravko-stoychev-46414758/)</li>
<li>[![](images/01-intro/boyan.jpg){ height=100 style="vertical-align: middle; border-radius: 50%" } Боян Бонев](https://www.linkedin.com/in/boyan-bonev-ams/)</li>
<li>[![](images/01-intro/vassil.jpg){ height=100 style="vertical-align: middle; border-radius: 50%" } Васил Дичев](https://www.linkedin.com/in/vdichev/)</li>
<li>[![](images/01-intro/viktor.jpg){ height=100 style="vertical-align: middle; border-radius: 50%" } Виктор Маринов](https://www.linkedin.com/in/viktor-marinov-07374512b/)</li>
</ul>

...може би и други!

# Сбирки и админастративни неща

::: incremental

* Понеделник и сряда от 18:15 до 20:00
  * в зали 02 и 01 съответно
  * От тази година 4 часа лекции
  * 1 час семинари?
* Изцяло на място
  * Но ще опитаме да правим записи
  * За да се получи курсът е нужно и вашето участие
* Slack – най-лесно може да ни намерите там <- новини и комуникация.
* Материали в GitHub: [https://github.com/scala-fmi/scala-fmi-2024](https://github.com/scala-fmi/scala-fmi-2024)
* Домашни и финален проект – GitHub Classroom

:::

# Оценяване?

::: { .fragment }

* Домашни през семестъра (около 5): 50 точки
* Финален проект: 50 точки
* Бонус точки
* Общо: 100+
* Ще следим за стил (важен за цялостната оценка)

:::

::: { .fragment }

**Скала** за оценяване (pun intended):

+--------+----------+
| Оценка | Точки    |
+--------+----------+
| 6      | ≥ 82     |
+--------+----------+
| 5      | 70--81   |
+--------+----------+
| 4      | 58--69   |
+--------+----------+
| 3      | 46--57   |
+--------+----------+
| 2      | \< 46    | 
+--------+----------+

:::

# Функционално? Кратка история

* 1930–1940 – Ламбда смятане от Алонсо Чърч
  - модел на изчисление, базиран на композиция на анонимни функции
* 1958 – LISP от Джон МакКартни – първи език за ФП
  - Повлиян от математиката
  - Рекурсия като цел
  - Въвежда garbage collection
  - Кодът е данни
* 1973 – ML от Робин Милнър
  - статично типизиране
  - type inference. Типова система на Хиндли-Милнър
  - Параметричен полиморфизъм (Generics)
  - Pattern matching
* 1980-те – допълнително развитие, Standard ML, Miranda (lazy evaluation)

# Функционално? Кратка история

* 1986 – Erlang от Джо Армстронг и Ериксън
  - фокус на телекоми и толерантни на грешки дистрибутирани системи
* 1990 – Haskell, отворен стандард за ФП език
  - абстракция чрез type classes
  - контролиран монаден вход/изход
* 2000-те – Scala от Мартин Одерски (2004-та), Clojure от Рич Хики (2007-ма)
  - Неизменими структури от данни
  - Средства за конкурентни и дистрибутирани системи
  - Комбинация на практики от различни езици (напр. ООП + ФП в Scala)
* Края на 2000-те до наши дни – ФП елементи се появяват в почти всеки език. Защо?

# Мартин Одерски


<div style="width: 45%; float: left"><img src="images/01-intro/odersky.JPG" /></div>

<div style="width: 50%; float: right">

* Професор в EPFL, Лозана
* Възхитен от ФП във време, в което Java изплува като платформа
* Решава да ги обедини в нов език – Pizza 🍕 – като добави елементи към Java
    * Параметричен полиформизъм (Generics)
    * Функции от по-висок ред
    * Pattern matching
* Имплементацията му на generics и изцяло новият компилатор, който написва, стават част от Java

</div>

# Стъпка назад

* Java има много ограничения
* Как би изглеждал език, комбиниращ ФП и ООП, ако го дизайнваме в момента?

# SCAlable LAnguage (Scala) – Януари 2004-та

::: incremental

* Без (твърде) много feature-и в самия език
* Вместо това малко, но пълно множество от мощни езикови конструкции
* Имплементиране на feature-и в библиотеки, използвайки тези конструкции

:::

::: { .fragment }

Език, който скалира според нуждите

:::

# [Малка граматика](https://docs.scala-lang.org/scala3/reference/syntax.html)

![](./images/01-intro/grammar-size.png){ height="480" }

::: { .fragment }

Малко, но мощни езикови средства,<br />които се комбинират добре едно с друго

:::

# Симбиоза на ФП и ООП

"Scala was created to demonstrate combinations of FP and OOP are practical"

"Functions for the logic, objects as modules"

# Детайлна типова система

![](./images/01-intro/detailed-type-system-1.png){ height="560" }

# Детайлна типова система

![](./images/01-intro/detailed-type-system-2.png){ height="560" }

# Детайлна типова система

![](./images/01-intro/detailed-type-system-3.png){ height="560" }

# Защо Scala днес? (част 1)

* Създадено за ФП
* ФП екосистема
* С възможност да излезеш от ограниченията когато е нужно
* ФП в Java все още се усеща скалъпено
  * И дори с fibers Java е все още години назад в concurrency

# Защо Scala днес? (част 2)

* Интиутивен, приятен и силно изразителен
* Учи ни на много добри практики
  * лесни за използване по дефоулт
* Уникален набор от функционалности
  * непрекъснато подобряващ се
* Веднъж свикнал със Scala и този начин на мислене няма връщане назад :D

# Scala 3

* Чакахме го 8 години :)
* DOT – математически модел за [есенцията на Scala](https://www.scala-lang.org/blog/2016/02/03/essence-of-scala.html) на ниво типова система
* Dotty – изцяло нов компилатор с модерен дизайн, базиран на DOT
* Опростяване, изчистване и допълване на езика ([списък на какво ново](https://docs.scala-lang.org/scala3/new-in-scala3.html))

# Scala 3

::: incremental

* Курсът ни ще се базира на Scala 3

  ![](images/01-intro/cheering-minions.gif)
* Този път очакваме IntelliJ да работи добре :D
* Ще ви срещнем и с мъничко Scala 2 синтаксис
  - много код все още използва него (Бележка Боян: Но не ви трябва да го четете....)

:::

# Scala по света

::: incremental

* Скала през годините....
* Трансформация от специфични проблемни ниши към General Purpose Language
* Obligatory pointless name drop: Twitter, Netflix, Disney+, Stripe, Airtable, Databricks, Ocado!

:::

# Scala екосистеми и проекти

* [Lightbend](https://www.lightbend.com/) – Akka, Play Framework, Kalix, Slick
* [Typelevel](https://typelevel.org/) – Cats, Cats Effect, Http4s, fs2, doobie и много други
* [ZIO](https://zio.dev/)
* Apache 
  - [Spark](https://spark.apache.org/) – large-scale data processing (incl. ML)
  - [Flink](https://flink.apache.org/) – distributed event stream processing
  - [Kafka](https://kafka.apache.org/) – distributed event streaming

# Иновативно и достъпно комюнити<br />(и ФП и Scala)

![](images/01-intro/conferences.png){ height=480 }

# Какво ще учим 😊?<br />(част 1)

::: incremental

* Ще преразгледаме основите на ФП (и ООП)
* Immutability, immutability, immutability
  * програми = стойности + трансформации върху тях
  * типовете ни водят напред
    * щом се компилира обикновено работи :D
* ADTs и как да си дизайнем домейна изчерпателно
  * “make illegal state unrepresentable”
* Как да кодим и unhappy пътя
  * без да хвърляме изключения
  * изразявайки го чрез стойности, които можем да трансформираме

:::

# Какво ще учим 😊?<br />(част 2)

::: incremental

* Concurrency
  * без мютекси, mutable състояние, deadlocks, ...
  * разбирайки го
  * чрез асинхронни ефектни системи
    * гарантиращи безпосност
      * отново, щом се компилира – работи :P
    * решаващи проблемите на Promise/Future
    * composite, performant, auto-cancellation
* Прилагане на абстрактната мощ на математиката в програмирането
  * type class-ове
  * думички като монади, апликативи и др.
  * котки (Typelevel екосистемата) 😺
  
:::

# Какво ще учим 😊?<br />(част 3)

::: incremental

* Композитността ще е повтаряема тема
* От как да пишем малки функции до как да изградим цялостно Scala приложение
  * програмите като стойности
  * домейн дизайн
  * dependency injection, верифициран по време на компилация
  * гарантирано безопасно управление на ресурси 
* Уеб приложения
  * които си комуникират
  * HTTP, връзка с релационни бази, JSON сериализация, поточна обработка
  * описване на endpoint-и като данни ![](images/01-intro/tapir.png){ height=40 style="vertical-align:middle; margin: 0" }
* Курсът е с фокус върху концепциите и тяхното разбиране
  * за да можем да ги приложим и извън Scala
  * често ще правим сравнения с други езици

:::

# Какво ще учим 😊?<br />Разпределение по теми

1. Въведение във функционалното програмиране със Scala
2. ООП във функционален език
3. Основни подходи при функционалното програмиране
4. Fold, колекции, lazy колекции
5. Pattern matching и алгебрични типове от данни (ADTs)
6. Ефекти и функционална обработка на грешки
7. Конкурентност
8. Type classes
9. Монади и апликативи
10. Cats и Cats Effect
11. Изграждане на цялостно Scala web приложение

(навярно ще направим промени)

# За какво няма да говорим достатъчно

* Scala.js, нито Scala Native
  * [Fullstack Scala 3 with Typelevel Stack](https://www.youtube.com/watch?v=j6eFW4VmbRU)
* Тестване
  * ще разгледаме основите и ще ви насочваме през домашните
  * но няма да успеем на наблегнем на тях и ще разчитаме и на вас
  * Property-based testing
* Spark (но може да разгледаме примери)
* Direct style concurrency
  * тепърва се развива (safety concerns)
  * но вече съществуват Scala библиотеки, базирани на Java fibers ([Ox](https://github.com/softwaremill/ox))
  * курсът ще се фокусира върху ефектни системи

# Ресурси

![](images/01-intro/programming-in-scala.png){ height=240 } ![](images/01-intro/fp-in-scala-2nd-edition.png){ height=240 } ![](images/01-intro/practical-fp-in-scala.png){ height=240 }

![](images/01-intro/essential-effects.jpg){ height=240 } ![](images/01-intro/scala-with-cats.png){ height=240 }

# Ресурси

* [Официална документация](https://docs.scala-lang.org/scala3/book/introduction.html)
* [API документация](https://scala-lang.org/api/3.x/)
* [Rock the JVM курсове<br />
  ![](images/01-intro/rock-the-jvm.png){ height=240 }](https://rockthejvm.com/)

# Малко код

```scala
@main def hello = println("Hello FP World!")
```

```scala
case class Competitor(name: String, age: Int, score: Int)

def findTop100AdultsScoreSum(competitors: List[Competitor]): Int =
  competitors
    .filter(_.age >= 18)
    .map(_.score)
    .sorted(Ordering[Int].reverse)
    .take(100)
    .sum
```

# Малко код

```scala
def square(n: Int): IO[Int] = IO.sleep(1.second) >> IO(n * n)

def double(n: Int): IO[Int] = IO.sleep(1.second) >> IO(n * 2)

val calc = for
  (a, b, c) <- (square(2), square(10), square(20)).parTupled
  d <- double(a + b + c)
yield d

def ioExample = calc.timed >>= IO.println
```

# Въпроси :)?

::: { .fragment }

![](images/01-intro/case-ended.webp)

:::
