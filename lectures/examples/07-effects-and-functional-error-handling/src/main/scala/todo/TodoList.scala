package todo

import todo.TodoListAction.*

enum Priority:
  case Low, Medium, High

case class TodoId(id: String) extends AnyVal:
  override def toString: String = id

case class Todo(id: TodoId, description: String, priority: Priority)

case class TodoList(todos: Map[TodoId, Todo], completedTodosCount: Int):
  def applyAction(todoAction: TodoListAction): TodoList = todoAction match
    case AddTodo(todo) =>
      this.copy(todos = todos + (todo.id -> todo))
    case CompleteTodo(todoId) =>
      TodoList(
        todos - todoId,
        completedTodosCount + todos.get(todoId).map(_ => 1).getOrElse(0)
      )
    case CancelTodo(todoId) =>
      this.copy(todos - todoId)

  def hasTodo(todoId: TodoId): Boolean = todos.contains(todoId)

object TodoList:
  def empty: TodoList = TodoList(Map.empty, 0)

enum TodoListAction:
  case AddTodo(todo: Todo)
  case CompleteTodo(todoId: TodoId)
  case CancelTodo(todoId: TodoId)
