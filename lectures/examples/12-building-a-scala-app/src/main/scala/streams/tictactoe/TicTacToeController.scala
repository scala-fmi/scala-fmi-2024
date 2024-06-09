package streams.tictactoe

import cats.effect.IO
import cats.syntax.all.*
import fs2.Pipe

class TicTacToeController(ticTacToeService: TicTacToeService):
  val ticTacToeWs = TicTacToeEndpoints.ticTacToeWsEndpoint.serverLogicSuccess: roomId =>
    val wsStream: Pipe[IO, Move, TicTacToeWebSocketData] = inputStream =>
      val gameStateStream = ticTacToeService.joinRoom(roomId).map(TicTacToeWebSocketData.NewState.apply)
      val moveStream =
        inputStream
          .evalMap(ticTacToeService.makeAMove(roomId, _))
          .map(TicTacToeWebSocketData.apply)
          .collect:
            case data @ TicTacToeWebSocketData.MoveError(_) => data

      gameStateStream.mergeHaltBoth(moveStream)

    wsStream.pure[IO]
