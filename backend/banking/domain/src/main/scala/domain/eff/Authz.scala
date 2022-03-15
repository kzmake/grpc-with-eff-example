package domain.eff

import org.atnos.eff.{|=, Eff, Fx}

sealed trait Authz[+A]
case class Allocate(principal: String, scopes: Set[String]) extends Authz[Unit]
case class Require(scopes: Set[String])                     extends Authz[Unit]
case class Authorize(principal: String)                     extends Authz[Boolean]

object Authz {
  type AuthzStack = Fx.fx1[Authz]
  type _authz[R]  = Authz |= R

  def allocate[R: _authz](principal: String, scopes: Set[String]): Eff[R, Unit] =
    Eff.send[Authz, R, Unit](Allocate(principal, scopes))
  def require[R: _authz](scopes: Set[String]): Eff[R, Unit]    = Eff.send[Authz, R, Unit](Require(scopes))
  def authorize[R: _authz](principal: String): Eff[R, Boolean] = Eff.send[Authz, R, Boolean](Authorize(principal))
}
