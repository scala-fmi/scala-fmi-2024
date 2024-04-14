package effects

type Params = Map[String, String]

// direct:
def extract(params: Params) = params("num")

def parse(s: String) = s.toInt

val reciprocal: PartialFunction[Int, Double] = {
  case x if x != 0 => 1.toDouble / x
}

// with Option:
def extractMaybe(params: Params): Option[String] = params.get("num")

def parseMaybe(s: String): Option[Int] =
  try Some(s.toInt)
  catch case e: NumberFormatException => None

val reciprocalMaybe: Int => Option[Double] = reciprocal.lift

def process(params: Params) =
  extractMaybe(params)
    .flatMap(parseMaybe)
    .flatMap(reciprocalMaybe)

def processAlt(params: Params) =
  extractMaybe(params)
    .flatMap(numStr => parseMaybe(numStr).flatMap(reciprocalMaybe))
