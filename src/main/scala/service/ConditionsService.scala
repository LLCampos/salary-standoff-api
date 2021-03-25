package service

import cats.effect.Sync
import cats.syntax.all._
import com.typesafe.scalalogging.LazyLogging
import io.circe.syntax._
import model.CandidateCondition._
import model._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import org.http4s.{EntityDecoder, HttpRoutes}
import repository.ConditionsRepository


class ConditionsService[F[_]](repository: ConditionsRepository[F])(implicit F: Sync[F]) extends Http4sDsl[F] with LazyLogging {

  implicit private val CandidateConditionDecoder: EntityDecoder[F, CandidateCondition] = jsonOf[F, CandidateCondition]
  implicit private val EmployersConditionDecoder: EntityDecoder[F, EmployersCondition] = jsonOf[F, EmployersCondition]
  implicit private val ConditionMetadataDecoder: EntityDecoder[F, ConditionMetadata] = jsonOf[F, ConditionMetadata]

  val routes: HttpRoutes[F] = HttpRoutes.of[F] {
    case req @ POST -> Root / "candidate_condition" => for {
      candidateCondition <- req.as[CandidateCondition]
      resp <- repository.addCandidateCondition(candidateCondition).flatMap(
          conditionId => Ok(PostCandidateConditionResponse(conditionId).asJson)
        )
    } yield resp

    case GET -> Root / "condition" / "metadata" / UUIDVar(conditionId) =>
      repository.getConditionMetadata(conditionId.toString).flatMap {
        case Some(metadata) => Ok(metadata.asJson)
        case None => NotFound(s"No metadata found for condition with uuid $conditionId")
      }

    case req @ POST -> Root / "employer_condition" / UUIDVar(conditionId) => for {
      employerCondition <- req.as[EmployersCondition]
      candidateConditionOpt <- repository.getCandidateCondition(conditionId.toString)
      res <- candidateConditionOpt match {
        case Some(candidateCondition) =>
          repository.deleteCondition(conditionId.toString) >> {
            val response = PostEmployerConditionResponse(Condition.areConditionsCompatible(candidateCondition, employerCondition))
            Ok(response.asJson)
          }
        case None => NotFound()
      }
    } yield res
  }
}
