package domain.account

import domain.base.ValueObject

final case class Money(value: Long) extends ValueObject with Ordered[Money] {
  def +(that: Money): Money = Money(this.value + that.value)
  def -(that: Money): Money = Money(this.value - that.value)

  def compare(that: Money): Int = this.value.compare(that.value)
}

object Money {
  val zero: Money = Money(0)
}
