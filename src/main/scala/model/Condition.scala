package model

sealed trait Condition

object Condition {
  def areConditionsCompatible(candidateCondition: CandidateCondition, employersCondition: EmployersCondition): Boolean = ???
}

case class CandidateCondition(minSalaryAcceptable: Int) extends Condition
case class EmployersCondition(maxSalaryAcceptable: Int) extends Condition
