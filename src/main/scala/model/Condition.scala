package model

import io.circe.generic.JsonCodec

sealed trait Condition

object Condition {
  def areConditionsCompatible(candidateCondition: CandidateCondition, employersCondition: EmployersCondition): Boolean =
    candidateCondition.minSalaryAcceptable <= employersCondition.maxSalaryAcceptable
}

@JsonCodec case class CandidateCondition(minSalaryAcceptable: Int) extends Condition
@JsonCodec case class EmployersCondition(maxSalaryAcceptable: Int) extends Condition
