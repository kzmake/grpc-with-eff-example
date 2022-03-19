package domain.eff

import org.atnos.eff.{|=, Eff, Fx}

sealed trait Authz[+A]
case class Allocate(scope: String) extends Authz[Unit]
case class Require(scope: String)  extends Authz[Unit]
case class Authorize()             extends Authz[Boolean]

object Authz {
  type AuthzStack = Fx.fx1[Authz]
  type _authz[R]  = Authz |= R

  // TODO: 課題3: AuthZ(認可)エフェクトの実装 / ドメイン層: 独自エフェクトの定義
  def allocate[R: _authz](scope: String): Eff[R, Unit] = ???
  def require[R: _authz](scope: String): Eff[R, Unit]  = ???
  def authorize[R: _authz]: Eff[R, Boolean]            = ???
}
