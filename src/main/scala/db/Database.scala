package db

import cats.effect.{Blocker, ContextShift, IO, Resource}
import com.zaxxer.hikari.HikariConfig
import config.DatabaseConfig
import doobie.hikari.HikariTransactor
import org.flywaydb.core.Flyway

import scala.concurrent.ExecutionContext
import scala.util.Properties

object Database {
  def transactor(config: DatabaseConfig, executionContext: ExecutionContext, blocker: Blocker)(implicit contextShift: ContextShift[IO]): Resource[IO, HikariTransactor[IO]] =
    Properties.envOrNone("JDBC_DATABASE_URL") match {
      // Heroku
      case Some(dbUrl) =>
        val hikariConfig = new HikariConfig()
        hikariConfig.setJdbcUrl(dbUrl)
        hikariConfig.setDriverClassName("org.postgresql.Driver")
        HikariTransactor.fromHikariConfig(hikariConfig, executionContext, blocker)
      // Local
      case None =>
        HikariTransactor.newHikariTransactor[IO](
          config.driver,
          config.url,
          config.user,
          config.password,
          executionContext,
          blocker
        )
    }

  def initialize(transactor: HikariTransactor[IO]): IO[Unit] = {
    transactor.configure { dataSource =>
      IO {
        val flyWay = Flyway.configure().dataSource(dataSource).load()
        flyWay.migrate()
        ()
      }
    }
  }
}
