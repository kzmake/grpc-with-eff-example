package adapter.primary.grpc.banking.v1

import api.banking._
import adapter.eff.Stack._
import adapter.secondary.map.MapAuthzOps.AuthzOps
import adapter.secondary.uuid.UUIDIdGenOps.IdGenOps
import akka.grpc.GrpcServiceException
import akka.grpc.scaladsl.Metadata
import org.atnos.eff.syntax.all._
import usecase.interactor._
import usecase.port.Port
import domain.error._
import io.grpc.Status
import scalapb.validate._

import scala.concurrent.Future

class BankingServiceController(
    val createAccountPort: Port[CreateAccountInputData, CreateAccountOutputData],
    val getAccountPort: Port[GetAccountInputData, GetAccountOutputData],
    val depositMoneyPort: Port[DepositMoneyInputData, DepositMoneyOutputData],
    val withdrawMoneyPort: Port[WithdrawMoneyInputData, WithdrawMoneyOutputData],
    val deleteAccountPort: Port[DeleteAccountInputData, DeleteAccountOutputData]
) extends v1.BankingServicePowerApi {
  override def createAccount(req: v1.CreateAccountRequest, metadata: Metadata): Future[v1.CreateAccountResponse] = {
    Validator[v1.CreateAccountRequest].validate(req) match {
      case Success =>
      case Failure(violations) =>
        throw new GrpcServiceException(
          Status.INVALID_ARGUMENT.withDescription(violations.mkString(": "))
        )
    }

    val principal = metadata.getText("principal").getOrElse("none")
    val in        = CreateAccountInputData(principal = principal)

    val out = createAccountPort
      .execute[AStack](in)
      .runAuthz(principal)
      .runIdGen
      .runEither[MyError]
      .run

    out match {
      case Right(v) =>
        Future.successful(
          v1.CreateAccountResponse(
            Some(
              v1.Account(
                id = v.payload.id.value,
                balance = v.payload.balance.value
              )
            )
          )
        )
      case Left(e) =>
        Future.failed(new GrpcServiceException(Status.UNKNOWN.withDescription(e.getMessage)))
    }
  }

  override def getAccount(req: v1.GetAccountRequest, metadata: Metadata): Future[v1.GetAccountResponse] = {
    Validator[v1.GetAccountRequest].validate(req) match {
      case Success =>
      case Failure(violations) =>
        throw new GrpcServiceException(
          Status.INVALID_ARGUMENT.withDescription(violations.mkString(": "))
        )
    }

    val principal = metadata.getText("principal").getOrElse("none")
    val in        = GetAccountInputData(principal = principal, id = req.id)
    val out = getAccountPort
      .execute[AStack](in)
      .runAuthz(principal)
      .runIdGen
      .runEither[MyError]
      .run

    out match {
      case Right(v) =>
        Future.successful(
          v1.GetAccountResponse(
            Some(
              v1.Account(
                id = v.payload.id.value,
                balance = v.payload.balance.value
              )
            )
          )
        )
      case Left(e: NotFoundError) =>
        Future.failed(new GrpcServiceException(Status.NOT_FOUND.withDescription(e.getMessage)))
      case Left(e: UnauthorizedError) =>
        Future.failed(new GrpcServiceException(Status.UNAUTHENTICATED.withDescription(e.getMessage)))
      case Left(e) =>
        Future.failed(new GrpcServiceException(Status.UNKNOWN.withDescription(e.getMessage)))
    }
  }

  override def depositMoney(req: v1.DepositMoneyRequest, metadata: Metadata): Future[v1.DepositMoneyResponse] = {
    Validator[v1.DepositMoneyRequest].validate(req) match {
      case Success =>
      case Failure(violations) =>
        throw new GrpcServiceException(
          Status.INVALID_ARGUMENT.withDescription(violations.mkString(": "))
        )
    }

    val principal = metadata.getText("principal").getOrElse("none")
    val in        = DepositMoneyInputData(principal = principal, id = req.id, money = req.money)
    val out = depositMoneyPort
      .execute[AStack](in)
      .runAuthz(principal)
      .runIdGen
      .runEither[MyError]
      .run

    out match {
      case Right(v) =>
        Future.successful(
          v1.DepositMoneyResponse(
            Some(
              v1.Account(
                id = v.payload.id.value,
                balance = v.payload.balance.value
              )
            )
          )
        )
      case Left(e: NotFoundError) =>
        Future.failed(new GrpcServiceException(Status.NOT_FOUND.withDescription(e.getMessage)))
      case Left(e: UnauthorizedError) =>
        Future.failed(new GrpcServiceException(Status.UNAUTHENTICATED.withDescription(e.getMessage)))
      case Left(e) =>
        Future.failed(new GrpcServiceException(Status.UNKNOWN.withDescription(e.getMessage)))
    }
  }

  override def withdrawMoney(req: v1.WithdrawMoneyRequest, metadata: Metadata): Future[v1.WithdrawMoneyResponse] = {
    Validator[v1.WithdrawMoneyRequest].validate(req) match {
      case Success =>
      case Failure(violations) =>
        throw new GrpcServiceException(
          Status.INVALID_ARGUMENT.withDescription(violations.mkString(": "))
        )
    }

    val principal = metadata.getText("principal").getOrElse("none")
    val in        = WithdrawMoneyInputData(principal = principal, id = req.id, money = req.money)
    val out = withdrawMoneyPort
      .execute[AStack](in)
      .runAuthz(principal)
      .runIdGen
      .runEither[MyError]
      .run

    out match {
      case Right(v) =>
        Future.successful(
          v1.WithdrawMoneyResponse(
            Some(
              v1.Account(
                id = v.payload.id.value,
                balance = v.payload.balance.value
              )
            )
          )
        )
      case Left(e: NotFoundError) =>
        Future.failed(new GrpcServiceException(Status.NOT_FOUND.withDescription(e.getMessage)))
      case Left(e: UnauthorizedError) =>
        Future.failed(new GrpcServiceException(Status.UNAUTHENTICATED.withDescription(e.getMessage)))
      case Left(e) =>
        Future.failed(new GrpcServiceException(Status.UNKNOWN.withDescription(e.getMessage)))
    }
  }

  override def deleteAccount(req: v1.DeleteAccountRequest, metadata: Metadata): Future[v1.DeleteAccountResponse] = {
    Validator[v1.DeleteAccountRequest].validate(req) match {
      case Success =>
      case Failure(violations) =>
        throw new GrpcServiceException(
          Status.INVALID_ARGUMENT.withDescription(violations.mkString(": "))
        )
    }

    val principal = metadata.getText("principal").getOrElse("none")
    val in        = DeleteAccountInputData(principal = principal, id = req.id)
    val out = deleteAccountPort
      .execute[AStack](in)
      .runAuthz(principal)
      .runIdGen
      .runEither[MyError]
      .run

    out match {
      case Right(v) =>
        Future.successful(v1.DeleteAccountResponse())
      case Left(e: NotFoundError) =>
        Future.failed(new GrpcServiceException(Status.NOT_FOUND.withDescription(e.getMessage)))
      case Left(e: UnauthorizedError) =>
        Future.failed(new GrpcServiceException(Status.UNAUTHENTICATED.withDescription(e.getMessage)))
      case Left(e) =>
        Future.failed(new GrpcServiceException(Status.UNKNOWN.withDescription(e.getMessage)))
    }
  }
}
