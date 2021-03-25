package repository

import cats.effect.Effect
import cats.implicits._
import doobie._
import doobie.implicits._
import model.{CandidateCondition, ConditionMetadata}


class DatabaseConditionsRepository[F[_]](transactor: Transactor[F])(implicit F: Effect[F]) extends ConditionsRepository[F] {

  implicit val candidateConditionGet: Read[CandidateCondition] =
    Read[(Int, String, String, String, Option[String])].map { case (minSalaryAcceptable, currency, grossOrNet, annualOrMonthly, extraComments) =>
      CandidateCondition(minSalaryAcceptable, ConditionMetadata(
        currency, grossOrNet, annualOrMonthly, extraComments
      ))
    }

  def addCandidateCondition(condition: CandidateCondition): F[ConditionId] = {
    val uuid = java.util.UUID.randomUUID.toString
    sql"""INSERT INTO condition (uuid, candidate_min_acceptable, currency, gross_or_net, annual_or_monthly, extra_comments, ts)
          VALUES ($uuid, ${condition.minSalaryAcceptable}, ${condition.metadata.currency}, ${condition.metadata.grossOrNet},
               ${condition.metadata.annualOrMonthly}, ${condition.metadata.extraComments}, now())"""
      .update.run.transact(transactor).as(uuid)
  }

  def getCandidateCondition(conditionId: ConditionId): F[Option[CandidateCondition]] =
    sql"""SELECT candidate_min_acceptable, currency, gross_or_net, annual_or_monthly, extra_comments
          FROM condition WHERE uuid = $conditionId""".query[CandidateCondition]
      .option
      .transact(transactor)

  def getConditionMetadata(conditionId: ConditionId): F[Option[ConditionMetadata]] =
    sql"""SELECT currency, gross_or_net, annual_or_monthly, extra_comments
          FROM condition WHERE uuid = $conditionId""".query[ConditionMetadata]
      .option
      .transact(transactor)

  def deleteCondition(conditionId: ConditionId): F[Unit] =
    sql"DELETE FROM condition WHERE uuid = $conditionId".update.run.transact(transactor).as(())

}
