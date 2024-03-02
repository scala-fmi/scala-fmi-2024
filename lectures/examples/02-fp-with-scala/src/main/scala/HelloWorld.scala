import com.scalafmi.printSum

@main
def hello = {
  println("Hello, World!")
  
  printSum

  fibDoubled(2)
}

//def ??? : Nothing = throw new NotImplementedError

def twice(n: Int): Int = ???

def fib(n: Int): Int =
  if n == 0 then 0
  else if n == 1 then 1
  else fib(n - 1) + fib(n - 2)

def fibDoubled(n: Int): Int = twice(fib(n))
