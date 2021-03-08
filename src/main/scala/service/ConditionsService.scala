package service

import cats.effect.Sync
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl

class ConditionsService[F[_]](implicit F: Sync[F]) extends Http4sDsl[F] {
  val routes: HttpRoutes[F] = HttpRoutes.of[F] {
    case POST -> Root / "candidate_condition" => ???
    case POST -> Root / "employer_condition" => ???
  }
}
