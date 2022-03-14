package domain.eff

import domain.shared.Id
import org.atnos.eff.{|=, Eff, Fx}

sealed trait IdGen[+A]
object IdGen {
  type IdGenStack = Fx.fx1[IdGen]
  type _idgen[R]  = IdGen |= R

  case class Generate[A]() extends IdGen[Id[A]]

  def generate[A, R: _idgen]: Eff[R, Id[A]] = Eff.send[IdGen, R, Id[A]](Generate[A]())
}
