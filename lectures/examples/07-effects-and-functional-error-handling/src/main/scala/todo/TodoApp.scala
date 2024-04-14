package todo

import answers.io.{ConsoleUtils, IO}
import todo.TodoListAction.AddTodo

object TodoApp:
  val actionPrompt: IO[String] = ConsoleUtils.prompt:
    """
      |1. List ToDos
      |2. Add Todo
      |3. Complete ToDo
      |4. Cancel Todo
      |""".stripMargin

  def evaluateInput(todoList: TodoList, actionId: String): IO[Option[TodoListAction]] =
    actionId match
      case "1" => listTodos(todoList)
      case "2" => addNewTodo
      case "3" => completeTodo(todoList)
      case "4" => cancelTodo(todoList)
      case _ =>
        IO.println("Invalid input") >> IO.of(None)

  def listTodos(todoList: TodoList): IO[Option[TodoListAction]] =
    val todoDescriptions = todoList.todos.values.map: todo =>
      s"ID: ${todo.id} -- ${todo.description} -- priority: ${todo.priority}"

    IO.println(todoDescriptions.mkString("\n")) >>
      IO.println(s"Todos completed so far: ${todoList.completedTodosCount}") >>
      IO.of(None)

  def promptForId: IO[TodoId] = ConsoleUtils.prompt("Enter ID:").map(TodoId.apply)

  def promptForPriority: IO[Priority] = for
    priorityString <- ConsoleUtils.prompt("Enter priority:")
    priority <-
      if Priority.values.map(_.toString).contains(priorityString) then IO.of(Priority.valueOf(priorityString))
      else IO.println("Invalid priority. Enter Low, Medium, High") >> promptForPriority
  yield priority

  def addNewTodo: IO[Option[TodoListAction]] = for
    id <- promptForId
    description <- ConsoleUtils.prompt("Enter description:")
    priority <- promptForPriority
    newTodo = Todo(id, description, priority)
  yield Some(AddTodo(newTodo))

  def completeTodo(todoList: TodoList): IO[Option[TodoListAction]] = for
    id <- promptForId
    maybeAction <-
      if todoList.hasTodo(id) then IO.of(Some(TodoListAction.CompleteTodo(id)))
      else IO.println(s"Non-existent todo ${id.id}") >> IO.of(None)
  yield maybeAction

  def cancelTodo(todoList: TodoList): IO[Option[TodoListAction]] = for
    id <- promptForId
    maybeAction <-
      if todoList.hasTodo(id) then IO.of(Some(TodoListAction.CancelTodo(id)))
      else IO.println(s"Non-existent todo ${id.id}") >> IO.of(None)
  yield maybeAction

  def askForContinuation: IO[Boolean] = ConsoleUtils.promptForBoolean("Continue?")

  def todoListApp(todoList: TodoList): IO[Unit] = for
    actionId <- actionPrompt
    maybeAction <- evaluateInput(todoList, actionId)
    updatedTodoList = maybeAction.map(todoList.applyAction).getOrElse(todoList)
    shouldContinue <- askForContinuation
    _ <- if shouldContinue then todoListApp(updatedTodoList) else IO.of(())
  yield ()

  @main
  def run(): Unit = todoListApp(TodoList.empty).unsafeRunStackSafe()
