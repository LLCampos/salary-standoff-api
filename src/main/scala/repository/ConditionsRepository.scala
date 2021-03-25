package repository

import model.{CandidateCondition, ConditionMetadata}

trait ConditionsRepository[F[_]] {
  type ConditionId = String

  def addCandidateCondition(candidateCondition: CandidateCondition): F[ConditionId]
  def getCandidateCondition(conditionId: ConditionId): F[Option[CandidateCondition]]
  def getConditionMetadata(conditionId: ConditionId): F[Option[ConditionMetadata]]
  def deleteCondition(conditionId: ConditionId): F[Unit]
}
