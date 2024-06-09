package streams.tictactoe

import cats.effect.IO
import cats.syntax.all.*
import fs2.Stream
import fs2.concurrent.SignallingRef

case class GameRoom private (stateRef: SignallingRef[IO, TicTacToe]):
  def makeAMove(move: Move): IO[Either[TicTacToeMoveError, TicTacToe]] =
    stateRef.modify: currentGameState =>
      val moveResult = currentGameState.makeMove(move)
      moveResult.fold(
        _ => (currentGameState, moveResult),
        newGameState => (newGameState, moveResult)
      )

  def stateStream: Stream[IO, TicTacToe] =
    stateRef.discrete.zipWithPrevious
      .collect:
        case (prev, current) if !prev.contains(current) => current

object GameRoom:
  def apply(): IO[GameRoom] =
    for state <- SignallingRef[IO].of(TicTacToe.initial)
    yield GameRoom(state)
