package fmi.config

import com.comcast.ip4s.Port
import com.typesafe.config.Config
import fmi.infrastructure.db.DbConfig

case class ShoppingAppConfig(
  secretKey: String,
  http: HttpConfig,
  database: DbConfig
)

object ShoppingAppConfig:
  def fromConfig(config: Config): ShoppingAppConfig =
    val secretKey = config.getString("secretKey")

    val http = HttpConfig(
      Port.fromInt(config.getInt("http.port")).get
    )

    val dbConfig = config.getConfig("database")
    val database = DbConfig(
      dbConfig.getString("host"),
      dbConfig.getInt("port"),
      dbConfig.getString("user"),
      dbConfig.getString("password"),
      dbConfig.getString("name"),
      dbConfig.getString("schema"),
      dbConfig.getInt("connectionPoolSize")
    )

    ShoppingAppConfig(
      secretKey,
      http,
      database
    )
