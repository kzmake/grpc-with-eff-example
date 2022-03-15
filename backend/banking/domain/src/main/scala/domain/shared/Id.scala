package domain.shared

import domain.base.{ValueObject, ResourceScope}

final case class Id[T](value: String) extends ValueObject with ResourceScope {
  val resourceScope = List(value)
}
