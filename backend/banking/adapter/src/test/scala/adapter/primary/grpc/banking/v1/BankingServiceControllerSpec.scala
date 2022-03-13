package adapter.primary.grpc.banking.v1

import api.banking.v1._
import org.scalatest.concurrent.ScalaFutures.convertScalaFuture
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers._
import usecase.interactor.CreateAccountInteractor

class BankingServiceControllerSpec extends AnyFreeSpec {
  val create  = new CreateAccountInteractor
  val service = new BankingServiceController(create)

  "#createAccount" - {
    "OK" in {
      val req = CreateAccountRequest()
      val expected = CreateAccountResponse(
        Some(
          Account(
            id = "1",
            balance = 0
          )
        )
      )

      val res = service.createAccount(req).futureValue

      res mustBe expected
    }
  }
}
