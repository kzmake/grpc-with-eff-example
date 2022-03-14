package usecase.interactor

import domain.account.Account
import domain.base.Repository
import domain.eff.IdGen._idgen
import org.atnos.eff.Eff
import usecase.port.Port
import usecase.port.InputData
import usecase.port.OutputData

class CreateAccountInteractor(
    val accountRepository: Repository[Account]
) extends Port[CreateAccountInputData, CreateAccountOutputData] {
  def execute[R: _idgen](in: CreateAccountInputData): Eff[R, CreateAccountOutputData] = for {
    account <- Account.applyEff[R]
    created <- accountRepository.add[R](account)
  } yield CreateAccountOutputData(payload = created)
}

case class CreateAccountInputData()                  extends InputData
case class CreateAccountOutputData(payload: Account) extends OutputData
