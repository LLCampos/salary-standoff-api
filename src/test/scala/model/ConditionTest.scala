package model

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class ConditionTest extends AnyWordSpec with Matchers {

  "ConditionTest" should {
    "areConditionsCompatible" should {
      "return true if candidate's min salary lower than employer's max salary" in {
        Condition.areConditionsCompatible(
          CandidateCondition(30000),
          EmployersCondition(40000)
        ) shouldBe true
      }

      "return true if candidate's min salary same as employer's max salary" in {
        Condition.areConditionsCompatible(
          CandidateCondition(30000),
          EmployersCondition(30000)
        ) shouldBe true
      }
      "return false if candidate's min salary higher than employer's max salary" in {
        Condition.areConditionsCompatible(
          CandidateCondition(30000),
          EmployersCondition(25000)
        ) shouldBe false
      }
    }
  }
}
