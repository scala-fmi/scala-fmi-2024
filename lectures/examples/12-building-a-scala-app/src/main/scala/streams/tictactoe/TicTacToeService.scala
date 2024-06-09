package streams.tictactoe

import cats.effect.{IO, Ref}
import fs2.Stream

class TicTacToeService private (roomsRef: Ref[IO, Map[String, GameRoom]]):
  private def findRoom(roomId: String): IO[GameRoom] =
    for
      maybeNewRoom <- GameRoom()
      room <-
        roomsRef.modify { rooms =>
          rooms.get(roomId) match
            case Some(room) => (rooms, room)
            case None => (rooms.updated(roomId, maybeNewRoom), maybeNewRoom)
        }
    yield room

  def joinRoom(roomId: String): Stream[IO, TicTacToe] =
    Stream.eval(findRoom(roomId)).flatMap(_.stateStream)

  def makeAMove(roomId: String, move: Move): IO[Either[TicTacToeMoveError, TicTacToe]] =
    for
      room <- findRoom(roomId)
      moveResult <- room.makeAMove(move)
    yield moveResult

object TicTacToeService:
  def apply(): IO[TicTacToeService] =
    Ref[IO].of(Map.empty[String, GameRoom]).map(initialRooms => new TicTacToeService(initialRooms))
