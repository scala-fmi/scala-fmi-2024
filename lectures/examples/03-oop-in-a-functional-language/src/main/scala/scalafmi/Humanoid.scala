package scalafmi

trait Humanoid:
  def name: String
  val age: Int

case class Person(name: String) extends Humanoid:
  val age: Int = name.size

case class Robot(brand: String, serialNumber: String, age: Int) extends Humanoid:
  def name = s"$brand--$serialNumber"

case class PersonId(id: String) extends AnyVal

case class LocationId(id: String) extends AnyVal

def createAddressRegistration(personId: PersonId, locationId: LocationId) = ???

def main =
  val stoyan = PersonId("100")
  val ruse = LocationId("5")
  createAddressRegistration(stoyan, ruse) // успех
//  createAddressRegistration(ruse, stoyan)
