package adapter.primary.grpc.banking.v1

import api.banking.v1._
import adapter.eff.Stack._
import adapter.secondary.uuid.UUIDIdGenOps.IdGenOps
import org.atnos.eff.syntax.all._
import usecase.interactor._
import usecase.port.Port

import scala.concurrent.Future

class BankingServiceController(
    val createAccount: Port[CreateAccountInputData, CreateAccountOutputData],
    val getAccount: Port[GetAccountInputData, GetAccountOutputData]
) extends BankingService {
  override def createAccount(req: CreateAccountRequest): Future[CreateAccountResponse] = {
    val in = CreateAccountInputData()
    val out =
      createAccount
        .execute[AllStack](in)
        .runIdGen
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

  override def getAccount(req: GetAccountRequest): Future[GetAccountResponse] = {
    val in = GetAccountInputData(id = req.id)
    val out =
      getAccount
        .execute[AllStack](in)
        .runPure

    out match {
      case Some(x) =>
        Future.successful(
          GetAccountResponse(
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

  override def depositMoney(req: DepositMoneyRequest): Future[DepositMoneyResponse] = ???

  override def withdrawMoney(req: WithdrawMoneyRequest): Future[WithdrawMoneyResponse] = ???

  override def deleteAccount(req: DeleteAccountRequest): Future[DeleteAccountResponse] = ???
}
