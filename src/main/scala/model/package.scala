import io.circe.generic.JsonCodec

import io.circe._

package object model {
  @JsonCodec case class PostCandidateConditionResponse(conditionId: String)
  @JsonCodec case class PostEmployerConditionResponse(areConditionsCompatible: Boolean)
}
