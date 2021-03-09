package repository

import model.CandidateCondition

trait ConditionsRepository[F[_]] {
  type ConditionId = String

  def addCandidateCondition(candidateCondition: CandidateCondition): F[ConditionId]
  def getCandidateCondition(conditionId: ConditionId): F[Option[CandidateCondition]]
  def deleteCondition(conditionId: ConditionId): F[Unit]
}
