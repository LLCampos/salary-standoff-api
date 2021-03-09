package repository

import cats.effect.Effect
import cats.syntax.all._
import doobie.Transactor
import doobie.implicits._
import model.CandidateCondition


class DatabaseConditionsRepository[F[_]](transactor: Transactor[F])(implicit F: Effect[F]) extends ConditionsRepository[F] {

  def addCandidateCondition(candidateCondition: CandidateCondition): F[ConditionId] = {
    val uuid = java.util.UUID.randomUUID.toString
    sql"""INSERT INTO condition (uuid, candidate_min_acceptable, ts)
        VALUES ($uuid, ${candidateCondition.minSalaryAcceptable}, now())""".update.run.transact(transactor).as(uuid)
  }

  def getCandidateCondition(conditionId: ConditionId): F[Option[CandidateCondition]] =
    sql"SELECT candidate_min_acceptable FROM condition WHERE uuid = $conditionId".query[CandidateCondition]
      .option
      .transact(transactor)

  def deleteCondition(conditionId: ConditionId): F[Unit] = ???
}
