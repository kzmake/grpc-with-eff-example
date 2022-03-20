package domain.eff

import org.atnos.eff.{|=, Eff, Fx}

sealed trait Authz[+A]
case class Allocate(scope: String) extends Authz[Unit]
case class Require(scope: String)  extends Authz[Unit]
case class Authorize()             extends Authz[Boolean]

object Authz {
  type AuthzStack = Fx.fx1[Authz]
  type _authz[R]  = Authz |= R

  def allocate[R: _authz](scope: String): Eff[R, Unit] = Eff.send[Authz, R, Unit](Allocate(scope))
  def require[R: _authz](scope: String): Eff[R, Unit]  = Eff.send[Authz, R, Unit](Require(scope))
  def authorize[R: _authz]: Eff[R, Boolean]            = Eff.send[Authz, R, Boolean](Authorize())
}
