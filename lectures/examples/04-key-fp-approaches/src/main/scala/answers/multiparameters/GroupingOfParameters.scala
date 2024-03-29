package answers.multiparameters

@main
def groupingOfParametersExamples =
  def min[T](compare: (T, T) => Int)(a: T, b: T): T =
    if compare(a, b) < 0 then a
    else b

  val compareByAbsoluteValue = (a: Int, b: Int) => a.abs - b.abs
  val minByAbsoluteValue = min(compareByAbsoluteValue)

  min(Integer.compare)(20, 10) // 10
  minByAbsoluteValue(-20, -10) // -10
  List(-20, -10, 40).reduce(minByAbsoluteValue) // -10
end groupingOfParametersExamples
