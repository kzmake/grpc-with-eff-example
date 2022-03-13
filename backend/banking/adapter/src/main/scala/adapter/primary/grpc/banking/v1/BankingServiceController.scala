package adapter.primary.grpc.banking.v1

import api.banking.v1._
import adapter.eff.Stack._
import org.atnos.eff.syntax.all.toEffOnePureValueOps
import usecase.interactor.CreateAccountInputData
import usecase.interactor.CreateAccountOutputData
import usecase.port.Port

import scala.concurrent.Future

class BankingServiceController(
    val createAccount: Port[CreateAccountInputData, CreateAccountOutputData]
) extends BankingService {

  override def createAccount(in: CreateAccountRequest): Future[CreateAccountResponse] = {
    val in = CreateAccountInputData()
    val out =
      createAccount
        .execute[CommandAllStack](in)
        .runPure

    out match {
      case Some(x) =>
        Future.successful(
          CreateAccountResponse(
            Some(
              Account(
                id = x.payload.id.value,
                balance = x.payload.balance.value
              )
            )
          )
        )
      case None => Future.failed(new Exception("failed"))
    }
  }

  override def getAccount(in: GetAccountRequest): Future[GetAccountResponse] = ???

  override def depositMoney(req: DepositMoneyRequest): Future[DepositMoneyResponse] = ???

  override def withdrawMoney(in: WithdrawMoneyRequest): Future[WithdrawMoneyResponse] = ???

  override def deleteAccount(in: DeleteAccountRequest): Future[DeleteAccountResponse] = ???
}
