package threads

import util.Utils

@main def runThreadsExample =
  import Utils.doWork

  def createThread(work: => Unit) = new Thread(() => work)

  Utils.time("Without threads") {
    doWork
    doWork
    doWork
    doWork
  }

  Utils.time("With threads") {
    val thread1 = createThread(doWork)
    val thread2 = createThread(doWork)
    val thread3 = createThread(doWork)
    val thread4 = createThread(doWork)

    thread1.start()
    thread2.start()
    thread3.start()
    thread4.start()

    thread1.join()
    thread2.join()
    thread3.join()
    thread4.join()
  }
end runThreadsExample
