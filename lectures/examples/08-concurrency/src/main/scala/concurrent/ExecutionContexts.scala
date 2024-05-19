package concurrent

import java.util.concurrent.Executors.newCachedThreadPool
import java.util.concurrent.{Executor, ForkJoinPool}
import scala.concurrent.{ExecutionContext, ExecutionContextExecutorService}

object ExecutionContexts:
  given default: ExecutionContext = ExecutionContext.fromExecutorService(new ForkJoinPool)

  val currentThreadEc: ExecutionContext = ExecutionContext.fromExecutor: (operation: Runnable) =>
    operation.run()

  val blocking: ExecutionContextExecutorService = ExecutionContext.fromExecutorService(newCachedThreadPool())
