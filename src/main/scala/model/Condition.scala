package model

import io.circe.generic.JsonCodec

sealed trait Condition

object Condition {
  def areConditionsCompatible(candidateCondition: CandidateCondition, employersCondition: EmployersCondition): Boolean =
    candidateCondition.minSalaryAcceptable <= employersCondition.maxSalaryAcceptable
}

@JsonCodec
case class CandidateCondition(
  minSalaryAcceptable: Int,
  metadata: ConditionMetadata,
) extends Condition

@JsonCodec
case class ConditionMetadata(
  currency: String,
  grossOrNet: String,
  annualOrMonthly: String,
  extraComments: Option[String]
)

@JsonCodec case class EmployersCondition(maxSalaryAcceptable: Int) extends Condition
