package streams.tictactoe

import cats.effect.IO
import io.circe.Codec
import streams.tictactoe.CirceConfiguration.given
import sttp.capabilities
import sttp.capabilities.fs2.Fs2Streams
import sttp.tapir.*
import sttp.tapir.Codec.textWebSocketFrame
import sttp.tapir.json.circe.*

enum TicTacToeWebSocketData derives Schema, Codec:
  case NewState(ticTacToe: TicTacToe)
  case MoveError(ticTacToeMoveError: TicTacToeMoveError)

object TicTacToeWebSocketData:
  def apply(either: Either[TicTacToeMoveError, TicTacToe]): TicTacToeWebSocketData =
    either.fold(MoveError.apply, NewState.apply)

object TicTacToeEndpoints:
  val ticTacToeWsEndpoint =
    endpoint
      .in("tictactoe-ws" / path[String].name("roomId"))
      .out(webSocketBody[Move, CodecFormat.Json, TicTacToeWebSocketData, CodecFormat.Json](Fs2Streams[IO]))
      .get
