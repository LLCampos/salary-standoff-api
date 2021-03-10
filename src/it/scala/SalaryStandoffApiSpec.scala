import cats.effect.{ContextShift, IO, Timer}
import config.Config
import io.circe.Json
import io.circe.syntax._
import model.{CandidateCondition, EmployersCondition, PostCandidateConditionResponse, PostEmployerConditionResponse}
import org.http4s.circe._
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.{Method, Request, Uri}
import org.scalatest.concurrent.Eventually
import org.scalatest.matchers.should.Matchers
import org.scalatest.time.{Millis, Seconds, Span}
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}

import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

class SalaryStandoffApiSpec extends AnyWordSpec with Matchers with BeforeAndAfterAll with BeforeAndAfterEach with Eventually {
  private implicit val timer: Timer[IO] = IO.timer(ExecutionContext.global)

  private implicit val contextShift: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

  private lazy val client = BlazeClientBuilder[IO](global).resource

  private val configFile = "test.conf"

  private lazy val config = Config.load(configFile).use(config => IO.pure(config)).unsafeRunSync()

  private lazy val urlStart = s"http://${config.server.host}:${config.server.port}"

  implicit override val patienceConfig: PatienceConfig = PatienceConfig(timeout = scaled(Span(5, Seconds)), interval = scaled(Span(100, Millis)))

  override def beforeAll(): Unit = {
    HttpServer.create(configFile).unsafeRunAsyncAndForget()
    IO.sleep(1.seconds).unsafeRunSync()
  }

  "Salary Standoff API" should {
    "return true on POST /employer_condition if conditions are compatible" in {
      val candidateCondition = CandidateCondition(minSalaryAcceptable = 40)
      val employerCondition = EmployersCondition(maxSalaryAcceptable = 50)

      val candidateRequest = Request[IO](
        method = Method.POST,
        uri = Uri.unsafeFromString(s"$urlStart/candidate_condition")
      ).withEntity(candidateCondition.asJson)

      val candidateResponse = client.use(_.expect[Json](candidateRequest)).unsafeRunSync().as[PostCandidateConditionResponse].toOption.get

      val employerRequest = Request[IO](
        method = Method.POST,
        uri = Uri.unsafeFromString(s"$urlStart/employer_condition/${candidateResponse.conditionId}")
      ).withEntity(employerCondition.asJson)

      val employerResponse = client.use(_.expect[Json](employerRequest)).unsafeRunSync().as[PostEmployerConditionResponse].toOption.get

      employerResponse.areConditionsCompatible shouldBe true
    }

    "return false on POST /employer_condition if conditions are not compatible" in {
      val candidateCondition = CandidateCondition(minSalaryAcceptable = 50)
      val employerCondition = EmployersCondition(maxSalaryAcceptable = 40)

      val candidateRequest = Request[IO](
        method = Method.POST,
        uri = Uri.unsafeFromString(s"$urlStart/candidate_condition")
      ).withEntity(candidateCondition.asJson)

      val candidateResponse = client.use(_.expect[Json](candidateRequest)).unsafeRunSync().as[PostCandidateConditionResponse].toOption.get

      val employerRequest = Request[IO](
        method = Method.POST,
        uri = Uri.unsafeFromString(s"$urlStart/employer_condition/${candidateResponse.conditionId}")
      ).withEntity(employerCondition.asJson)

      val employerResponse = client.use(_.expect[Json](employerRequest)).unsafeRunSync().as[PostEmployerConditionResponse].toOption.get

      employerResponse.areConditionsCompatible shouldBe false
    }

    "fail when trying to check condition for compatibility more than once" in {
      val candidateCondition = CandidateCondition(minSalaryAcceptable = 50)
      val employerCondition = EmployersCondition(maxSalaryAcceptable = 40)

      val candidateRequest = Request[IO](
        method = Method.POST,
        uri = Uri.unsafeFromString(s"$urlStart/candidate_condition")
      ).withEntity(candidateCondition.asJson)

      val candidateResponse = client.use(_.expect[Json](candidateRequest)).unsafeRunSync().as[PostCandidateConditionResponse].toOption.get

      val employerRequest = Request[IO](
        method = Method.POST,
        uri = Uri.unsafeFromString(s"$urlStart/employer_condition/${candidateResponse.conditionId}")
      ).withEntity(employerCondition.asJson)

      val statusFirstTime = client.use(_.status(employerRequest)).unsafeRunSync()
      val statusSecondTime = client.use(_.status(employerRequest)).unsafeRunSync()

      statusFirstTime.code shouldBe 200
      statusSecondTime.code shouldBe 404
    }
  }
}
