package callbacks

import product.{Product, ProductFactory, Verification}

import java.util.concurrent.{Executor, Executors}

object Callbacks:
  val threadPool: Executor = Executors.newFixedThreadPool(Runtime.getRuntime.availableProcessors)
  def execute(work: => Any): Unit = threadPool.execute(() => work)

  def produceBook(onComplete: Product => Unit): Unit = execute {
    val product = ProductFactory.produceProduct("Book")

    execute(onComplete(product))
  }

//  def produce2Books(onComplete: (Product, Product) => Unit): Unit = execute {
//    produceBook(_ => ())
//    produceBook(_ => ())
//  }

  def produce2Books(onComplete: (Product, Product) => Unit): Unit =
    // We need to do complex and error prone state management
    // and concurrency synchronization
    var firstProduct: Option[Product] = None

    val onProduced: Product => Unit = { newProduct =>
      this.synchronized {
        firstProduct match
          case None => firstProduct = Some(newProduct)
          case Some(existingProduct) => onComplete(existingProduct, newProduct)
      }
    }

    produceBook(onProduced)
    produceBook(onProduced)

  @main def run: Unit = execute {
//    produceBook(println)
    produce2Books((p1, p2) => println((p1, p2)))
  }

  def verifyProduct(product: Product)(onVerified: Verification => Unit): Unit = execute {
    val verification = ProductFactory.verifyProduct(product)

    execute(onVerified(verification))
  }

  def produceInPipeline(callback: (List[Product], List[Verification]) => Unit): Unit =
    // Callback hell
    produceBook { a =>
      verifyProduct(a) { aVerification =>
        produceBook { b =>
          verifyProduct(b) { bVerification =>
            callback(List(a, b), List(aVerification, bVerification))
          }
        }
      }
    }
