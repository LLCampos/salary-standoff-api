package model

sealed trait Condition

object Condition {
  def areConditionsCompatible(candidateCondition: CandidateCondition, employersCondition: EmployersCondition): Boolean =
    candidateCondition.minSalaryAcceptable <= employersCondition.maxSalaryAcceptable
}

case class CandidateCondition(minSalaryAcceptable: Int) extends Condition
case class EmployersCondition(maxSalaryAcceptable: Int) extends Condition
