package tictactoe

// Belote
// Bidding | Playing | FInished

enum Player:
  case X, O

  def nextPlayer: Player = this match
    case X => O
    case O => X

case class Cell private (x: Int, y: Int):
  override def toString = s"($x, $y)"

object Cell:
  val columnsRange: Range = 1 to 3
  val rowsRange: Range = 1 to 3

  def applyOption(x: Int, y: Int): Option[Cell] =
    if columnsRange.contains(x) && rowsRange.contains(y) then Some(Cell(x, y))
    else None

  val columns: List[List[Cell]] = columnsRange.toList.map: column =>
    rowsRange.toList.map(y => Cell(column, y))

  val rows: List[List[Cell]] = rowsRange.toList.map: row =>
    columnsRange.toList.map(x => Cell(x, row))

  // Todo: derive from columnsRange and rowsRange
  val diagonals: List[List[Cell]] = List(
    List(Cell(1, 1), Cell(2, 2), Cell(3, 3)),
    List(Cell(1, 3), Cell(2, 2), Cell(3, 1))
  )

  val allCells: Set[Cell] = rows.flatten.toSet

  val allWinningPositions: List[List[Cell]] = rows ++ columns ++ diagonals

case class Move(player: Player, targetCell: Cell)

enum TicTacToeMoveError:
  case PlayerNotInTurn(attemptedToMove: Player, currentPlayer: Player)
  case CellIsAlreadyTaken(cell: Cell, takenBy: Player)
  case GameFinished

enum TicTacToeOutcome:
  case Winner(player: Player)
  case Tie

case class TicTacToe(board: Map[Cell, Player], currentPlayer: Player):
  def winner: Option[Player] =
    val markedWinningPositions = Cell.allWinningPositions.map(_.map(board.get))

    List(Player.X, Player.O).find: player =>
      markedWinningPositions.exists(_.forall(_ == Some(player)))

  def isFull: Boolean = board.keySet == Cell.allCells
  def isTie: Boolean = isFull && winner.isEmpty
  def isFinished: Boolean = winner.isDefined || isTie

  def outcome: Option[TicTacToeOutcome] =
    if isFinished then
      Some:
        winner.map(TicTacToeOutcome.Winner.apply).getOrElse(TicTacToeOutcome.Tie)
    else None

  def makeMove(move: Move): Either[TicTacToeMoveError, TicTacToe] =
    if isFinished then Left(TicTacToeMoveError.GameFinished)
    else if move.player != currentPlayer then Left(TicTacToeMoveError.PlayerNotInTurn(move.player, currentPlayer))
    else
      board
        .get(move.targetCell)
        .map(occupiedBy => Left(TicTacToeMoveError.CellIsAlreadyTaken(move.targetCell, occupiedBy)))
        .getOrElse(
          Right(
            TicTacToe(
              board + (move.targetCell -> move.player),
              currentPlayer.nextPlayer
            )
          )
        )

object TicTacToe:
  def initial: TicTacToe = TicTacToe(Map.empty, Player.X)
