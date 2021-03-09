package service

import cats.data.OptionT
import cats.effect.Sync
import cats.syntax.all._
import com.typesafe.scalalogging.LazyLogging
import io.circe.Json
import io.circe.syntax._
import model.CandidateCondition._
import model.{CandidateCondition, Condition, EmployersCondition, PostCandidateConditionResponse, PostEmployerConditionResponse}
import org.http4s.HttpRoutes
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import repository.ConditionsRepository


class ConditionsService[F[_]](repository: ConditionsRepository[F])(implicit F: Sync[F]) extends Http4sDsl[F] with LazyLogging {

  val routes: HttpRoutes[F] = HttpRoutes.of[F] {
    case req @ POST -> Root / "candidate_condition" => for {
      candidateCondition <- req.as[Json].map(_.as[CandidateCondition])
      resp <- candidateCondition match {
        case Right(condition) => repository.addCandidateCondition(condition).flatMap(
          conditionId => Ok(PostCandidateConditionResponse(conditionId).asJson)
        )
        case Left(err) =>
          BadRequest(err.toString())
      }
    } yield resp

    case req @ POST -> Root / "employer_condition" / UUIDVar(conditionId) => (for {
      employerCondition <- OptionT(req.as[Json].map(_.as[EmployersCondition]).map(_.toOption))
      candidateCondition <- OptionT(repository.getCandidateCondition(conditionId.toString))
    } yield PostEmployerConditionResponse(
      Condition.areConditionsCompatible(candidateCondition, employerCondition)
    )).value.flatMap {
      case Some(resp) => Ok(resp.asJson)
      case None => BadRequest()
    }
  }
}
