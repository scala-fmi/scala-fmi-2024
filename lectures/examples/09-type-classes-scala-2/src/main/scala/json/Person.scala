package json

import JsonSerializable.ops._

case class Person(name: String, email: String, age: Int)

object Person {
  implicit val personSerializable: JsonSerializable[Person] = new JsonSerializable[Person] {
    def toJson(person: Person): JsonValue = JsonObject(Map(
      "name" -> person.name.toJson,
      "email" -> person.email.toJson,
      "age" -> person.age.toJson
    ))
  }
}

