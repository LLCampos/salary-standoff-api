import io.circe.generic.JsonCodec

package object model {
  @JsonCodec case class PostCandidateConditionResponse(conditionId: String)
  @JsonCodec case class PostEmployerConditionResponse(areConditionsCompatible: Boolean)
}
